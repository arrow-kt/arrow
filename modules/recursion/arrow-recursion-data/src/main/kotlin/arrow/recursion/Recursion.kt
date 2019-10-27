package arrow.recursion

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.identity
import arrow.free.Cofree
import arrow.mtl.typeclasses.compose
import arrow.mtl.typeclasses.nest
import arrow.mtl.typeclasses.unnest
import arrow.recursion.data.fix
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.FreeR
import arrow.recursion.pattern.fix
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Traverse

typealias Algebra<F, A> = (Kind<F, A>) -> A
typealias AlgebraM<F, M, A> = (Kind<F, A>) -> Kind<M, A>

typealias Coalgebra<F, A> = (A) -> Kind<F, A>
typealias CoalgebraM<F, M, A> = (A) -> Kind<M, Kind<F, A>>

typealias RAlgebra<F, T, A> = (Kind<F, Tuple2<T, A>>) -> A
typealias RAlgebraM<F, M, T, A> = (Kind<F, Tuple2<T, A>>) -> Kind<M, A>

typealias RCoalgebra<F, T, A> = (A) -> Kind<F, Either<T, A>>
typealias RCoalgebraM<F, M, T, A> = (A) -> Kind<M, Kind<F, Either<T, A>>>

typealias CVAlgebra<F, A> = (Kind<F, Cofree<F, A>>) -> A
typealias CVAlgebraM<F, M, A> = (Kind<F, Cofree<F, A>>) -> Kind<M, A>

typealias CVCoalgebra<F, A> = (A) -> Kind<F, FreeR<F, A>>
typealias CVCoalgebraM<F, M, A> = (A) -> Kind<M, Kind<F, FreeR<F, A>>>

/**
 * Combination of cata and ana.
 *
 * An implementation of merge-sort:
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.recursion.Algebra
 * import arrow.recursion.Coalgebra
 * import arrow.recursion.hylo
 * import arrow.typeclasses.Functor
 *
 * // boilerplate that @higherkind generates
 * class ForTree private constructor()
 * typealias TreeOf<A, B> = Kind<TreePartialOf<A>, B>
 * typealias TreePartialOf<A> = Kind<ForTree, A>
 *
 * // A simple binary tree
 * sealed class Tree<A, B> : TreeOf<A, B> {
 *  class Empty<A, B> : Tree<A, B>()
 *  class Leaf<A, B>(val a: A) : Tree<A, B>()
 *  class Branch<A, B>(val l: B, val r: B) : Tree<A, B>()
 *
 *  companion object {
 *    fun <A> functor(): Functor<TreePartialOf<A>> = object : Functor<TreePartialOf<A>> {
 *      override fun <C, B> Kind<TreePartialOf<A>, C>.map(f: (C) -> B): Kind<TreePartialOf<A>, B> = when (val t = this as Tree<A, C>) {
 *        is Empty -> Empty()
 *        is Leaf -> Leaf(t.a)
 *        is Branch -> Branch(f(t.l), f(t.r))
 *      }
 *    }
 *  }
 * }
 *
 * infix fun List<Int>.merge(other: List<Int>): List<Int> = when {
 *  this.isEmpty() -> other
 *  other.isEmpty() -> this
 *  else ->
 *    if (first() > other.first()) (listOf(other.first()) + (this merge other.drop(1)))
 *    else (listOf(first()) + (this.drop(1) merge other))
 * }
 *
 * fun main() {
 *  val unfold: Coalgebra<TreePartialOf<Int>, List<Int>> = {
 *    when {
 *      it.isEmpty() -> Tree.Empty()
 *      it.size == 1 -> Tree.Leaf(it.first())
 *      else -> (it.size / 2).let { half ->
 *        Tree.Branch<Int, List<Int>>(it.take(half), it.drop(half))
 *      }
 *    }
 *  }
 *  val fold: Algebra<TreePartialOf<Int>, List<Int>> = {
 *    (it as Tree<Int, List<Int>>).let { t ->
 *      when (t) {
 *        is Tree.Empty -> emptyList()
 *        is Tree.Leaf -> listOf(t.a)
 *        is Tree.Branch -> t.l merge t.r
 *      }
 *    }
 *  }
 *
 *  (0..1000).shuffled().also(::println).hylo(fold, unfold, Tree.functor()).also(::println)
 * }
 *
 * ```
 *
 * Note: Not stack-safe. Use [hyloM] with a stack-safe monad, like [Eval]
 */
fun <F, A, B> A.hylo(
  alg: Algebra<F, B>,
  coalg: Coalgebra<F, A>,
  FF: Functor<F>
): B {
  fun h(a: A): B = FF.run { alg(coalg(a).map(::h)) }
  return h(this)
}

/**
 * Hylomorphism over a composed functor
 */
fun <F, W, A, B> A.hyloC(
  alg: (Kind<F, Kind<W, B>>) -> B,
  coalg: (A) -> Kind<F, Kind<W, A>>,
  FF: Functor<F>,
  WF: Functor<W>
): B = hylo({
  alg(it.unnest())
}, {
  coalg(it).nest()
}, FF.compose(WF))

/**
 * Monadic hylomorphism, can be used to gain stacksafety when using a stack safe monad, but it requires a
 *  traverse instance and not just a functor.
 */
