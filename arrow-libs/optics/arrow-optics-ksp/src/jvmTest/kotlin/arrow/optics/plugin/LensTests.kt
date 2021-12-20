package arrow.optics.plugin

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
      """ {
      "r".source.evalsTo(true)
    }
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
      """ {
      "r".source.evalsTo(true)
    }
  }
}
