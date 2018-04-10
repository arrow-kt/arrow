package arrow.test

import arrow.test.laws.Law
import arrow.typeclasses.Eq
import io.kotlintest.Description
import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec : StringSpec() {

  fun testLaws(vararg laws: List<Law>) {
    val flattened = laws.flatMap { list: List<Law> -> list.asIterable() }
    val distinct = flattened.distinctBy { law: Law -> law.name }
//    return distinct.forEach { law: Law ->
////      val tc = TestCase(suite = rootTestSuite, name = law.name, test = law.test, config = defaultTestCaseConfig)
////      rootTestSuite.addTestCase(tc)
////      tc
//        val tc = TestCase(Description(emptyList(), law.name),this@UnitSpec, { law.test()} , 0, defaultTestCaseConfig)
//    }
      distinct.forEach { law ->
        law.name {
          law.test()
        }
      }
  }

  inline fun <F> Eq<F>.logged(): Eq<F> = Eq { a, b ->
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