fun <F, M, A, B> A.hyloM(
  alg: AlgebraM<F, M, B>,
  coalg: CoalgebraM<F, M, A>,
  TF: Traverse<F>,
  MM: Monad<M>
): Kind<M, B> = hyloC({
  MM.run {
    it.flatMap {
      TF.run {
        it.sequence(MM).flatMap(alg)
      }
    }
  }
}, coalg, MM, TF)

/**
 * Monadic hylomorphism over composed traversables
 */
fun <F, W, M, A, B> A.hyloMC(
  alg: (Kind<F, Kind<W, B>>) -> Kind<M, B>,
  coalg: (A) -> Kind<M, Kind<F, Kind<W, A>>>,
  TF: Traverse<F>,
  TW: Traverse<W>,
  MM: Monad<M>
): Kind<M, B> = hyloM({
  alg(it.unnest())
}, {
  MM.run { coalg(it).map { it.nest() } }
}, TF.compose(TW), MM)

/**
 * Combination of futu and histo
 */
fun <F, A, B> A.chrono(
  alg: CVAlgebra<F, B>,
  coalg: CVCoalgebra<F, A>,
  FF: Functor<F>
): B =
  FreeF.pure<F, A>(this)
    .hylo<F, FreeR<F, A>, Cofree<F, B>>({
      Cofree(FF, alg(it), Eval.now(it))
    }, {
      when (val fa = it.unfix.fix()) {
        is FreeF.Pure -> coalg(fa.e)
        is FreeF.Impure -> FF.run { fa.fa.map { it.value().fix() } }
      }
    }, FF).head

/**
 * Monadic version of chrono
 */
fun <F, M, A, B> A.chronoM(
  alg: CVAlgebraM<F, M, B>,
  coalg: CVCoalgebraM<F, M, A>,
  TF: Traverse<F>,
  MM: Monad<M>
): Kind<M, B> =
  MM.run {
    FreeF.pure<F, A>(this@chronoM)
      .hyloM<F, M, FreeR<F, A>, Cofree<F, B>>(
        {
          alg(it).map { res -> Cofree(TF, res, Eval.now(it)) }
        },
        {
          when (val fa = it.unfix.fix()) {
            is FreeF.Pure -> coalg(fa.e)
            is FreeF.Impure -> just(TF.run { fa.fa.map { it.value().fix() } })
          }
        },
        TF, MM
      ).map { it.head }
  }

/**
 * Combination of ana + histo
 *
 * Useful to build up a recursive data structure and fold it with the implicit result caching histo provides.
 */
fun <F, A, B> A.dyna(
  alg: CVAlgebra<F, B>,
  coalg: Coalgebra<F, A>,
  FF: Functor<F>
): B =
  hylo<F, A, Cofree<F, B>>({
    Cofree(FF, alg(it), Eval.now(it))
  }, coalg, FF).head

/**
 * Monadic version of dyna
 */
fun <F, M, A, B> A.dynaM(
  alg: CVAlgebraM<F, M, B>,
  coalg: CoalgebraM<F, M, A>,
  TF: Traverse<F>,
  MM: Monad<M>
): Kind<M, B> =
  MM.run {
    hyloM<F, M, A, Cofree<F, B>>({
      alg(it).map { res -> Cofree(TF, res, Eval.now(it)) }
    }, coalg, TF, MM).map { it.head }
  }

/**
 * Refold, but with the ability to short circuit during construction
 */
fun <F, A, B> B.elgot(alg: Algebra<F, A>, f: (B) -> Either<A, Kind<F, B>>, FF: Functor<F>): A {
  fun h(b: B): A =
    f(b).fold(::identity) { FF.run { alg(it.map(::h)) } }

  return h(this)
}

/**
 * Monadic version of elgot
 */
fun <F, M, A, B> B.elgotM(alg: AlgebraM<F, M, A>, f: (B) -> Kind<M, Either<A, Kind<F, B>>>, TF: Traverse<F>, MM: Monad<M>): Kind<M, A> {
  fun h(b: B): Kind<M, A> = MM.run {
    f(b).flatMap { it.fold(MM::just) { MM.run { TF.run { it.traverse(MM, ::h).flatMap(alg) } } } }
  }
  return h(this)
}

/**
 * Refold but may short circuit at any time during deconstruction
 */
fun <F, A, B> A.coelgot(f: (Tuple2<A, Eval<Kind<F, B>>>) -> B, coalg: Coalgebra<F, A>, FF: Functor<F>): B {
  fun h(a: A): B =
    FF.run {
      f(
        Tuple2(a, Eval.later { coalg(a).map(::h) })
      )
    }
  return h(this)
}

/**
 * Monadic version of coelgot
 */
fun <F, M, A, B> A.coelgotM(f: (Tuple2<A, Eval<Kind<M, Kind<F, B>>>>) -> Kind<M, B>, coalg: CoalgebraM<F, M, A>, TF: Traverse<F>, MM: Monad<M>): Kind<M, B> {
  fun h(a: A): Kind<M, B> =
    TF.run {
      MM.run {
        f(
          Tuple2(a, Eval.later { coalg(a).flatMap { it.map(::h).sequence(MM) } })
        )
      }
    }

  return h(this)
}
