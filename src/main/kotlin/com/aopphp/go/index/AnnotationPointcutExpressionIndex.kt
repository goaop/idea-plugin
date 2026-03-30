package com.aopphp.go.index

import com.aopphp.go.pointcut.Pointcut
import com.aopphp.go.psi.PointcutElementFactory
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import com.intellij.util.indexing.DataIndexer
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
import com.jetbrains.php.lang.psi.elements.PhpClassMember
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex
import com.intellij.psi.util.PsiTreeUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInput
import java.io.DataOutput
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Indexes pointcut expressions from #[\Go\Lang\Annotation\*] PHP 8 attributes.
 * Key:   FQN of the aspect member (e.g. \MyAspect.beforeMethod)
 * Value: compiled Pointcut object
 */
class AnnotationPointcutExpressionIndex : FileBasedIndexExtension<String, Pointcut>() {

    companion object {
        @JvmField
        val KEY: ID<String, Pointcut> = ID.create("com.aopphp.go.annotation.pointcuts")
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
                is Function -> visitElement(element, result)
                is PhpClass -> {
                    PsiTreeUtil.getChildrenOfTypeAsList(element, PhpClassMember::class.java)
                        .forEach { visitElement(it, result) }
                }
            }
        }

        return result
    }

    private fun visitElement(element: PhpNamedElement, map: MutableMap<String, Pointcut>) {
        if (element !is PhpAttributesOwner) return
        for (attr in element.attributes) {
            val fqn = attr.fqn ?: continue
            if (!fqn.startsWith("\\Go\\Lang\\Annotation\\")) continue

            val stringArg = PsiTreeUtil.findChildOfType(attr, StringLiteralExpression::class.java)
                ?: continue
            val expressionText = StringUtil.trimEnd(stringArg.text, "\"").removePrefix("\"")

            val pointcutExpression = PointcutElementFactory.createPointcut(attr.project, expressionText)
                ?: continue

            val elementFqn = element.fqn ?: continue
            map[elementFqn] = pointcutExpression.compile()
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

    override fun getInputFilter() = PhpConstantNameIndex.PHP_INPUT_FILTER

    override fun dependsOnFileContent() = true

    override fun getVersion() = 3
}
