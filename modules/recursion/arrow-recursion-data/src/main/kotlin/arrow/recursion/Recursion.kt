package arrow.recursion

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.free.Cofree
import arrow.recursion.data.fix
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.FreeR
import arrow.recursion.pattern.fix
import arrow.typeclasses.*

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
 * import arrow.core.Eval
 * import arrow.data.ListK
 * import arrow.data.k
 * import arrow.recursion.Algebra
 * import arrow.recursion.Coalgebra
 * import arrow.recursion.data.Fix
 * import arrow.recursion.hylo
 * import arrow.typeclasses.Functor
 * import kotlin.math.ceil
 * import kotlin.math.floor
 *
 * // @higherkind boilerplate. Not needed outside of docs when using kapt.
 * class ForBinaryTreeF private constructor()
 * typealias BinaryTreeFPartialOf<A> = Kind<ForBinaryTreeF, A>
 * typealias BinaryTreeOf<A, R> = Kind<BinaryTreeFPartialOf<A>, R>
 *
 * fun <A, R> BinaryTreeOf<A, R>.fix() = this as BinaryTreeF<A, R>
 *
 * sealed class BinaryTreeF<A, R> : BinaryTreeOf<A, R> {
 *  class Empty<A, R> : BinaryTreeF<A, R>()
 *  class Leaf<A, R>(val a: A) : BinaryTreeF<A, R>()
 *  class Node<A, R>(val left: R, val right: R) : BinaryTreeF<A, R>()
 *
 *  fun <B> map(f: (R) -> B): BinaryTreeF<A, B> = when (this) {
 *    is Empty -> Empty()
 *    is Leaf -> Leaf(a)
 *    is Node -> Node(f(left), f(right))
 *  }
 *
 *  companion object {
 *    fun <A> functor() = object : Functor<BinaryTreeFPartialOf<A>> {
 *      override fun <B, C> Kind<BinaryTreeFPartialOf<A>, B>.map(f: (B) -> C): Kind<BinaryTreeFPartialOf<A>, C> =
 *        fix().map(f)
 *    }
 *
 *    fun <A> empty(): BinaryTree<A> = Fix(Empty())
 *    fun <A> leaf(a: A): BinaryTree<A> = Fix(Leaf(a))
 *    fun <A> node(left: BinaryTree<A>, right: BinaryTree<A>): BinaryTree<A> = Fix(Node(Eval.now(left), Eval.now(right)))
 *  }
 * }
 *
 * typealias BinaryTree<A> = Fix<BinaryTreeFPartialOf<A>>
 *
 * infix fun ListK<Int>.merge(other: ListK<Int>): ListK<Int> = when {
 *  this.isEmpty() -> other
 *  other.isEmpty() -> this
 *  else ->
 *    if (first() > other.first()) (ListK.just(other.first()) + (this merge other.drop(1).k())).k()
 *    else (ListK.just(first()) + (this.drop(1).k() merge other)).k()
 *  }
 *
 * fun main() {
 *  val unfold: Coalgebra<BinaryTreeFPartialOf<Int>, ListK<Int>> = {
 *    when {
 *      it.isEmpty() -> BinaryTreeF.Empty()
 *      it.size == 1 -> BinaryTreeF.Leaf(it.first())
 *      else -> BinaryTreeF.Node(
 *        it.take(floor(it.size / 2.0).toInt()).k(),
 *        it.takeLast(ceil(it.size / 2.0).toInt()).k()
 *      )
 *    }
 *  }
 *
 *  val fold: Algebra<BinaryTreeFPartialOf<Int>, ListK<Int>> = {
 *    when (val fa = it.fix()) {
 *      is BinaryTreeF.Empty -> ListK.empty()
 *      is BinaryTreeF.Leaf -> ListK.just(fa.a)
 *      is BinaryTreeF.Node -> fa.left.k() merge fa.right.k()
 *    }
 *  }
 *
 * (0..100).shuffled().also(::println).k().hylo(fold, unfold, BinaryTreeF.functor())
 *  .toList().also(::println)
 * }
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

fun <F, W, M, A, B> A.hyloMC(
  alg: (Kind<F, Kind<W, B>>) -> Kind<M, B>,
  coalg: (A) -> Kind<M, Kind<F, Kind<W, A>>>,
  TF: Traverse<F>,
  TW: Traverse<W>,
  AW: Applicative<W>,
  MM: Monad<M>
): Kind<M, B> = hyloM({
  alg(it.unnest())
}, {
  MM.run { coalg(it).map { it.nest() } }
}, TF.compose(TW, AW), MM)

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

fun <F, A, B> A.dyna(
  alg: CVAlgebra<F, B>,
  coalg: Coalgebra<F, A>,
  FF: Functor<F>
): B =
  hylo<F, A, Cofree<F, B>>({
    Cofree(FF, alg(it), Eval.now(it))
  }, coalg, FF).head

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