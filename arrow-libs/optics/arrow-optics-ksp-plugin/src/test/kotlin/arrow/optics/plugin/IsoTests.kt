package arrow.optics.plugin

import arrow.optics.plugin.internals.noCompanion
import kotlin.test.Test

class IsoTests {

  @Test
  fun `Isos will be generated for value class`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline
      |value class IsoData(
      |  val field1: String
      |) { companion object }
      |
      |val i: Iso<IsoData, String> = IsoData.field1
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Iso generation requires companion object declaration`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline
      |value class IsoNoCompanion(
      |  val field1: String
      |)
      """.failsWith { it.contains("IsoNoCompanion".noCompanion) }
  }
}
