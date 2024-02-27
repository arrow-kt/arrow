package arrow.optics.plugin

import kotlin.test.Test

class OptionalTests {

  @Test
  fun `Optional will be generated for data class`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class OptionalData(
      |  val field1: String?
      |) { companion object }
      |
      |val i: Lens<OptionalData, String?> = OptionalData.field1
      |val j: Optional<OptionalData, String> = OptionalData.field1.notNull
      |val r = i != null && j != null
      """.evals("r" to true)
  }

  @Test
  fun `Optional will be generated for generic data class`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class OptionalData<A>(
      |  val field1: A?
      |) { companion object }
      |
      |val i: Lens<OptionalData<String>, String?> = OptionalData.field1()
      |val j: Optional<OptionalData<String>, String> = OptionalData.field1<String>().notNull
      |val r = i != null && j != null
      """.evals("r" to true)
  }

  @Test
  fun `Optional will be generated for data class with secondary constructors`() {
    """
      |$`package`
      |$imports
      |@optics
      |data class OptionalSecondaryConstructor(val fieldNumber: Int?, val fieldString: String?) {
      |  constructor(number: Int?) : this(number, number?.toString())
      |  companion object
      |}
      |
      |val i: Lens<OptionalSecondaryConstructor, String?> = OptionalSecondaryConstructor.fieldString
      |val j: Optional<OptionalSecondaryConstructor, String> = OptionalSecondaryConstructor.fieldString.notNull
      |val r = i != null && j != null
      """.evals("r" to true)
  }
}
