package consumer

/** AutoFold **/

sealed class A

data class B(val x: Int) : A()
data class C(val x: Int) : A()
object D : A()


sealed class Expr<out A, B>
data class Const(val number: Double) : Expr<Nothing, Double>()
data class Sum<C>(val e1: Expr<Int, C>, val e2: Expr<Int, C>) : Expr<Int, C>()
object NotANumber : Expr<Nothing, Nothing>()

object Autofold {

  fun syntheticExprFold(): Int =
    Sum(
      e1 = Const(3.0),
      e2 = Const(2.1)
    ).fold(
      { c: Const -> c.number.toInt() },
      { _: Sum<Double> -> 0 },
      { 94 }
    )

  fun syntheticAFold(): Int =
    B(44).fold(
      { b: B -> b.x * b.x },
      { c: C -> c.x - c.x },
      { 0 }
    )
}