package arrow.recursion.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.tuple2.applicative.applicative
import arrow.core.extensions.tuple2.functor.functor
import arrow.core.extensions.tuple2.traverse.traverse
import arrow.free.Cofree
import arrow.recursion.*
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.recursion.typeclasses.Recursive)
 *
 * Typeclass for types that can be generically folded with algebras.
 */
interface Recursive<T, F> {

  fun FF(): Functor<F>

  /**
   * Implementation for project.
   */
  fun T.projectT(): Kind<F, T>

  /**
   * Creates a coalgebra given a functor.
   */
  fun project(): Coalgebra<F, T> = { it.projectT() }

  /**
   * Fold generalized over any recursive type.
   */
  fun <A> T.cata(alg: Algebra<F, A>): A = hylo(alg, project(), FF())

  fun <M, A> T.cataM(TF: Traverse<F>, MM: Monad<M>, alg: (Kind<F, A>) -> Kind<M, A>): Kind<M, A> =
    hyloM(alg, project() andThen MM::just, TF, MM)

  fun <A> T.para(alg: RAlgebra<F, T, A>): A =
    hyloC({
      FF().run { it.map { it.fix() } }.let(alg)
    }, project() andThen { FF().run { it.map { it toT it } } },
      FF(), Tuple2.functor()
    )

  fun <M, A> T.paraM(
    TF: Traverse<F>,
    MM: Monad<M>,
    MT: Monoid<T>,
    alg: (Kind<F, Tuple2<T, A>>) -> Kind<M, A>
  ): Kind<M, A> =
    hyloMC({
      alg(FF().run { it.map { it.fix() } })
    }, project() andThen { FF().run { it.map { it toT it } } } andThen MM::just, TF, Tuple2.traverse(), Tuple2.applicative(MT), MM)

  fun <A> T.histo(alg: CVAlgebra<F, A>): A =
    hylo<F, T, Cofree<F, A>>({
      Cofree(FF(), alg(it), Eval.now(it))
    }, project(),
      FF()
    ).head

  fun <M, A> T.histoM(TF: Traverse<F>, MM: Monad<M>, alg: CVAlgebraM<F, M, A>): Kind<M, A> =
    MM.run {
      hyloM<F, M, T, Cofree<F, A>>({
        alg(it).map { a -> Cofree(TF, a, Eval.now(it)) }
      }, project() andThen MM::just,
        TF, MM
      ).map { it.head }
    }

  fun <A, B> B.elgot(alg: Algebra<F, A>, f: (B) -> Either<A, Kind<F, B>>): A {
    fun h(b: B): A =
      f(b).fold(::identity) { FF().run { alg(it.map(::h)) } }

    return h(this)
  }

  fun <M, A, B> B.elgotM(TF: Traverse<F>, MM: Monad<M>, alg: AlgebraM<F, M, A>, f: (B) -> Either<A, Kind<F, B>>): Kind<M, A> {
    fun h(b: B): Kind<M, A> =
      f(b).fold(MM::just) { MM.run { TF.run { it.traverse(MM, ::h).flatMap(alg) } } }

    return h(this)
  }
}
