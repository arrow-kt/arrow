package arrow.optics.plugin

import arrow.optics.plugin.internals.isoTooBigErrorMessage
import arrow.optics.plugin.internals.noCompanion
import org.junit.jupiter.api.Test

class IsoTests {

  @Test
  fun `Isos will be generated for data class`() {
    """
      |$imports
      |@optics
      |data class IsoData(
      |  val field1: String
      |) { companion object }
      |
      |val i: Iso<IsoData, String> = IsoData.iso
      |val r = i != null
      """ {
      "r".source.evalsTo(true)
    }
  }

  @Test
  fun `Isos will be generated for data class with secondary constructors`() {
    """
      |$imports
      |@optics
      |data class IsoSecondaryConstructor(val fieldNumber: Int, val fieldString: String) {
      |  constructor(number: Int) : this(number, number.toString())
      |  companion object
      |}
      |
      |val i: Iso<IsoSecondaryConstructor, Pair<Int, String>> = IsoSecondaryConstructor.iso
      |val r = i != null
      """ {
      "r".source.evalsTo(true)
    }
  }

  @Test
  fun `Iso generation requires companion object declaration`() {
    """
      |$imports
      |@optics
      |data class IsoNoCompanion(
      |  val field1: String
      |)
      """ {
      failsWith { it.contains("IsoNoCompanion".noCompanion) }
    }
  }

  @Test
  fun `Isos cannot be generated for huge classes`() {
    """
      |$imports
      |@optics
      |data class IsoXXL(
      |  val field1: String,
      |  val field2: String,
      |  val field3: String,
      |  val field4: String,
      |  val field5: String,
      |  val field6: String,
      |  val field7: String,
      |  val field8: String,
      |  val field9: String,
      |  val field10: String,
      |  val field11: String,
      |  val field12: String,
      |  val field13: String,
      |  val field14: String,
      |  val field15: String,
      |  val field16: String,
      |  val field17: String,
      |  val field18: String,
      |  val field19: String,
      |  val field20: String,
      |  val field21: String,
      |  val field22: String,
      |  val field23: String
      |) {
      |  companion object
      |}
      """ {
      failsWith { it.contains("IsoXXL".isoTooBigErrorMessage) }
    }
  }
}
