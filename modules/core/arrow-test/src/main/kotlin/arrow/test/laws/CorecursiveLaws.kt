package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.extensions.eval.monad.map
import arrow.core.extensions.eval.monad.monad
import arrow.core.right
import arrow.core.value
import arrow.recursion.Coalgebra
import arrow.recursion.CoalgebraM
import arrow.recursion.data.Fix
import arrow.recursion.data.ForFix
import arrow.recursion.hylo
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.FreeFPartialOf
import arrow.recursion.pattern.FreeR
import arrow.recursion.typeclasses.Corecursive
import arrow.typeclasses.Traverse
import io.kotlintest.properties.Gen

object CorecursiveLaws {

  fun <T, F> laws(CR: Corecursive<T, F>, coalg: Coalgebra<F, Int>): List<Law> =
    listOf(
      Law("Ana == hylo + embed") { CR.anaEqualsHyloAndEmbed(coalg) },
      Law("Apo + coalgebra instead of r-coalgebra == ana") { CR.apoEqualsAnaWithNormalCoalgebra(coalg) }
      // Law("Futu + coalgebra instead of cv-coalgebra == ana") { CR.futuEqualsAnaWithNormalCoalgebra(coalg) }
    )

  fun <T, F> laws(TF: Traverse<F>, CR: Corecursive<T, F>, coalg: Coalgebra<F, Int>, coalgM: CoalgebraM<F, ForEval, Int>): List<Law> =
    laws(CR, coalg) + listOf(
      Law("anaM with eval is stacksafe") { CR.anaMIsStackSafeWithEval(TF, coalgM) },
      Law("apoM with eval is stackSafe") { CR.apoMIsStackSafeWithEval(TF, coalgM) }
      // Law("futuM with eval is stackSafe) { CR.futuMIsStackSafeWithEval(TF, coalgM) }
    )

  fun <T, F> Corecursive<T, F>.anaEqualsHyloAndEmbed(coalg: Coalgebra<F, Int>) =
    forFew(5, Gen.int().filter { it in 0..100 }) { i ->
      i.ana(coalg) == i.hylo(embed(), coalg, FF())
    }

  fun <T, F> Corecursive<T, F>.apoEqualsAnaWithNormalCoalgebra(coalg: Coalgebra<F, Int>) =
    forFew(5, Gen.int().filter { it in 0..100 }) { i ->
      i.apo {
        FF().run { coalg(it).map { it.right() } }
      } == i.ana(coalg)
    }

  /* TODO This crashes the kotlin compiler...
  fun <T, F> Corecursive<T, F>.futuEqualsAnaWithNormalCoalgebra(coalg: Coalgebra<F, Int>) =
    forFew(5, Gen.int().filter { it in 0..100 }) { i ->
      i.futu {
        FF().run { coalg(it).map { FreeF.pure<F, Int>(it) } }
      } == i.ana(coalg)
    }
    */

  fun <T, F> Corecursive<T, F>.anaMIsStackSafeWithEval(TF: Traverse<F>, coalg: CoalgebraM<F, ForEval, Int>) =
    5000.anaM(TF, Eval.monad(), coalg).value()

  fun <T, F> Corecursive<T, F>.apoMIsStackSafeWithEval(TF: Traverse<F>, coalg: CoalgebraM<F, ForEval, Int>) =
    5000.apoM(TF, Eval.monad()) {
      FF().run { coalg(it).map { it.map { it.right() } } }
    }.value()

  /* Also crashes compiler wtf
  fun <T, F> Corecursive<T, F>.futuMIsStackSafeWithEval(TF: Traverse<F>, coalg: CoalgebraM<F, ForEval, Int>) =
    5000.futuM(TF, Eval.monad()) {
      FF().run { coalg(it).map { it.map { FreeF.pure<F, Int>(it) } } }
    }
    */
}