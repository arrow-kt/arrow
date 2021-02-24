package arrow.optics.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.ListExtensions
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import arrow.core.toT
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

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListExtensions.traversal(): Traversal<List<A>, A> = ListTraversal()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
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

@Deprecated(
  "Each is being deprecated. Use the instance for List from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListExtensions.each(): Each<List<A>, A> = ListEach()

/**
 * [Each] instance definition for [List] that summons a [Traversal] to focus in each [A] of the source [List].
 */
@Deprecated(
  "Each is being deprecated. Use the instance for List from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
interface ListEach<A> : Each<List<A>, A> {
  override fun each() = ListTraversal<A>()

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListEach<A> {}
  }
}
@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "FilterIndex.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListExtensions.filterIndex(): FilterIndex<List<A>, Int, A> = ListFilterIndex()

/**
 * [FilterIndex] instance definition for [List].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "FilterIndex.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
interface ListFilterIndex<A> : FilterIndex<List<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<List<A>, A> = object : Traversal<List<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else FA.just(a)
      }
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListFilterIndex<A> {}
  }
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "Index.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListExtensions.index(): Index<List<A>, Int, A> = ListIndex()

/**
 * [Index] instance definition for [List].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Index.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
interface ListIndex<A> : Index<List<A>, Int, A> {
  override fun index(i: Int): Optional<List<A>, A> = POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
  )

  companion object {

    operator fun <A> invoke() = object : ListIndex<A> {}
  }
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "Cons.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Cons"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListExtensions.cons(): Cons<List<A>, A> = ListCons()

/**
 * [Cons] instance definition for [List].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Cons.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Cons"
  ),
  DeprecationLevel.WARNING
)
interface ListCons<A> : Cons<List<A>, A> {
  override fun cons(): Prism<List<A>, Tuple2<A, List<A>>> = PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1)) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> listOf(a) + aas }
  )

  companion object {

    operator fun <A> invoke() = object : ListCons<A> {}
  }
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "Snoc.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListExtensions.snoc(): Snoc<List<A>, A> = ListSnoc()

/**
 * [Snoc] instance definition for [List].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Snoc.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
  ),
  DeprecationLevel.WARNING
)
interface ListSnoc<A> : Snoc<List<A>, A> {

  override fun snoc() = object : Prism<List<A>, Tuple2<List<A>, A>> {
    override fun getOrModify(s: List<A>): Either<List<A>, Tuple2<List<A>, A>> =
      Option.applicative().mapN(Option.just(s.dropLast(1)), s.lastOrNull().toOption(), ::identity)
        .fix()
        .toEither { s }

    override fun reverseGet(b: Tuple2<List<A>, A>): List<A> =
      b.a + b.b
  }

  companion object {

    operator fun <A> invoke() = object : ListSnoc<A> {}
  }
}
