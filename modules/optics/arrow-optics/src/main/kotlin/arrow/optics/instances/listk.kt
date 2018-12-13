package arrow.optics.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.extension
import arrow.instances.option.applicative.applicative
import arrow.optics.*
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [ListK] that has focus in each [A].
 *
 * @receiver [ListK.Companion] to make it statically available.
 * @return [Traversal] with source [ListK] and focus every [A] of the source.
 */
fun <A> ListK.Companion.traversal(): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [ListK].
 */
@extension
interface ListKEachInstance<A> : Each<ListK<A>, A> {
  override fun each(): Traversal<ListK<A>, A> =
    ListK.traversal()
}

/**
 * [FilterIndex] instance definition for [ListK].
 */
@extension
interface ListKFilterIndexInstance<A> : FilterIndex<ListK<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else just(a)
      }
    }
  }
}

/**
 * [Index] instance definition for [ListK].
 */
@extension
interface ListKIndexInstance<A> : Index<ListK<A>, Int, A> {
  override fun index(i: Int): Optional<ListK<A>, A> = POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() }
  )
}

/**
 * [Cons] instance definition for [ListK].
 */
@extension
interface ListKConsInstance<A> : Cons<ListK<A>, A> {
  override fun cons(): Prism<ListK<A>, Tuple2<A, ListK<A>>> = PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1).k()) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> ListK(listOf(a) + aas) }
  )
}

/**
 * [Snoc] instance definition for [ListK].
 */
@extension
interface ListKSnocInstance<A> : Snoc<ListK<A>, A> {

  override fun snoc() = object : Prism<ListK<A>, Tuple2<ListK<A>, A>> {
    override fun getOrModify(s: ListK<A>): Either<ListK<A>, Tuple2<ListK<A>, A>> =
      Option.applicative().map(Try { s.dropLast(1).k() }.toOption(), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<ListK<A>, A>): ListK<A> =
      ListK(b.a + b.b)
  }

}
