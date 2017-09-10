package kategory

import io.kotlintest.TestCase
import io.kotlintest.specs.StringSpec


/**
 * Base class for unit tests
 */
abstract class UnitSpec : StringSpec() {
    
    fun testLaws(laws: List<Law>): List<TestCase> =
        laws.map { law ->
            val tc = TestCase(suite = rootTestSuite, name = law.name, test = law.test, config = defaultTestCaseConfig)
            rootTestSuite.addTestCase(tc)
            tc
        }

}
