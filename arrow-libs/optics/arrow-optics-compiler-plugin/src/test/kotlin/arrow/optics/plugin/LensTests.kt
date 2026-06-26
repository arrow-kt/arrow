package arrow.optics.plugin

import kotlin.test.Test

class LensTests {

  @Test
  fun `companion lens is generated for a monomorphic data class`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |data class LensData(val field1: String) {
    |  companion object
    |}
    |
    |val lens: Lens<LensData, String> = LensData.field1
    |val r = lens.get(LensData("hello")) == "hello" &&
    |        lens.set(LensData("hello"), "world") == LensData("world")
    """.evals("r" to true)
  }

  @Test
  fun `lens generated without an explicit companion object`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |data class LensData(val field1: String)
    |
    |val r = LensData.field1.get(LensData("hello")) == "hello"
    """.evals("r" to true)
  }

  @Test
  fun `generic data class produces a lens function`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |data class OpticsTest<A>(val field: A) {
    |  companion object
    |}
    |
    |val lens: Lens<OpticsTest<String>, String> = OpticsTest.field<String>()
    |val r = lens.get(OpticsTest("x")) == "x" &&
    |        lens.set(OpticsTest("x"), "y") == OpticsTest("y")
    """.evals("r" to true)
  }

  @Test
  fun `nullable focus keeps nullability`() {
    """
    |import arrow.optics.*
    |import arrow.optics.dsl.*
    |
    |@optics
    |data class OptionalData(val field1: String?) {
    |  companion object
    |}
    |
    |val lens: Lens<OptionalData, String?> = OptionalData.field1
    |val opt: Optional<OptionalData, String> = OptionalData.field1.notNull
    |val r = lens.get(OptionalData(null)) == null &&
    |        opt.getOrNull(OptionalData("x")) == "x"
    """.evals("r" to true)
  }

  @Test
  fun `shared abstract property of a sealed class produces a lens`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |sealed class LensSealed {
    |  abstract val property1: String
    |  data class Child1(override val property1: String) : LensSealed()
    |  data class Child2(override val property1: String, val n: Int) : LensSealed()
    |  companion object
    |}
    |
    |val lens: Lens<LensSealed, String> = LensSealed.property1
    |val c1: LensSealed = LensSealed.Child1("a")
    |val c2: LensSealed = LensSealed.Child2("b", 5)
    |val r = lens.get(c1) == "a" &&
    |        lens.set(c1, "z") == LensSealed.Child1("z") &&
    |        lens.set(c2, "z") == LensSealed.Child2("z", 5)
    """.evals("r" to true)
  }

  @Test
  fun `multiple fields each produce a lens`() {
    """
    |import arrow.optics.*
    |
    |@optics
    |data class Person(val name: String, val age: Int) {
    |  companion object
    |}
    |
    |val p = Person("Alejandro", 40)
    |val r = Person.name.get(p) == "Alejandro" &&
    |        Person.age.set(p, 41) == Person("Alejandro", 41)
    """.evals("r" to true)
  }
}
