package arrow.optics

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Nullable
import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.optics.typeclasses.Snoc
import arrow.typeclasses.Monoid

/**
 * [Optional] to safely operate on the head of a list
 */
fun <A> POptional.Companion.listHead(): Optional<List<A>, A> =
  Optional(
    getOption = { Option.fromNullable(it.firstOrNull()) },
    set = { list, newHead -> list.mapIndexed { index, value -> if (index == 0) newHead else value } }
  )

/**
 * [Optional] to safely operate on the tail of a list
 */
fun <A> POptional.Companion.listTail(): Optional<List<A>, List<A>> =
  Optional(
    getOption = { if (it.isEmpty()) None else Some(it.drop(1)) },
    set = { list, newTail ->
      list.firstOrNull()?.let {
        listOf(it) + newTail
      } ?: emptyList()
    }
  )

/**
 * [PIso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A, B> PIso.Companion.listToPOptionNel(): PIso<List<A>, List<B>, Option<NonEmptyList<A>>, Option<NonEmptyList<B>>> =
  PIso(
    get = { aas -> if (aas.isEmpty()) None else Some(NonEmptyList(aas.first(), aas.drop(1))) },
    reverseGet = { optNel -> optNel.fold({ emptyList() }, NonEmptyList<B>::all) }
  )

/**
 * [Iso] that defines equality between a [List] and [Option] [NonEmptyList]
 */
fun <A> PIso.Companion.listToOptionNel(): Iso<List<A>, Option<NonEmptyList<A>>> =
  listToPOptionNel()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
fun <A> PTraversal.Companion.list(): Traversal<List<A>, A> =
  Every.list()

fun <A> Fold.Companion.list(): Fold<List<A>, A> =
  Every.list()

fun <A> PEvery.Companion.list(): Every<List<A>, A> = object : Every<List<A>, A> {
  override fun <R> foldMap(M: Monoid<R>, s: List<A>, map: (A) -> R): R =
    M.run { s.fold(empty()) { acc, a -> acc.combine(map(a)) } }

  override fun modify(s: List<A>, map: (focus: A) -> A): List<A> =
    s.map(map)
}

/**
 * [FilterIndex] instance definition for [List].
 */
fun <A> FilterIndex.Companion.list(): FilterIndex<List<A>, Int, A> =
  FilterIndex { p ->
    object : Every<List<A>, A> {
      override fun <R> foldMap(M: Monoid<R>, s: List<A>, map: (A) -> R): R = M.run {
        s.foldIndexed(empty()) { index, acc, a -> if (p(index)) acc.combine(map(a)) else acc }
      }

      override fun modify(s: List<A>, map: (focus: A) -> A): List<A> =
        s.mapIndexed { index, a -> if (p(index)) map(a) else a }
    }
  }

/**
 * [Index] instance definition for [List].
 */
fun <A> Index.Companion.list(): Index<List<A>, Int, A> =
  Index { i ->
    POptional(
      getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
      set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
    )
  }

operator fun <A, T> PLens<T, T, List<A>, List<A>>.get(i: Int): POptional<T, T, A, A> =
  Index.list<A>().run { this@get.get(i) }

/**
 * [Cons] instance definition for [List].
 */
fun <A> Cons.Companion.list(): Cons<List<A>, A> =
  Cons {
    PPrism(
      getOrModify = { list -> list.firstOrNull()?.let { Pair(it, list.drop(1)) }?.right() ?: list.left() },
      reverseGet = { (a, aas) -> listOf(a) + aas }
    )
  }

infix fun <A> A.cons(tail: List<A>): List<A> =
  Cons.list<A>().run { this@cons.cons(tail) }

fun <A> List<A>.uncons(): Pair<A, List<A>>? =
  Cons.list<A>().run { this@uncons.uncons() }

fun <A> Snoc.Companion.list(): Snoc<List<A>, A> = Snoc {
  object : Prism<List<A>, Pair<List<A>, A>> {
    override fun getOrModify(s: List<A>): Either<List<A>, Pair<List<A>, A>> =
      Nullable.mapN(s.dropLast(1), s.lastOrNull()) { a, b ->
        Pair(a, b).right()
      } ?: s.left()

    override fun reverseGet(b: Pair<List<A>, A>): List<A> =
      b.first + b.second
  }
}

infix fun <A> List<A>.snoc(last: A): List<A> =
  Snoc.list<A>().run { this@snoc.snoc(last) }

fun <A> List<A>.unsnoc(): Pair<List<A>, A>? =
  Snoc.list<A>().run { this@unsnoc.unsnoc() }
