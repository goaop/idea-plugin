package com.aopphp.go.index

import com.intellij.psi.PsiFile
import com.intellij.util.ReflectionUtil
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
import com.jetbrains.php.lang.psi.elements.PhpNamedElement
import com.jetbrains.php.lang.psi.stubs.indexes.PhpConstantNameIndex
import com.jetbrains.php.lang.psi.stubs.indexes.StringSetDataExternalizer

/**
 * Indexes PHP elements (classes, methods, functions) that have PHP 8 Attributes applied.
 * Key:   attribute class FQN (e.g. \Go\Lang\Attribute\Before)
 * Value: set of FQNs of elements decorated with that attribute
 */
class AttributePhpNamedElementIndex : FileBasedIndexExtension<String, Set<String>>() {

    companion object {
        @JvmField
        val KEY: ID<String, Set<String>> = ID.create("com.aopphp.go.attributed.elements")

        private val EXTERNALIZER: StringSetDataExternalizer =
            ReflectionUtil.newInstance(StringSetDataExternalizer::class.java)
    }

    override fun getName() = KEY

    override fun getIndexer() = DataIndexer<String, Set<String>, FileContent> { inputData ->
        val psiFile = inputData.psiFile
        if (psiFile !is PhpFile) return@DataIndexer emptyMap()
        buildMap(psiFile)
    }

    private fun buildMap(phpFile: PhpFile): Map<String, Set<String>> {
        val result = mutableMapOf<String, MutableSet<String>>()

        for (element in phpFile.topLevelDefs.values()) {
            when (element) {
                is Function -> visitElement(element, result)
                is PhpClass -> {
                    visitElement(element, result)
                    element.ownMethods.forEach { visitElement(it, result) }
                    element.ownFields.forEach { visitElement(it, result) }
                }
            }
        }

        return result
    }

    private fun visitElement(element: PhpNamedElement, map: MutableMap<String, MutableSet<String>>) {
        if (element !is PhpAttributesOwner) return
        val elementFqn = element.fqn ?: return

        for (attr in element.attributes) {
            val attrFqn = attr.fqn ?: continue
            map.getOrPut(attrFqn) { mutableSetOf() }.add(elementFqn)
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<Set<String>> = EXTERNALIZER

    override fun getInputFilter() = PhpConstantNameIndex.PHP_INPUT_FILTER

    override fun dependsOnFileContent() = true

    override fun getVersion() = 5
}
