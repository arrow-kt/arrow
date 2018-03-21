package arrow.test

import arrow.typeclasses.Eq
import arrow.test.laws.Law
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec : StringSpec() {
    
    fun testLaws(vararg laws: List<Law>): List<TestCase>{
        val flattened = laws.flatMap { list: List<Law> ->  list.asIterable() }
        val distinct = flattened.distinctBy { law: Law -> law.name }
        return distinct.map { law: Law ->
            val tc = TestCase(suite = rootTestSuite, name = law.name, test = law.test, config = defaultTestCaseConfig)
            rootTestSuite.addTestCase(tc)
            tc
        }
    }

    inline fun <F> Eq<F>.logged(): Eq<F> = Eq { a, b ->
        try {
            val result = this@logged.eqv(a, b)
            if (!result) {
                println("$a <---> $b")
            }
            result
        } catch (t: Throwable) {
            println("EXCEPTION: ${t.message}")
            println("$a <---> $b")
            false
        }
    }
}
