package arrow.optics.plugin

import org.junit.jupiter.api.Test

class OptionalTests {

  @Test
  fun `Optional will be generated for data class`() {
    """
      |$imports
      |@optics
      |data class OptionalData(
      |  val field1: String?
      |) { companion object }
      |
      |val i: Optional<OptionalData, String> = OptionalData.field1
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Optional will be generated for generic data class`() {
    """
      |$imports
      |@optics
      |data class OptionalData<A>(
      |  val field1: A?
      |) { companion object }
      |
      |val i: Optional<OptionalData<String>, String> = OptionalData.field1()
      |val r = i != null
      """.evals("r" to true)
  }

  @Test
  fun `Optional will be generated for data class with secondary constructors`() {
    """
      |$imports
      |@optics
      |data class OptionalSecondaryConstructor(val fieldNumber: Int?, val fieldString: String?) {
      |  constructor(number: Int?) : this(number, number?.toString())
      |  companion object
      |}
      |
      |val i: Optional<OptionalSecondaryConstructor, String> = OptionalSecondaryConstructor.fieldString
      |val r = i != null
      """.evals("r" to true)
  }
}
