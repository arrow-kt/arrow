package arrow.test.laws

import arrow.core.ForEval
import arrow.recursion.Algebra
import arrow.recursion.AlgebraM
import arrow.recursion.Coalgebra
import arrow.recursion.CoalgebraM
import arrow.recursion.typeclasses.Birecursive
import arrow.typeclasses.Eq
import arrow.typeclasses.Traverse
import io.kotlintest.properties.Gen

object BirecursiveLaws {

  fun <T, F> laws(
    BR: Birecursive<T, F>,
    genSmallT: Gen<T>,
    eqT: Eq<T>,
    alg: Algebra<F, Int>,
    coalg: Coalgebra<F, Int>
  ): List<Law> = RecursiveLaws.laws(BR, genSmallT, alg) + CorecursiveLaws.laws(BR, coalg, eqT)

  fun <T, F> laws(
    TF: Traverse<F>,
    BR: Birecursive<T, F>,
    genSmallT: Gen<T>,
    genLargeT: Gen<T>,
    eqT: Eq<T>,
    alg: Algebra<F, Int>,
    algM: AlgebraM<F, ForEval, Int>,
    coalg: Coalgebra<F, Int>,
    coalgM: CoalgebraM<F, ForEval, Int>
  ): List<Law> = RecursiveLaws.laws(TF, BR, genSmallT, genLargeT, alg, algM) +
    CorecursiveLaws.laws(TF, BR, coalg, coalgM, eqT)
}
