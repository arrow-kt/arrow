package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.FunctionK
import arrow.core.andThen
import arrow.core.extensions.either.functor.functor
import arrow.core.extensions.either.traverse.traverse
import arrow.core.fix
import arrow.core.identity
import arrow.recursion.Algebra
import arrow.recursion.CVCoalgebra
import arrow.recursion.CVCoalgebraM
import arrow.recursion.Coalgebra
import arrow.recursion.CoalgebraM
import arrow.recursion.RCoalgebra
import arrow.recursion.RCoalgebraM
import arrow.recursion.data.fix
import arrow.recursion.hylo
import arrow.recursion.hyloC
import arrow.recursion.hyloM
import arrow.recursion.hyloMC
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.fix
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Corecursive)
 *
 * Typeclass for types that can be generically unfolded with coalgebras.
 */
interface Corecursive<T, F> {

  fun FF(): Functor<F>

  /**
   * Implementation for embed.
   */
  fun Kind<F, T>.embedT(): T

  /**
   * Creates a algebra given a functor.
   */
  fun embed(): Algebra<F, T> = { it.embedT() }

  /**
   * Unfold to a structure F given any seed value.
   *
   * ```kotlin:ank:playground
   * import arrow.core.ListK
   * import arrow.recursion.Coalgebra
   * import arrow.recursion.extensions.listk.corecursive.corecursive
   * import arrow.recursion.pattern.ListF
   * import arrow.recursion.pattern.ListFPartialOf
   *
   * fun main() {
   *  val allIntsInRangeCoalg: Coalgebra<ListFPartialOf<Int>, Int> = {
   *    when (it) {
   *      0 -> ListF.NilF()
   *      else -> ListF.ConsF(it, it - 1)
   *    }
   *  }
   *  ListK.corecursive<Int>().run {
   *    100.ana(allIntsInRangeCoalg).also(::println)
   *  }
   * }
   * ```
   *
   * Note: Not stack-safe. Use [anaM] with a stack-safe monad, like [Eval]
   */
  fun <A> A.ana(coalg: Coalgebra<F, A>): T = hylo(embed(), coalg, FF())

  /**
   * Unfold monadically given any seed value.
   *
   * Can be used to gain stack-safety when used with a stack-safe monad like [Eval]
   *
   * ```kotlin:ank:playground
   * import arrow.core.Eval
   * import arrow.core.ForEval
   * import arrow.core.ListK
   * import arrow.core.extensions.eval.monad.monad
   * import arrow.core.value
   * import arrow.recursion.CoalgebraM
   * import arrow.recursion.extensions.listf.traverse.traverse
   * import arrow.recursion.extensions.listk.corecursive.corecursive
   * import arrow.recursion.pattern.ListF
   * import arrow.recursion.pattern.ListFPartialOf
   *
   * fun main() {
   *  val allIntsInRangeCoalg: CoalgebraM<ListFPartialOf<Int>, ForEval, Int> = {
   *    when (it) {
   *      0 -> Eval.now(ListF.NilF())
   *      else -> Eval.now(ListF.ConsF(it, it - 1))
   *    }
   *  }
   *
   *  ListK.corecursive<Int>().run {
   *    5000.anaM(ListF.traverse(), Eval.monad(), allIntsInRangeCoalg).value().also(::println)
   *  }
   * }
   * ```
   */
  fun <M, A> A.anaM(TF: Traverse<F>, MM: Monad<M>, coalg: CoalgebraM<F, M, A>): Kind<M, T> =
    hyloM(embed() andThen MM::just, coalg, TF, MM)

  /**
   * Unfold with the option to short circuit (by passing a Left<T>) at any time
   */
  fun <A> A.apo(coalg: RCoalgebra<F, T, A>): T =
    hyloC({
      FF().run { it.map { it.fix().fold(::identity, ::identity) } }.embedT()
    }, coalg, FF(), Either.functor())

  /**
   * Monadic version of apo
   *
   * Can be used to gain stack-safety when used with a stack-safe monad like [Eval]
   */
  fun <M, A> A.apoM(TF: Traverse<F>, MM: Monad<M>, coalg: RCoalgebraM<F, M, T, A>): Kind<M, T> =
    hyloMC({
      FF().run { MM.just(it.map { it.fix().fold(::identity, ::identity) }.embedT()) }
    }, coalg, TF, Either.traverse(), MM)

  /**
   * Unfold multiple layers of structure at a time. As opposed to apo (which gave us binary control over continuing the unfold or not)
   *  futu gives the option to unfold any number of layers.
   *
   * Returning Pure continues the unfold (but does not actually add a layer of structure on its own) and returning
   *  Impure adds a layer of structure to the unfolded structure.
   */
  fun <A> A.futu(coalg: CVCoalgebra<F, A>): T =
    FreeF.pure<F, A>(this).hylo(
      embed(),
      {
        when (val fa = it.unfix.fix()) {
          is FreeF.Pure -> coalg(fa.e)
          is FreeF.Impure -> FF().run { fa.fa.map { it.value().fix() } }
        }
      },
      FF()
    )

  /**
   * Monadic version of futu
   *
   * Can be used to gain stack-safety when used with a stack-safe monad like [Eval]
   */
  fun <M, A> A.futuM(TF: Traverse<F>, MM: Monad<M>, coalg: CVCoalgebraM<F, M, A>): Kind<M, T> =
    FreeF.pure<F, A>(this).hyloM(embed() andThen MM::just, {
      when (val fa = it.unfix.fix()) {
        is FreeF.Pure -> coalg(fa.e)
        is FreeF.Impure -> MM.just(TF.run { fa.fa.map { it.value().fix() } })
      }
    }, TF, MM)

  /**
   * Unfold using a normal algebra but apply a natural transformation (FunctionK) at each level
   */
  fun <A> A.postPro(trans: FunctionK<F, F>, coalg: Coalgebra<F, A>): T =
    hylo(embed(), coalg andThen trans::invoke, FF())
}
