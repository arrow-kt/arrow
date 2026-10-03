package arrow.optics.plugin

import kotlin.test.Test

/**
 * The Optics plug-in used alongside kotlinx.serialization, which queries generated names very early.
 */
class SerializationTests {
  @Test
  fun `enum next to an optics sealed interface with a data object`() {
    """
      |$`package`
      |
      |$imports
      |
      |enum class SomeEnumType { Foo, Bar, Baz }
      |
      |@optics
      |sealed interface CacheResult {
      |  data object NotSet : CacheResult
      |  @optics data class Success(val workouts: List<String>) : CacheResult
      |}
      |
      |val i: Prism<CacheResult, CacheResult.Success> = CacheResult.success
      |val j: Prism<CacheResult, CacheResult.NotSet> = CacheResult.notSet
      """.compilationSucceedsWithSerialization()
  }

  @Test
  fun `enum next to an optics sealed interface with a shared abstract property`() {
    """
      |$`package`
      |
      |$imports
      |
      |enum class SomeEnumType { Foo, Bar, Baz }
      |
      |@optics
      |sealed interface Shape {
      |  val id: String
      |  data class Circle(override val id: String, val radius: Int) : Shape
      |  data class Square(override val id: String, val side: Int) : Shape
      |}
      |
      |val i: Lens<Shape, String> = Shape.id
      |val j: Prism<Shape, Shape.Circle> = Shape.circle
      """.compilationSucceedsWithSerialization()
  }
}

private fun String.compilationSucceedsWithSerialization() = compilationSucceeds(withKotlinxSerialization = true)
