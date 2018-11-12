package arrow.optics.instances

import arrow.Kind
import arrow.core.*
import arrow.data.k
import arrow.data.traverse
import arrow.optics.*
import arrow.optics.typeclasses.Cons
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

fun <A> ListInstances.traversal(): Traversal<List<A>, A> = ListTraversal()

/**
 * [Traversal] for [List] that focuses in each [A] of the source [List].
 */
interface ListTraversal<A> : Traversal<List<A>, A> {

  override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> = FA.run {
    s.k().traverse(FA, f).map { it.list }
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListTraversal<A> {}
  }

}

fun <A> ListInstances.each(): Each<List<A>, A> = ListEachInstance()

/**
 * [Each] instance definition for [List] that summons a [Traversal] to focus in each [A] of the source [List].
 */
interface ListEachInstance<A> : Each<List<A>, A> {
  override fun each() = ListTraversal<A>()

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListEachInstance<A> {}
  }
}

fun <A> ListInstances.filterIndex(): FilterIndex<List<A>, Int, A> = ListFilterIndexInstance()

/**
 * [FilterIndex] instance definition for [List].
 */
interface ListFilterIndexInstance<A> : FilterIndex<List<A>, Int, A> {
  override fun filter(p: (Int) -> Boolean): Traversal<List<A>, A> = object : Traversal<List<A>, A> {
    override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> = FA.run {
      s.mapIndexed { index, a -> a toT index }.k().traverse(FA) { (a, j) ->
        if (p(j)) f(a) else just(a)
      }.map { it.list }
    }
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <A> invoke() = object : ListFilterIndexInstance<A> {}
  }
}

fun <A> ListInstances.index(): Index<List<A>, Int, A> = ListIndexInstance()

/**
 * [Index] instance definition for [List].
 */
interface ListIndexInstance<A> : Index<List<A>, Int, A> {
  override fun index(i: Int): Optional<List<A>, A> = POptional(
    getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
    set = { a -> { l -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } } }
  )

  companion object {

    operator fun <A> invoke() = object : ListIndexInstance<A> {}
  }
}

fun <A> ListInstances.cons(): Cons<List<A>, A> = ListConsInstance()

/**
 * [Cons] instance definition for [List].
 */
interface ListConsInstance<A> : Cons<List<A>, A> {
  override fun cons(): Prism<List<A>, Tuple2<A, List<A>>> = PPrism(
    getOrModify = { list -> list.firstOrNull()?.let { Tuple2(it, list.drop(1)) }?.right() ?: list.left() },
    reverseGet = { (a, aas) -> listOf(a) + aas }
  )

  companion object {

    operator fun <A> invoke() = object : ListConsInstance<A> {}
  }
}
