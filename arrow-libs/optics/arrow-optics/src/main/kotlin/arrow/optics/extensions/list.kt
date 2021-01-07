package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
import arrow.core.extensions.option.applicative.applicative
import arrow.core.k
import arrow.core.fix
import arrow.optics.POptional
import arrow.optics.PPrism
import arrow.optics.Prism
import arrow.optics.Traversal
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative

fun <A> ListExtensions.traversal(): Traversal<List<A>, A> = ListTraversal()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
interface ListTraversal<A> : Traversal<List<A>, A> {

  override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
    s.k().traverse(FA, f)

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListTraversal<A> {}
  }
}

fun <A> ListExtensions.each(): Each<List<A>, A> = listEach()

/**
 * [Each] instance definition for [List] that summons a [Traversal] to focus in each [A] of the source [List].
 */
fun <A> listEach(): Each<List<A>, A> = Each { ListTraversal() }

fun <A> ListExtensions.filterIndex(): FilterIndex<List<A>, Int, A> = listFilterIndex()

/**
 * [FilterIndex] instance definition for [List].
 */
fun <A> listFilterIndex(): FilterIndex<List<A>, Int, A> = FilterIndex { p ->
  object : Traversal<List<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else FA.just(a)
      }
  }
}

fun <A> ListExtensions.index(): Index<List<A>, Int, A> = listIndex()

/**
 * [Index] instance definition for [List].
 */
fun <A> listIndex(): Index<List<A>, Int, A> = Index { i ->
  POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
  )
}

fun <A> ListExtensions.cons(): Cons<List<A>, A> = listCons()

/**
 * [Cons] instance definition for [List].
 */
fun <A> listCons(): Cons<List<A>, A> = Cons {
  PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1)) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> listOf(a) + aas }
  )
}

fun <A> ListExtensions.snoc(): Snoc<List<A>, A> = listSnoc()

/**
 * [Snoc] instance definition for [List].
 */
fun <A> listSnoc(): Snoc<List<A>, A> = Snoc {
  object : Prism<List<A>, Tuple2<List<A>, A>> {
    override fun getOrModify(s: List<A>): Either<List<A>, Tuple2<List<A>, A>> =
      Option.applicative().mapN(Option.just(s.dropLast(1)), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<List<A>, A>): List<A> =
      b.a + b.b
  }
}
