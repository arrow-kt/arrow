package arrow.optics.plugin

import kotlin.test.Test

/**
 * These tests actually *execute* the generated optic bodies (`get`/`set`/`reverseGet`/`getOrNull`),
 * unlike the ported KSP suite which mostly checks that the optics resolve. They are the regression
 * net for the IR body generation — see review §1.1.
 */
class RuntimeTests {

  // ---- LENS (data class) -------------------------------------------------------------

  @Test
  fun `lens get and set on a data class`() {
    """
      |$`package`
      |$imports
      |@optics data class Point(val x: Int, val y: Int) { companion object }
      |
      |val lx: Lens<Point, Int> = Point.x
      |val p = Point(1, 2)
      |val r = lx.get(p) == 1 &&
      |        lx.set(p, 9) == Point(9, 2) &&
      |        lx.modify(p) { it + 10 } == Point(11, 2)
      """.evals("r" to true)
  }

  @Test
  fun `lens laws hold`() {
    """
      |$`package`
      |$imports
      |@optics data class Point(val x: Int, val y: Int) { companion object }
      |
      |val lx = Point.x
      |val p = Point(1, 2)
      |val getSet = lx.get(lx.set(p, 9)) == 9
      |val setGet = lx.set(p, lx.get(p)) == p
      |val setSet = lx.set(lx.set(p, 3), 9) == lx.set(p, 9)
      |val r = getSet && setGet && setSet
      """.evals("r" to true)
  }

  // ---- ISO (value class) -------------------------------------------------------------

  @Test
  fun `iso get and reverseGet round-trip`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline value class Cents(val value: Int) { companion object }
      |
      |val iso: Iso<Cents, Int> = Cents.value
      |val r = iso.get(Cents(3)) == 3 &&
      |        iso.reverseGet(7) == Cents(7) &&
      |        iso.reverseGet(iso.get(Cents(42))) == Cents(42)
      """.evals("r" to true)
  }

  @Test
  fun `generic iso get and reverseGet round-trip`() {
    """
      |$`package`
      |$imports
      |@optics @JvmInline value class Wrap<T>(val unwrap: T) { companion object }
      |
      |val iso: Iso<Wrap<String>, String> = Wrap.unwrap<String>()
      |val r = iso.get(Wrap("hi")) == "hi" && iso.reverseGet("bye") == Wrap("bye")
      """.evals("r" to true)
  }

  // ---- PRISM -------------------------------------------------------------------------

  @Test
  fun `prism getOrNull matches the right branch`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface Shape {
      |  data class Dot(val at: Int) : Shape
      |  data class Line(val len: Int) : Shape
      |  companion object
      |}
      |
      |val p: Prism<Shape, Shape.Dot> = Shape.dot
      |val r = p.getOrNull(Shape.Dot(1)) == Shape.Dot(1) &&
      |        p.getOrNull(Shape.Line(2)) == null
      """.evals("r" to true)
  }

  @Test
  fun `generic prism getOrNull at a concrete instantiation`() {
    """
      |$`package`
      |$imports
      |@optics sealed class Tree<A> {
      |  data class Leaf<A>(val value: A) : Tree<A>()
      |  data class Branch<A>(val left: A, val right: A) : Tree<A>()
      |  companion object
      |}
      |
      |val p: Prism<Tree<Int>, Tree.Leaf<Int>> = Tree.leaf<Int>()
      |val r = p.getOrNull(Tree.Leaf(5)) == Tree.Leaf(5) &&
      |        p.getOrNull(Tree.Branch(1, 2)) == null
      """.evals("r" to true)
  }

  // ---- Sealed shared-property LENS (§5.2) — the `when`-dispatch `set` ----------------

  @Test
  fun `sealed shared-property lens get and set across subclasses with extra fields`() {
    // `Circle` has TWO extra fields (radius, color), so the `set` reconstruction reads multiple
    // siblings — this is the case that exposes IR node sharing (review §2.1).
    """
      |$`package`
      |$imports
      |@optics sealed class Shape {
      |  abstract val name: String
      |  data class Circle(override val name: String, val radius: Int, val color: String) : Shape()
      |  data class Square(override val name: String, val side: Int) : Shape()
      |  companion object
      |}
      |
      |val nameLens: Lens<Shape, String> = Shape.name
      |val circle: Shape = Shape.Circle("c", 5, "red")
      |val square: Shape = Shape.Square("s", 3)
      |val r = nameLens.get(circle) == "c" &&
      |        nameLens.set(circle, "z") == Shape.Circle("z", 5, "red") &&
      |        nameLens.set(square, "z") == Shape.Square("z", 3) &&
      |        nameLens.modify(circle) { it + "!" } == Shape.Circle("c!", 5, "red")
      """.evals("r" to true)
  }

  // ---- Generic LENS ------------------------------------------------------------------

  @Test
  fun `generic lens get and set with a sibling of a different type parameter`() {
    // Setting `first` reconstructs `Pair2`, reading the sibling `second` (type `B`) — exercises the
    // generic sibling-substitution path (review §2.4).
    """
      |$`package`
      |$imports
      |@optics data class Pair2<A, B>(val first: A, val second: B) { companion object }
      |
      |val l: Lens<Pair2<String, Int>, String> = Pair2.first<String, Int>()
      |val original = Pair2("x", 1)
      |val r = l.get(original) == "x" &&
      |        l.set(original, "y") == Pair2("y", 1)
      """.evals("r" to true)
  }

  // ---- Nullable focus + notNull ------------------------------------------------------

  @Test
  fun `nullable lens and notNull optional behave at runtime`() {
    """
      |$`package`
      |$imports
      |@optics data class Maybe(val value: String?) { companion object }
      |
      |val l: Lens<Maybe, String?> = Maybe.value
      |val opt: Optional<Maybe, String> = Maybe.value.notNull
      |val r = l.get(Maybe(null)) == null &&
      |        l.set(Maybe("a"), "b") == Maybe("b") &&
      |        opt.getOrNull(Maybe("x")) == "x" &&
      |        opt.getOrNull(Maybe(null)) == null &&
      |        opt.set(Maybe("x"), "y") == Maybe("y")
      """.evals("r" to true)
  }
}
