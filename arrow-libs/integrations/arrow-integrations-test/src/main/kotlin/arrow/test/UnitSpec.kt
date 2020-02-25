package arrow.test

import io.kotlintest.TestCase
import io.kotlintest.specs.AbstractStringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec : AbstractStringSpec() {

  private val lawTestCases = mutableListOf<TestCase>()

  override fun testCases(): List<TestCase> = super.testCases() + lawTestCases
}
