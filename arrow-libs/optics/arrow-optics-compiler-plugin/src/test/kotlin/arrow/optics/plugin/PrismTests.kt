package arrow.optics.plugin

import kotlin.test.Test

class PrismTests {

  @Test
  fun `companion prisms are generated for a sealed class`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |sealed class PrismSealed {
    |  data class PrismSealed1(val a: String?) : PrismSealed()
    |  data class PrismSealed2(val b: String?) : PrismSealed()
    |  companion object
    |}
    |
    |val p1: Prism<PrismSealed, PrismSealed.PrismSealed1> = PrismSealed.prismSealed1
    |val one: PrismSealed = PrismSealed.PrismSealed1("x")
    |val two: PrismSealed = PrismSealed.PrismSealed2("y")
    |val r = p1.getOrNull(one) == PrismSealed.PrismSealed1("x") &&
    |        p1.getOrNull(two) == null
    """.evals("r" to true)
  }

  @Test
  fun `prism for sealed interface with lowercased keyword name`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |sealed interface Thing {
    |  data class Object(val value: Int) : Thing
    |  companion object
    |}
    |
    |val prism: Prism<Thing, Thing.Object> = Thing.`object`
    |val r = prism.getOrNull(Thing.Object(3)) == Thing.Object(3)
    """.evals("r" to true)
  }
}
