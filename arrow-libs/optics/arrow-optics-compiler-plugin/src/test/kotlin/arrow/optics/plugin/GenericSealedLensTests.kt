@file:OptIn(ExperimentalCompilerApi::class)

package arrow.optics.plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

/** Shared-property lenses on generic sealed parents (review #1 §3.2). */
class GenericSealedLensTests {
  private val optIn = "Potentially unsafe optic because of a covariant type argument"

  @Test
  fun `invariant parent with forwarding subclasses`() {
    """
      |$`package`
      |$imports
      |@optics sealed class Box<A> {
      |  abstract val value: A
      |  abstract val tag: String
      |  data class Full<A>(override val value: A, override val tag: String, val extra: Int) : Box<A>()
      |  data class Other<B>(override val value: B, override val tag: String) : Box<B>()
      |  companion object
      |}
      |
      |val value: Lens<Box<Int>, Int> = Box.value<Int>()
      |val tag: Lens<Box<Int>, String> = Box.tag<Int>()
      |val r = value.get(Box.Full(1, "t", 2)) == 1 &&
      |        value.set(Box.Full(1, "t", 2), 5) == Box.Full(5, "t", 2) &&
      |        value.set(Box.Other(1, "t"), 5) == Box.Other(5, "t") &&
      |        tag.set(Box.Other(1, "a"), "b") == Box.Other(1, "b")
      """.evals("r" to true)
  }

  @Test
  fun `invariant parent with fixed arguments`() {
    """
      |$`package`
      |$imports
      |@optics sealed class Inv<T> {
      |  abstract val prop: T
      |  data class S(override val prop: String) : Inv<String>()
      |  data class I(override val prop: Int) : Inv<Int>()
      |  companion object
      |}
      |
      |val r = Inv.prop<String>().set(Inv.S("a"), "b") == Inv.S("b") &&
      |        Inv.prop<Int>().get(Inv.I(1)) == 1
      """.evals("r" to true)
  }

  @Test
  fun `covariant parent, property without type parameters`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Res<out A> {
      |  val tag: String
      |  data class Ok<A>(val v: A, override val tag: String) : Res<A>
      |  data class Err(override val tag: String) : Res<Nothing>
      |  companion object
      |}
      |
      |val tag: Lens<Res<Int>, String> = Res.tag<Int>()
      |val r = tag.set(Res.Ok(1, "a"), "b") == Res.Ok(1, "b") &&
      |        tag.set(Res.Err("a"), "b") == Res.Err("b")
      """.evals("r" to true)
  }

  @Test
  fun `covariant parent with exclusively forwarding subclasses`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Wrapped<out A> {
      |  val value: A
      |  data class One<A>(override val value: A) : Wrapped<A>
      |  data class Two<A>(override val value: A, val count: Int) : Wrapped<A>
      |  companion object
      |}
      |
      |val r = Wrapped.value<Int>().set(Wrapped.Two(1, 3), 2) == Wrapped.Two(2, 3) &&
      |        Wrapped.value<Int>().get(Wrapped.One(7)) == 7
      """.evals("r" to true)
  }

  @Test
  fun `covariant parent with fixed arguments works with opt-in`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Base<out T> {
      |  val prop: T
      |  data class Child1(override val prop: String) : Base<String>
      |  data class Child2(override val prop: Int) : Base<Int>
      |  companion object
      |}
      |
      |@OptIn(PotentiallyUnsafeCovariantOptic::class)
      |val r = Base.prop<String>().set(Base.Child1("a"), "b") == Base.Child1("b")
      """.evals("r" to true)
  }

  @Test
  fun `covariant parent with a forwarded parameter in a sibling field requires opt-in`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Box<out A> {
      |  val value: A
      |  data class Full<A>(override val value: A, val history: MutableList<A>) : Box<A>
      |  companion object
      |}
      |
      |val l = Box.value<Int>()
      """.failsWith { optIn in it }
  }

  @Test
  fun `covariant parent with a non-forwarded argument requires opt-in`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Box<out A> {
      |  val value: A
      |  data class Many<B>(override val value: List<B>) : Box<List<B>>
      |  companion object
      |}
      |
      |val l = Box.value<List<Int>>()
      """.failsWith { optIn in it }
  }

  @Test
  fun `generic parent with a non-uniform property`() {
    """
      |$`package`
      |$imports
      |@optics sealed class B<A> {
      |  abstract val v: A?
      |  data class X<A>(override val v: A) : B<A>()
      |  companion object
      |}
      |
      |val l = B.v<Int>()
      """.failsWith { "Unresolved reference 'v'" in it }
  }

  @Test
  fun `potentially unsafe lens without the opt-in marker in the classpath is reported`() {
    // an older arrow-annotations, without `PotentiallyUnsafeCovariantOptic`
    val oldAnnotations = SourceFile.kotlin(
      "OldAnnotations.kt",
      """
      package arrow.optics
      annotation class optics(val targets: Array<OpticsTarget> = emptyArray())
      enum class OpticsTarget { ISO, LENS, PRISM, OPTIONAL, DSL }
      """.trimIndent(),
    )
    val source = SourceFile.kotlin(
      SOURCE_FILENAME,
      """
      package p
      import arrow.optics.*
      @optics sealed interface Base<out T> {
        val prop: T
        val tag: String
        data class Child1(override val prop: String, override val tag: String) : Base<String>
        companion object
      }
      val tag: Lens<Base<String>, String> = Base.tag<String>()
      """.trimIndent(),
    )
    val compilation = buildCompilation(sources = arrayOf(oldAnnotations, source))
    // classpaths are arrow-annotations, arrow-core, arrow-optics, in this order
    compilation.classpaths = compilation.classpaths.drop(1)
    val result = compilation.compile()
    result.exitCode shouldBe KotlinCompilation.ExitCode.OK
    result.messages shouldContain "The lens 'prop' is not generated because setting it may not respect covariant type arguments"
    result.messages shouldNotContain "The lens 'tag'"
  }
}
