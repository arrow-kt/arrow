package arrow.optics.plugin

import kotlin.test.Test

class IsoTests {

  @Test
  fun `companion iso is generated for a value class`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |@JvmInline
    |value class Cents(val value: Int) {
    |  companion object
    |}
    |
    |val iso: Iso<Cents, Int> = Cents.value
    |val r = iso.get(Cents(3)) == 3 && iso.reverseGet(5) == Cents(5)
    """.evals("r" to true)
  }

  @Test
  fun `generic iso for a value class`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |@JvmInline
    |value class Wrapper<T>(val wrapped: T) {
    |  companion object
    |}
    |
    |val iso: Iso<Wrapper<String>, String> = Wrapper.wrapped<String>()
    |val r = iso.get(Wrapper("hi")) == "hi" && iso.reverseGet("bye") == Wrapper("bye")
    """.evals("r" to true)
  }
}
