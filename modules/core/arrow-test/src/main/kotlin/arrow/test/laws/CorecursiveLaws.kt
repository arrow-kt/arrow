package arrow.test.laws

import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.extensions.eval.monad.map
import arrow.core.extensions.eval.monad.monad
import arrow.core.right
import arrow.core.value
import arrow.recursion.Coalgebra
import arrow.recursion.CoalgebraM
import arrow.recursion.hylo
import arrow.recursion.pattern.FreeF
import arrow.recursion.typeclasses.Corecursive
import arrow.typeclasses.Eq
import arrow.typeclasses.Traverse
import io.kotlintest.properties.Gen

object CorecursiveLaws {

  fun <T, F> laws(CR: Corecursive<T, F>, coalg: Coalgebra<F, Int>, eqT: Eq<T>): List<Law> =
    listOf(
      Law("Ana == hylo + embed") { CR.anaEqualsHyloAndEmbed(coalg, eqT) },
      Law("Apo + coalgebra instead of r-coalgebra == ana") { CR.apoEqualsAnaWithNormalCoalgebra(coalg, eqT) },
      Law("Futu + coalgebra instead of cv-coalgebra == ana") { CR.futuEqualsAnaWithNormalCoalgebra(coalg, eqT) }
    )

  fun <T, F> laws(TF: Traverse<F>, CR: Corecursive<T, F>, coalg: Coalgebra<F, Int>, coalgM: CoalgebraM<F, ForEval, Int>, eqT: Eq<T>): List<Law> =
    laws(CR, coalg, eqT) + listOf(
      Law("anaM with eval is stacksafe") { CR.anaMIsStackSafeWithEval(TF, coalgM) },
      Law("apoM with eval is stackSafe") { CR.apoMIsStackSafeWithEval(TF, coalgM) },
      Law("futuM with eval is stackSafe") { CR.futuMIsStackSafeWithEval(TF, coalgM) }
    )

  fun <T, F> Corecursive<T, F>.anaEqualsHyloAndEmbed(coalg: Coalgebra<F, Int>, eqT: Eq<T>) =
    forFew(5, Gen.int().filter { it in 0..100 }) { i ->
      i.ana(coalg).equalUnderTheLaw(i.hylo(embed(), coalg, FF()), eqT)
    }

  fun <T, F> Corecursive<T, F>.apoEqualsAnaWithNormalCoalgebra(coalg: Coalgebra<F, Int>, eqT: Eq<T>) =
    forFew(5, Gen.int().filter { it in 0..100 }) { i ->
      i.apo {
        FF().run { coalg(it).map { it.right() } }
      }.equalUnderTheLaw(i.ana(coalg), eqT)
    }

  fun <T, F> Corecursive<T, F>.futuEqualsAnaWithNormalCoalgebra(coalg: Coalgebra<F, Int>, eqT: Eq<T>) =
    forFew(5, Gen.int().filter { it in 0..100 }) { i ->
      i.futu {
        FF().run { coalg(it).map { FreeF.pure<F, Int>(it) } }
      }.equalUnderTheLaw(i.ana(coalg), eqT)
    }

  fun <T, F> Corecursive<T, F>.anaMIsStackSafeWithEval(TF: Traverse<F>, coalg: CoalgebraM<F, ForEval, Int>) =
    5000.anaM(TF, Eval.monad(), coalg).value()

  fun <T, F> Corecursive<T, F>.apoMIsStackSafeWithEval(TF: Traverse<F>, coalg: CoalgebraM<F, ForEval, Int>) =
    5000.apoM(TF, Eval.monad()) {
      FF().run { coalg(it).map { it.map { it.right() } } }
    }.value()

  fun <T, F> Corecursive<T, F>.futuMIsStackSafeWithEval(TF: Traverse<F>, coalg: CoalgebraM<F, ForEval, Int>) =
    5000.futuM(TF, Eval.monad()) {
      FF().run { coalg(it).map { it.map { FreeF.pure<F, Int>(it) } } }
    }
}
