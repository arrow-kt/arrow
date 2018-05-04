package arrow.recursion.laws

import arrow.Kind
import arrow.core.*
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import io.kotlintest.properties.Gen

val intGen = Gen.choose(0, 1000)

typealias NatPattern = ForOption
typealias GNat<T> = Kind<T, NatPattern>

fun toGNatCoalgebra() = Coalgebra<NatPattern, Int> {
  if (it == 0) None else Some(it - 1)
}

fun fromGNatAlgebra() = Algebra<NatPattern, Eval<Int>> {
  it.fix().fold({ Eval.Zero }, { it.map { it + 1 } })
}

inline fun <reified T> Int.toGNat(CT: Corecursive<T>): GNat<T> = CT.run {
  ana(Option.functor(), toGNatCoalgebra())
}

inline fun <reified T> GNat<T>.toInt(RT: Recursive<T>): Int = RT.run {
  cata(Option.functor(), fromGNatAlgebra())
}
