package arrow.optics.plugin

import com.tschuchort.compiletesting.SourceFile
import kotlin.test.Test

/** Kotlin corner cases found in review #2. All of them run with IR verification (see `buildCompilation`). */
class CornerCaseTests {

  // ---- nested sealed hierarchies (review #2, 1.5) --------------------------------------

  private val nestedHierarchy =
    """
      |@optics sealed interface LCE<out D> {
      |  data object Loading : LCE<Nothing>
      |
      |  @optics sealed interface CE<out D> : LCE<D> { companion object }
      |
      |  @optics data class Content<out D>(val data: D) : CE<D> { companion object }
      |
      |  @optics sealed interface Failure : CE<Nothing> {
      |    @optics data class Timeout(val millis: Long) : Failure { companion object }
      |    data class Other(val message: String) : Failure
      |    companion object
      |  }
      |
      |  companion object
      |}
    """

  @Test
  fun `nested sealed hierarchy gets prisms for direct inheritors, composable through the DSL`() {
    """
      |$`package`
      |$imports
      |$nestedHierarchy
      |
      |val loading: Prism<LCE<Nothing>, LCE.Loading> = LCE.loading()
      |val toContent: Optional<LCE<Int>, Int> = LCE.cE<Int>().content().data()
      |val toTimeout: Optional<LCE<Nothing>, Long> = LCE.cE<Nothing>().failure().timeout.millis
      |val r = loading.getOrNull(LCE.Loading) == LCE.Loading &&
      |        toContent.getOrNull(LCE.Content(5)) == 5 &&
      |        toContent.getOrNull(LCE.Loading) == null &&
      |        toContent.set(LCE.Content(5), 6) == LCE.Content(6) &&
      |        toTimeout.getOrNull(LCE.Failure.Timeout(3)) == 3L &&
      |        toTimeout.getOrNull(LCE.Failure.Other("x")) == null &&
      |        LCE.Failure.other.getOrNull(LCE.Failure.Other("x")) == LCE.Failure.Other("x")
      """.evals("r" to true)
  }

  @Test
  fun `nested sealed hierarchy has no prisms for indirect inheritors`() {
    """
      |$`package`
      |$imports
      |$nestedHierarchy
      |
      |val p = LCE.content<Int>()
      // the only candidate is the DSL extension for `CE`'s prism, not a member of `LCE.Companion`
      """.failsWith { "Candidate 'fun <D, __S> PPrism<__S, __S, LCE.CE<D>, LCE.CE<D>>.content()" in it && "receiver type mismatch" in it }
  }

  @Test
  fun `nested monomorphic sealed hierarchy with a shared-property lens in the middle`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Shape {
      |  @optics sealed interface Round : Shape {
      |    val radius: Int
      |    data class Circle(override val radius: Int) : Round
      |    data class Ring(override val radius: Int, val width: Int) : Round
      |    companion object
      |  }
      |  data class Square(val side: Int) : Shape
      |  companion object
      |}
      |
      |val radius: Optional<Shape, Int> = Shape.round + Shape.Round.radius
      |val r = radius.getOrNull(Shape.Round.Ring(2, 3)) == 2 &&
      |        radius.set(Shape.Round.Ring(2, 3), 5) == Shape.Round.Ring(5, 3) &&
      |        radius.getOrNull(Shape.Square(1)) == null &&
      |        Shape.Round.circle.getOrNull(Shape.Round.Circle(1)) == Shape.Round.Circle(1)
      """.evals("r" to true)
  }

  // ---- generics (review #2, 1.2 and 2.1) -------------------------------------------------

  @Test
  fun `sealed shared-property lens over a generic subclass`() {
    """
      |$`package`
      |$imports
      |@optics sealed class S {
      |  abstract val n: String
      |  data class A<T>(override val n: String, val t: T) : S()
      |  data class B(override val n: String) : S()
      |  companion object
      |}
      |
      |val r = S.n.set(S.A("a", 1), "z") == S.A("z", 1) && S.n.set(S.B("b"), "z") == S.B("z")
      """.evals("r" to true)
  }

  @Test
  fun `generic lens keeps upper bounds`() {
    """
      |$`package`
      |$imports
      |@optics data class W<T : Comparable<T>>(val item: T, val other: Int) { companion object }
      |@optics data class H(val w: W<String>) { companion object }
      |
      |val r = W.item<String>().set(W("a", 1), "b") == W("b", 1) &&
      |        W.other<String>().set(W("a", 1), 2) == W("a", 2) &&
      |        H.w.item().get(H(W("x", 0))) == "x"
      """.evals("r" to true)
  }

  @Test
  fun `generic lens rejects arguments outside the bounds`() {
    """
      |$`package`
      |$imports
      |@optics data class W<T : Comparable<T>>(val item: T) { companion object }
      |
      |val bad = W.item<Any>()
      """.failsWith { "not within its bounds" in it || "upper bound" in it }
  }

  @Test
  fun `generic value class iso with interdependent bounds`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline value class Box<A, L : List<A>>(val items: L) { companion object }
      |
      |val r = Box.items<Int, List<Int>>().reverseGet(listOf(1)) == Box<Int, List<Int>>(listOf(1))
      """.evals("r" to true)
  }

  @Test
  fun `prism for a generic subclass with a phantom type parameter`() {
    """
      |$`package`
      |$imports
      |@optics sealed class T<A> {
      |  data class S<A, X>(val a: A) : T<A>()
      |  companion object
      |}
      |
      |val r = T.s<Int, String>().getOrNull(T.S<Int, String>(1)) == T.S<Int, String>(1)
      """.evals("r" to true)
  }

  @Test
  fun `lens over a definitely non-nullable field`() {
    """
      |$`package`
      |$imports
      |@optics data class D<T>(val v: T & Any, val w: T) { companion object }
      |
      |val r = D.v<String?>().set(D("a", null), "b") == D("b", null)
      """.evals("r" to true)
  }

  @Test
  fun `lenses over suspend and context function types`() {
    """
      |$`package`
      |$imports
      |@optics data class Fns(val s: suspend (Int) -> Int, val c: context(String) () -> Int) { companion object }
      |
      |val fs: suspend (Int) -> Int = { it }
      |val fc: context(String) () -> Int = { 1 }
      |val fns = Fns(fs, fc)
      |val r = Fns.s.get(fns) === fs && Fns.c.set(fns, fc).c === fc
      """.evals("r" to true, contextParameters = true)
  }

  // ---- synthetic file names (review #2, 1.3) ---------------------------------------------

  @Test
  fun `nested classes with the same simple name in one package`() {
    """
      |$`package`
      |$imports
      |class A { @optics data class Leaf(val v: Int) { companion object } }
      |class B { @optics data class Leaf(val v: String) { companion object } }
      |@optics data class HA(val l: A.Leaf) { companion object }
      |@optics data class HB(val l: B.Leaf) { companion object }
      |
      |val r = HA.l.v.get(HA(A.Leaf(1))) == 1 && HB.l.v.get(HB(B.Leaf("x"))) == "x"
      """.evals("r" to true)
  }

  @Test
  fun `user file named like the old generated file`() {
    compilationSucceeds(
      sources = arrayOf(
        SourceFile.kotlin(
          "Source.kt",
          """
          package p
          import arrow.optics.*
          @optics data class Leaf(val v: Int) { companion object }
          @optics data class Tree(val leaf: Leaf) { companion object }
          val r = Tree.leaf.v.get(Tree(Leaf(1))) == 1
          """.trimIndent(),
        ),
        SourceFile.kotlin("LeafOptics.kt", "package p\nfun helper() = 1"),
      ),
    )
  }
}
