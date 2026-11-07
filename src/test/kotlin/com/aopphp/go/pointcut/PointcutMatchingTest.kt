package com.aopphp.go.pointcut

import com.aopphp.go.psiutil.PointcutElementFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.Method
import com.jetbrains.php.lang.psi.elements.PhpClass

/**
 * End-to-end matching of compiled pointcuts against real PHP PSI, following the Go! AOP 4.0 semantics.
 */
class PointcutMatchingTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
            <?php
            namespace App;

            #[\Attribute(\Attribute::TARGET_METHOD)]
            class Cacheable {}

            #[\Attribute(\Attribute::TARGET_METHOD)]
            class ShortCacheable extends Cacheable {}

            class Entity {}

            interface Repository {}

            trait LoggerTrait {
                public function log() {}
            }

            class Base {
                use LoggerTrait;
                public function base() {}
            }

            class Child extends Base implements Repository {}

            final class Service {
                final public function finalPublic() {}
                final protected function finalProtected() {}
                public function plainPublic() {}
                final private function finalPrivate() {}
                public function &byReference() {}
                public function find(): ?Entity {}
                public function findAll(): array {}
                public function set() {}
                public function setName() {}
                #[ShortCacheable]
                public function cached() {}
            }

            class Model {
                public private(set) string ${'$'}id;
                public readonly string ${'$'}name;
                public string ${'$'}title;
                public static string ${'$'}shared;
                public string ${'$'}hooked { get => 'value'; }
                public const VERSION = 1;
            }

            class Cart {}
            class Order {}
            """.trimIndent()
        )
    }

    private fun compile(expression: String): Pointcut {
        val psi = PointcutElementFactory.createPointcut(project, expression)
        assertNotNull("Expression does not parse: $expression", psi)
        return PointcutCompiler.compile(psi!!)
    }

    /** Returns the matched elements as `Class`, `Class::method` or `Class::$property` */
    private fun matched(expression: String): Set<String> {
        val pointcut = compile(expression)
        val result = mutableSetOf<String>()
        for (phpClass in PsiTreeUtil.findChildrenOfType(myFixture.file, PhpClass::class.java)) {
            if (PointcutAdvisor.isPointcutMatches(phpClass, pointcut)) result.add(phpClass.name)
            phpClass.ownMethods.filter { PointcutAdvisor.isPointcutMatches(it, pointcut) }
                .forEach { result.add("${phpClass.name}::${it.name}") }
            phpClass.ownFields.filter { PointcutAdvisor.isPointcutMatches(it, pointcut) }
                .forEach { result.add("${phpClass.name}::\$${it.name}") }
        }
        return result
    }

    fun testSpaceSeparatedModifiersMustAllBePresent() {
        assertEquals(
            setOf("Service::finalPublic", "Service::finalProtected"),
            matched("execution(final public|protected App\\Service->*(*))")
        )
    }

    fun testStarMatchesAnEmptySuffix() {
        assertEquals(setOf("Service::set", "Service::setName"), matched("execution(public App\\Service->set*(*))"))
    }

    fun testNullableReturnType() {
        assertEquals(setOf("Service::find"), matched("execution(public App\\Service->*(*): ?App\\Entity)"))
        assertEquals(setOf("Service::find"), matched("execution(public App\\Service->*(*): App\\Entity|null)"))
        assertEquals(setOf("Service::findAll"), matched("execution(public App\\Service->*(*): array)"))
    }

    fun testMatchReturningByReference() {
        assertEquals(setOf("Service::byReference"), matched("execution(public App\\Service->*(*)) && matchReturningByReference()"))
        assertFalse(
            "Service::byReference" in matched("execution(public App\\Service->*(*)) && !matchReturningByReference()")
        )
    }

    fun testNotMatchInheritedKeepsDeclaredMembers() {
        assertTrue("Service::plainPublic" in matched("execution(public App\\Service->*(*)) && !matchInherited()"))
    }

    fun testPropertyModifiersOfPhp84() {
        assertEquals(setOf("Model::\$id"), matched("access(public private(set) App\\Model->*)"))
    }

    fun testPropertiesThatAreNeverIntercepted() {
        // Readonly, static and hooked properties and class constants are never intercepted by Go! AOP 4.0
        assertEquals(emptySet<String>(), matched("access(public readonly App\\Model->*)"))
        assertEquals(setOf("Model::\$id", "Model::\$title"), matched("access(public App\\Model->*)"))
    }

    fun testInheritancePatternIncludesTheTypeAndTraitUsers() {
        val classes = matched("initialization(App\\Base+)")
        assertEquals(setOf("Base", "Child"), classes)
        assertEquals(setOf("Base", "Child"), matched("initialization(App\\LoggerTrait+)"))
        assertEquals(setOf("Child"), matched("initialization(App\\Repository+)"))
    }

    fun testAlternativesStayInsideTheirNamePart() {
        assertEquals(setOf("Cart", "Order"), matched("initialization(App\\Cart|Order)"))
    }

    fun testAttributePointcutMatchesAttributeSubclasses() {
        assertEquals(setOf("Service::cached"), matched("@execution(App\\Cacheable)"))
    }

    fun testWithinAndNegation() {
        val members = matched("within(App\\Model) && !access(public App\\Model->title)")
        assertTrue("Model::\$id" in members)
        assertFalse("Model::\$title" in members)
    }

    fun testRemovedPointcutsMatchNothing() {
        assertTrue(matched("dynamic(public App\\Service->*(*))").isEmpty())
    }

    fun testCompiledPointcutIsSerializable() {
        // Compiled pointcuts are stored in a file-based index with Java serialization
        val pointcut = compile(
            "execution(final public|protected App\\**->*(*): ?App\\Entity) && !matchReturningByReference() " +
                "|| access(public private(set) App\\Model->*) || @within(App\\Cacheable) || App\\Aspect->named"
        )
        val bytes = java.io.ByteArrayOutputStream()
        java.io.ObjectOutputStream(bytes).use { it.writeObject(pointcut) }
        val restored = java.io.ObjectInputStream(java.io.ByteArrayInputStream(bytes.toByteArray())).use { it.readObject() }
        assertEquals(pointcut, restored)
    }

    fun testMethodsOfTheFixtureAreParsed() {
        val methods = PsiTreeUtil.findChildrenOfType(myFixture.file, Method::class.java).map { it.name }
        assertTrue("byReference" in methods)
    }
}
