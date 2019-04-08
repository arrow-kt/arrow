import arrow.Kind
import arrow.core.Eval
import arrow.data.ListK
import arrow.data.k
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.data.Fix
import arrow.recursion.hylo
import arrow.typeclasses.Functor
import kotlin.math.ceil
import kotlin.math.floor

// @higherkind boilerplate. Not needed outside of docs when using kapt.
class ForBinaryTreeF private constructor()
typealias BinaryTreeFPartialOf<A> = Kind<ForBinaryTreeF, A>
typealias BinaryTreeOf<A, R> = Kind<BinaryTreeFPartialOf<A>, R>

fun <A, R> BinaryTreeOf<A, R>.fix() = this as BinaryTreeF<A, R>

sealed class BinaryTreeF<A, R> : BinaryTreeOf<A, R> {
  class Empty<A, R> : BinaryTreeF<A, R>()
  class Leaf<A, R>(val a: A) : BinaryTreeF<A, R>()
  class Node<A, R>(val left: R, val right: R) : BinaryTreeF<A, R>()

  fun <B> map(f: (R) -> B): BinaryTreeF<A, B> = when (this) {
    is Empty -> Empty()
    is Leaf -> Leaf(a)
    is Node -> Node(f(left), f(right))
  }

  companion object {
    fun <A> functor() = object : Functor<BinaryTreeFPartialOf<A>> {
      override fun <B, C> Kind<BinaryTreeFPartialOf<A>, B>.map(f: (B) -> C): Kind<BinaryTreeFPartialOf<A>, C> =
        fix().map(f)
    }

    fun <A> empty(): BinaryTree<A> = Fix(Empty())
    fun <A> leaf(a: A): BinaryTree<A> = Fix(Leaf(a))
    fun <A> node(left: BinaryTree<A>, right: BinaryTree<A>): BinaryTree<A> = Fix(Node(Eval.now(left), Eval.now(right)))
  }
}

typealias BinaryTree<A> = Fix<BinaryTreeFPartialOf<A>>

infix fun ListK<Int>.merge(other: ListK<Int>): ListK<Int> = when {
  this.isEmpty() -> other
  other.isEmpty() -> this
  else ->
    if (first() > other.first()) (ListK.just(other.first()) + (this merge other.drop(1).k())).k()
    else (ListK.just(first()) + (this.drop(1).k() merge other)).k()
}

fun main() {
  val unfold: Coalgebra<BinaryTreeFPartialOf<Int>, ListK<Int>> = {
    when {
      it.isEmpty() -> BinaryTreeF.Empty()
      it.size == 1 -> BinaryTreeF.Leaf(it.first())
      else -> BinaryTreeF.Node(
        it.take(floor(it.size / 2.0).toInt()).k(),
        it.takeLast(ceil(it.size / 2.0).toInt()).k()
      )
    }
  }

  val fold: Algebra<BinaryTreeFPartialOf<Int>, ListK<Int>> = {
    when (val fa = it.fix()) {
      is BinaryTreeF.Empty -> ListK.empty()
      is BinaryTreeF.Leaf -> ListK.just(fa.a)
      is BinaryTreeF.Node -> fa.left.k() merge fa.right.k()
    }
  }

  (0..9).shuffled().also(::println).k().hylo(fold, unfold, BinaryTreeF.functor())
    .toList().also(::println)
}