package arrow.optics.plugin

import kotlin.test.Ignore
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
  fun `Isos will be generated for value class with parameters having keywords as names`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline
      |value class IsoData(
      |  val `in`: String
      |) { companion object }
      """.compilationSucceeds()
  }

  @Test
  @Ignore("Needs fixing joinedTypeParams in processIsoSyntax function")
  fun `Isos will be generated for generic value class with parameters having keywords as names`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline
      |value class IsoData<T>(
      |  val `in`: T
      |) { companion object }
      """.compilationSucceeds()
  }

  // In the compiler plugin the companion object is generated automatically when missing,
  // so a value class without a companion is now valid (unlike the KSP processor).
  @Test
  fun `Iso generation works without an explicit companion object`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline
      |value class IsoNoCompanion(
      |  val field1: String
      |)
      |
      |val i: Iso<IsoNoCompanion, String> = IsoNoCompanion.field1
      |val r = i != null
      """.evals("r" to true)
  }
}
