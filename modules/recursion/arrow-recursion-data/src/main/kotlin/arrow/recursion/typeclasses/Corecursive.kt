package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.functor.functor
import arrow.core.extensions.either.traverse.traverse
import arrow.recursion.*
import arrow.recursion.data.fix
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
   * Unfold to a Kind given any seed value.
   *
   * ```kotlin:ank:playground
   * import arrow.data.ListK
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
   *
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
   * Unfold monadically to a Kind given any seed value.
   *
   * Can be used to gain stack-safety when used with a stack-safe monad like [Eval]
   *
   * ```kotlin:ank:playground
   * import arrow.core.Eval
   * import arrow.core.ForEval
   * import arrow.core.extensions.eval.monad.monad
   * import arrow.core.value
   * import arrow.data.ListK
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

  fun <A> A.apo(coalg: RCoalgebra<F, T, A>): T =
    hyloC({
      FF().run { it.map { it.fix().fold(::identity, ::identity) } }.embedT()
    }, coalg, FF(), Either.functor())

  fun <M, A> A.apoM(TF: Traverse<F>, MM: Monad<M>, coalg: RCoalgebraM<F, M, T, A>): Kind<M, T> =
    hyloMC({
      FF().run { MM.just(it.map { it.fix().fold(::identity, ::identity) }.embedT()) }
    }, coalg, TF, Either.traverse(), Either.applicative(), MM)

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

  /* Causes compiler error (futu did as well at some point but works again for some weird reason)
  fun <M, A> A.futuM(TF: Traverse<F>, MM: Monad<M>, coalg: CVCoalgebraM<F, M, A>): Kind<M, T> =
    FreeF.pure<F, A>(this).hyloM(embed() andThen MM::just, {
      when (val fa = it.unfix.fix()) {
        is FreeF.Pure -> coalg(fa.e)
        is FreeF.Impure -> MM.just(TF.run { fa.fa.map { it.value().fix() } })
      }
    }, TF, MM)
  */

  fun <A> A.postPro(trans: FunctionK<F, F>, coalg: Coalgebra<F, A>): T =
    hylo(embed(), coalg andThen trans::invoke, FF())

  fun <A> A.coelgot(f: (Tuple2<A, Eval<Kind<F, T>>>) -> T, coalg: Coalgebra<F, A>): T {
    fun h(a: A): T =
      FF().run {
        f(
          Tuple2(a, Eval.later { coalg(a).map(::h) })
        )
      }
    return h(this)
  }

  fun <M, A> A.coelgotM(TF: Traverse<F>, MM: Monad<M>, f: (Tuple2<A, Eval<Kind<M, Kind<F, T>>>>) -> Kind<M, T>, coalg: CoalgebraM<F, M, A>): Kind<M, T> {
    fun h(a: A): Kind<M, T> =
      TF.run {
        MM.run {
          f(
            Tuple2(a, Eval.later { coalg(a).flatMap { it.map(::h).sequence(MM) } })
          )
        }
      }

    return h(this)
  }
}