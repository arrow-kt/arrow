package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
import arrow.core.ListK
import arrow.core.k
import arrow.core.fix
import arrow.core.extensions.option.applicative.applicative
import arrow.optics.Optional
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

/**
 * [Traversal] for [ListK] that has focus in each [A].
 *
 * @receiver [ListK.Companion] to make it statically available.
 * @return [Traversal] with source [ListK] and focus every [A] of the source.
 */
@Deprecated(
  "ListK is being deprecated. Use the instance for List from the companion object of the typeclass.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK.Companion.traversal(): Traversal<ListK<A>, A> = object : Traversal<ListK<A>, A> {
  override fun <F> modifyF(FA: Applicative<F>, s: ListK<A>, f: (A) -> Kind<F, A>): Kind<F, ListK<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [ListK].
 */
@Deprecated(
  "Each is being deprecated. Use the instance for List from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
interface ListKEach<A> : Each<ListK<A>, A> {
  override fun each(): Traversal<ListK<A>, A> =
    ListK.traversal()
}
/**
 * [FilterIndex] instance definition for [ListK].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore",
  ReplaceWith(
    "FilterIndex.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
interface ListKFilterIndex<A> : FilterIndex<ListK<A>, Int, A> {
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
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore",
  ReplaceWith(
    "Index.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
interface ListKIndex<A> : Index<ListK<A>, Int, A> {
  override fun index(i: Int): Optional<ListK<A>, A> = POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa }.k() }
  )
}

/**
 * [Cons] instance definition for [ListK].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore",
  ReplaceWith(
    "Cons.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Cons"
  ),
  DeprecationLevel.WARNING
)
interface ListKCons<A> : Cons<ListK<A>, A> {
  override fun cons(): Prism<ListK<A>, Tuple2<A, ListK<A>>> = PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1).k()) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> ListK(listOf(a) + aas) }
  )
}

/**
 * [Snoc] instance definition for [ListK].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore",
  ReplaceWith(
    "Snoc.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
  ),
  DeprecationLevel.WARNING
)
interface ListKSnoc<A> : Snoc<ListK<A>, A> {
  override fun snoc() = object : Prism<ListK<A>, Tuple2<ListK<A>, A>> {
    override fun getOrModify(s: ListK<A>): Either<ListK<A>, Tuple2<ListK<A>, A>> =
      Option.applicative().mapN(Option.just(s.dropLast(1).k()), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<ListK<A>, A>): ListK<A> =
      ListK(b.a + b.b)
  }
}
