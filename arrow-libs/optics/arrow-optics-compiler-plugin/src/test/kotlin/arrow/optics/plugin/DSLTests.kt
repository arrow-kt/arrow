package arrow.optics.plugin

import kotlin.test.Test

class DSLTests {

  @Test
  fun `lens DSL composes nested data classes`() {
    """
    |import arrow.optics.*
    |
    |@optics data class Street(val number: Int, val name: String) { companion object }
    |@optics data class Address(val city: String, val street: Street) { companion object }
    |@optics data class Company(val name: String, val address: Address) { companion object }
    |
    |val streetName: Lens<Company, String> = Company.address.street.name
    |val c = Company("ACME", Address("AMS", Street(1, "Main")))
    |val r = streetName.get(c) == "Main" &&
    |        streetName.set(c, "Side").address.street.name == "Side"
    """.evals("r" to true)
  }

  @Test
  fun `prism DSL composes through a sealed branch`() {
    """
    |import arrow.optics.*
    |
    |@optics data class Inner(val value: Int) { companion object }
    |@optics data class Wrapper(val inner: Inner) : Thing { companion object }
    |@optics sealed interface Thing {
    |  companion object
    |}
    |
    |val opt: Optional<Thing, Int> = Thing.wrapper.inner.value
    |val r = opt.getOrNull(Wrapper(Inner(7))) == 7
    """.evals("r" to true)
  }
}
