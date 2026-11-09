package com.aopphp.go.index

import com.aopphp.go.pointcut.IntroductionPointcut
import com.aopphp.go.pointcut.Pointcut
import com.aopphp.go.pointcut.PointcutCompiler
import com.aopphp.go.psiutil.PointcutElementFactory
import com.aopphp.go.util.GoAopAttributes
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.Function
import com.jetbrains.php.lang.psi.elements.PhpAttributesOwner
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInput
import java.io.DataOutput
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Indexes pointcut expressions from #[\Go\Lang\Attribute\*] PHP 8 attributes.
 * Key:   FQN of the aspect member (e.g. \MyAspect.beforeMethod)
 * Value: compiled Pointcut object
 *
 * `$this->memberName` references inside pointcut expressions are resolved at index
 * time: the expression of the referenced `#[\Go\Lang\Attribute\Pointcut]`-annotated
 * member in the same class is substituted inline before compilation.
 */
class AttributePointcutExpressionIndex : FileBasedIndexExtension<String, Pointcut>() {

    companion object {
        @JvmField
        val KEY: ID<String, Pointcut> = ID.create("com.aopphp.go.attribute.pointcuts")

    }

    override fun getName() = KEY

    override fun getIndexer() = DataIndexer<String, Pointcut, FileContent> { inputData ->
        val psiFile = inputData.psiFile
        if (psiFile !is PhpFile) return@DataIndexer emptyMap()
        buildMap(psiFile)
    }

    private fun buildMap(phpFile: PhpFile): Map<String, Pointcut> {
        val result = mutableMapOf<String, Pointcut>()

        for (element in phpFile.topLevelDefs.values()) {
            when (element) {
                is Function -> visitElement(element, null, emptyMap(), result)
                is PhpClass -> {
                    // Pre-build the map of memberName -> raw expression for all
                    // #[\Go\Lang\Attribute\Pointcut]-annotated members in this class.
                    // This avoids unsafe PSI navigation (findMethodByName etc.) inside
                    // the replace callback below, which can trigger index access.
                    val selfPointcutMap = buildSelfPointcutMap(element)
                    element.ownMethods.forEach { visitElement(it, element.fqn, selfPointcutMap, result) }
                    element.ownFields.forEach { visitElement(it, element.fqn, selfPointcutMap, result) }
                }
            }
        }

        return result
    }

    /**
     * Collects the raw pointcut expression string for every member in [phpClass]
     * that carries `#[\Go\Lang\Attribute\Pointcut]`.
     * Result: memberName → expression (e.g. `"myPointcut" → "execution(public **->*(*))"`).
     */
    private fun buildSelfPointcutMap(phpClass: PhpClass): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (method in phpClass.ownMethods) {
            for (attr in method.attributes) {
                if (attr.fqn != GoAopAttributes.POINTCUT) continue
                val stringArg = GoAopAttributes.expressionArgument(attr) ?: continue
                map[method.name] = stringArg.contents
            }
        }
        return map
    }

    private fun visitElement(
        element: PhpNamedElement,
        aspectClassFqn: String?,
        selfPointcutMap: Map<String, String>,
        map: MutableMap<String, Pointcut>
    ) {
        if (element !is PhpAttributesOwner) return
        for (attr in element.attributes) {
            val stringArg = GoAopAttributes.expressionArgument(attr) ?: continue

            val expressionText = resolveThisReferences(stringArg.contents, selfPointcutMap)

            val pointcutExpression = PointcutElementFactory.createPointcut(attr.project, expressionText)
                ?: continue

            val pointcut = PointcutCompiler.compile(pointcutExpression, aspectClassFqn)
            // A #[Pointcut] next to an advice attribute on the same method is kept by the advice
            if (attr.fqn == GoAopAttributes.POINTCUT && map.containsKey(element.fqn)) continue
            map[element.fqn] = if (attr.fqn == GoAopAttributes.DECLARE_PARENTS) IntroductionPointcut(pointcut) else pointcut
        }
    }

    /**
     * Replaces every `$this->memberName` occurrence in [expression] with the actual
     * pointcut expression from [selfPointcutMap], wrapped in parentheses.
     * Recursion up to depth 10 handles chained self-references.
     */
    private fun resolveThisReferences(
        expression: String,
        selfPointcutMap: Map<String, String>,
        depth: Int = 0
    ): String {
        if (depth > 10 || selfPointcutMap.isEmpty()) return expression
        return expression.replace(Regex("\\\$this->([a-zA-Z_][a-zA-Z0-9_]*)")) { matchResult ->
            val memberName = matchResult.groupValues[1]
            val referencedExpr = selfPointcutMap[memberName] ?: return@replace matchResult.value
            val inner = resolveThisReferences(referencedExpr, selfPointcutMap, depth + 1)
            "($inner)"
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer() = object : DataExternalizer<Pointcut> {
        override fun save(out: DataOutput, value: Pointcut) {
            val bytes = ByteArrayOutputStream()
            ObjectOutputStream(bytes).use { it.writeObject(value) }
            out.writeInt(bytes.size())
            out.write(bytes.toByteArray())
        }

        override fun read(input: DataInput): Pointcut? {
            val buffer = ByteArray(input.readInt()).also { input.readFully(it) }
            return try {
                ObjectInputStream(ByteArrayInputStream(buffer)).use { it.readObject() as Pointcut }
            } catch (_: ClassNotFoundException) {
                null
            }
        }
    }

    override fun getInputFilter(): FileBasedIndex.InputFilter = PhpConstantNameIndex.PHP_INPUT_FILTER

    override fun dependsOnFileContent() = true

    override fun getVersion() = 11
}