package arrow.test

import arrow.test.laws.Law
import arrow.typeclasses.Eq
import io.kotlintest.TestCase
import io.kotlintest.TestType
import io.kotlintest.specs.AbstractStringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec : AbstractStringSpec() {

  fun testLaws(vararg laws: List<Law>): List<TestCase> {
   laws
     .flatMap { list: List<Law> -> list.asIterable() }
     .distinctBy { law: Law -> law.name }
     .map { law: Law ->
       createTestCase(law.name, law.test, defaultTestCaseConfig, TestType.Test)
     }
   return testCases()
  }


  fun <F> Eq<F>.logged(): Eq<F> = Eq { a, b ->
    try {
      val result = a.eqv(b)
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
