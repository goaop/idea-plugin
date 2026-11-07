package com.aopphp.go.pointcut

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpNamedElement

/**
 * Tests the navigation between advices and advised elements through the pointcut index.
 */
class PointcutAdvisorTest : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            """
            <?php
            namespace App {
                interface Countable {}
                trait CountableTrait {}

                class Foo {
                    public function bar() {}
                    public function baz() {}
                }
            }

            namespace App\Aspect {
                use Go\Lang\Attribute\Before;
                use Go\Lang\Attribute\DeclareParents;
                use Go\Lang\Attribute\Pointcut;

                abstract class BaseAspect implements \Go\Aop\Aspect {
                    #[Pointcut('execution(public App\Foo->bar(*))')]
                    protected function fooBar(): void {}
                }

                class ChildAspect extends BaseAspect {
                    #[Before('${'$'}this->fooBar')]
                    public function beforeInheritedPointcut(): void {}
                }

                class OtherAspect implements \Go\Aop\Aspect {
                    #[Before('App\Aspect\BaseAspect->fooBar || execution(public App\Foo->baz(*))')]
                    public function beforeQualifiedPointcut(): void {}

                    #[Before('initialization(App\Foo)')]
                    public function beforeNew(): void {}

                    #[DeclareParents('within(App\Foo)', interfaceName: \App\Countable::class, traitName: \App\CountableTrait::class)]
                    protected ${'$'}countable;
                }
            }
            """.trimIndent()
        )
    }

    private fun phpClass(fqn: String): PhpClass = PhpIndex.getInstance(project).getClassesByFQN(fqn).single()

    private fun names(elements: List<PhpNamedElement>?) = elements.orEmpty().map { it.name }.toSet()

    fun testInheritedSelfPointcutReference() {
        val advice = phpClass("\\App\\Aspect\\ChildAspect").findOwnMethodByName("beforeInheritedPointcut")!!
        assertEquals(setOf("bar"), names(PointcutAdvisor.getMatchedElements(advice)))
    }

    fun testQualifiedPointcutReference() {
        val advice = phpClass("\\App\\Aspect\\OtherAspect").findOwnMethodByName("beforeQualifiedPointcut")!!
        assertEquals(setOf("bar", "baz"), names(PointcutAdvisor.getMatchedElements(advice)))
    }

    fun testInitializationSelectsTheClass() {
        val advice = phpClass("\\App\\Aspect\\OtherAspect").findOwnMethodByName("beforeNew")!!
        assertEquals(setOf("Foo"), names(PointcutAdvisor.getMatchedElements(advice)))
    }

    fun testIntroductionSelectsTheClass() {
        val introduction = phpClass("\\App\\Aspect\\OtherAspect").findOwnFieldByName("countable", false)!!
        assertEquals(setOf("Foo"), names(PointcutAdvisor.getMatchedElements(introduction)))
    }

    fun testClassIsAdvisedByIntroductionAndInitialization() {
        val advices = names(PointcutAdvisor.getMatchedAdvices(phpClass("\\App\\Foo")))
        assertTrue(advices.toString(), "countable" in advices)
        assertTrue(advices.toString(), "beforeNew" in advices)
    }

    fun testMethodIsAdvisedThroughReferences() {
        val bar = phpClass("\\App\\Foo").findOwnMethodByName("bar")!!
        val advices = names(PointcutAdvisor.getMatchedAdvices(bar))
        assertTrue(advices.toString(), "beforeInheritedPointcut" in advices)
        assertTrue(advices.toString(), "beforeQualifiedPointcut" in advices)
    }
}
