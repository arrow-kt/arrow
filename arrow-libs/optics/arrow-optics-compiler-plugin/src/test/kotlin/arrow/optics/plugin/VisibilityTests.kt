package arrow.optics.plugin

import kotlin.test.Test

/** Visibility of the generated optics (review #2, 1.1 and 2.2). */
class VisibilityTests {

  @Test
  fun `optics of a private class are usable in its file`() {
    """
      |$`package`
      |$imports
      |@optics @optics.copy private data class P(val x: Int, val q: Q)
      |@optics private data class Q(val y: Int) { companion object }
      |
      |private val p = P(1, Q(2))
      |val r = P.x.get(p) == 1 &&
      |        P.q.set(p, Q(3)) == P(1, Q(3)) &&
      |        p.copy { P.x set 5 } == P(5, Q(2))
      """.evals("r" to true)
  }

  @Test
  fun `DSL of a private class is skipped with a warning`() {
    """
      |$`package`
      |$imports
      |@optics private data class P(val q: Q) { companion object }
      |@optics data class Q(val y: Int) { companion object }
      """.warnsWith { "The DSL extensions for q are not generated" in it }
  }

  @Test
  fun `DSL of a private class is not generated`() {
    """
      |$`package`
      |$imports
      |@optics private data class Q(val y: Int) { companion object }
      |@optics data class P(val n: Int) { companion object }
      |
      |private val l = Lens.id<Q>().y
      |val m = Lens.id<P>().n
      """.failsWith { "Unresolved reference 'y'" in it && "'n'" !in it }
  }

  @Test
  fun `optics of a class nested in a private class`() {
    """
      |$`package`
      |$imports
      |private class Outer {
      |  @optics data class In(val x: Int) { companion object }
      |}
      |
      |val r = Outer.In.x.get(Outer.In(1)) == 1
      """.evals("r" to true)
  }

  @Test
  fun `optics of a protected nested class`() {
    """
      |$`package`
      |$imports
      |open class Outer {
      |  @optics protected data class In(val x: Int, val y: Int) { companion object }
      |  fun t() = In.x.set(In(1, 2), 3) == In(3, 2)
      |}
      |
      |val r = Outer().t()
      """.evals("r" to true)
  }

  @Test
  fun `lens over a private constructor is private`() {
    """
      |$`package`
      |$imports
      |@optics data class PC private constructor(val x: Int) {
      |  companion object {
      |    fun of(x: Int) = PC(if (x < 0) 0 else x)
      |  }
      |}
      |
      |val l = PC.x
      """.failsWith { "Cannot access 'val x: Lens<PC, Int>': it is private" in it }
  }

  @Test
  fun `lens over a private constructor is usable inside the class`() {
    """
      |$`package`
      |$imports
      |@optics data class PC private constructor(val x: Int) {
      |  fun reset() = PC.x.set(this, 0)
      |  companion object {
      |    fun of(x: Int) = PC(x)
      |  }
      |}
      |
      |val r = PC.of(3).reset() == PC.of(0)
      """.evals("r" to true)
  }

  @Test
  fun `lens over a private property is private, others are public`() {
    """
      |$`package`
      |$imports
      |@optics data class PP(private val secret: Int, val pub: Int) {
      |  fun peek() = PP.secret.get(this)
      |  companion object
      |}
      |
      |val r = PP(1, 2).peek() == 1 && PP.pub.get(PP(1, 2)) == 2
      """.evals("r" to true)
  }

  @Test
  fun `lens over a private property cannot be used outside the class`() {
    """
      |$`package`
      |$imports
      |@optics data class PP(private val secret: Int, val pub: Int) { companion object }
      |
      |val l = PP.secret
      """.failsWith { "it is private" in it }
  }

  @Test
  fun `lens over an internal constructor is internal`() {
    """
      |$`package`
      |$imports
      |@optics data class IC internal constructor(val x: Int) { companion object }
      |
      |internal val r = IC.x.set(IC(1), 2) == IC(2)
      """.evals("r" to true)
  }

  @Test
  fun `sealed lens over a subclass with a private constructor is private`() {
    """
      |$`package`
      |$imports
      |@optics sealed interface S {
      |  val n: String
      |  data class A private constructor(override val n: String) : S
      |  data class B(override val n: String) : S
      |  companion object
      |}
      |
      |val l = S.n
      """.failsWith { "it is private" in it }
  }

  @Test
  fun `copy over a private constructor is usable inside the class`() {
    """
      |$`package`
      |$imports
      |@optics @optics.copy data class C private constructor(val x: Int) {
      |  fun two() = copy { C.x set 2 }
      |  companion object {
      |    fun of(x: Int) = C(x)
      |  }
      |}
      |
      |val r = C.of(1).two() == C.of(2)
      """.evals("r" to true)
  }
}
