package arrow.optics.plugin

import arrow.optics.plugin.internals.typeParametersErrorMessage
import org.junit.jupiter.api.Test

class LensTests {

  @Test
  fun `Lenses will be generated for data class`() {
    """
      |$imports
      |@optics
      |data class LensData(
      |  val field1: String
      |) { companion object }
      |
      |val i: Lens<LensData, String> = LensData.field1
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses will be generated for data class with secondary constructors`() {
    """
      |$imports
      |@optics
      |data class LensesSecondaryConstructor(val fieldNumber: Int, val fieldString: String) {
      |  constructor(number: Int) : this(number, number.toString())
      |  companion object
      |}
      |
      |val i: Lens<LensesSecondaryConstructor, String> = LensesSecondaryConstructor.fieldString
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses which mentions imported elements`() {
    """
      |$imports
      |
      |@optics
      |data class OpticsTest(val time: kotlin.time.Duration) {
      |  companion object
      |}
      |
      |val i: Lens<OpticsTest, kotlin.time.Duration> = OpticsTest.time
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Lenses which mentions type arguments`() {
    """
      |$imports
      |@optics
      |data class OpticsTest<A>(val field: A) {
      |  companion object
      |}
      |
      |val i: Lens<OpticsTest, Int> = OpticsTest<Int>.time
      |val r = i != null
      """.failsWith { it.contains("OpticsTest".typeParametersErrorMessage) }
  }
}
