package arrow.test.laws

import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.extensions.eval.monad.monad
import arrow.core.value
import arrow.recursion.Algebra
import arrow.recursion.AlgebraM
import arrow.recursion.hylo
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Traverse
import io.kotlintest.properties.Gen

object RecursiveLaws {

  fun <T, F> laws(
    RR: Recursive<T, F>,
    smallGenT: Gen<T>,
    alg: Algebra<F, Int>
  ): List<Law> = listOf(
    Law("Cata == Hylo + project") { RR.cataEqualsHyloAndProject(smallGenT, alg) },
    Law("Para + algebra instead of r-algebra == cata") { RR.paraEqualsCataWithNormalAlgebra(smallGenT, alg) },
    Law("Histo + algebra instead of cv-algebra == cata") { RR.histoEqualsCataWithNormalAlgebra(smallGenT, alg) }
  )

  fun <T, F> laws(
    TF: Traverse<F>,
    RR: Recursive<T, F>,
    smallGenT: Gen<T>,
    largeGenT: Gen<T>,
    alg: Algebra<F, Int>,
    algM: AlgebraM<F, ForEval, Int>
  ): List<Law> = laws(RR, smallGenT, alg) + listOf(
    Law("cataM with eval is stacksafe") { RR.cataMEvalIsStackSafe(TF, largeGenT, algM) },
    Law("paraM with eval is stacksafe") { RR.paraMEvalIsStackSafe(TF, largeGenT, algM) },
    Law("histoM with eval is stacksafe") { RR.histoMEvalIsStackSafe(TF, largeGenT, algM) }
  )

  fun <T, F> Recursive<T, F>.cataEqualsHyloAndProject(smallGenT: Gen<T>, alg: Algebra<F, Int>) =
    forFew(5, smallGenT) { t ->
      t.cata(alg) == t.hylo(alg, project(), FF())
    }

  fun <T, F> Recursive<T, F>.paraEqualsCataWithNormalAlgebra(smallGenT: Gen<T>, alg: Algebra<F, Int>) =
    forFew(5, smallGenT) { t ->
      t.para<Int> {
        alg(FF().run { it.map { it.b } })
      } == t.cata(alg)
    }

  fun <T, F> Recursive<T, F>.histoEqualsCataWithNormalAlgebra(smallGenT: Gen<T>, alg: Algebra<F, Int>) =
    forFew(5, smallGenT) { t ->
      t.histo<Int> {
        alg(FF().run { it.map { it.head } })
      } == t.cata(alg)
    }

  fun <T, F> Recursive<T, F>.cataMEvalIsStackSafe(TF: Traverse<F>, largeGenT: Gen<T>, alg: AlgebraM<F, ForEval, Int>) =
    forFew(5, largeGenT) { t ->
      t.cataM(TF, Eval.monad(), alg).value()
      true
    }

  fun <T, F> Recursive<T, F>.paraMEvalIsStackSafe(TF: Traverse<F>, largeGenT: Gen<T>, alg: AlgebraM<F, ForEval, Int>) =
    forFew(5, largeGenT) { t ->
      t.paraM<ForEval, Int>(TF, Eval.monad()) {
        alg(FF().run { it.map { it.b } })
      }.value()
      true
    }

  fun <T, F> Recursive<T, F>.histoMEvalIsStackSafe(TF: Traverse<F>, largeGenT: Gen<T>, alg: AlgebraM<F, ForEval, Int>) =
    forFew(5, largeGenT) { t ->
      t.histoM<ForEval, Int>(TF, Eval.monad()) {
        alg(FF().run { it.map { it.head } })
      }.value()
      true
    }
}
