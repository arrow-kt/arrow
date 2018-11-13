package arrow.optics.instances

import arrow.Kind
import arrow.core.ListInstances
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.data.k
import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.Traversal
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
    s.k().traverse(FA, f)
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
      }
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
    set = { l, a -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } }
  )

  companion object {

    operator fun <A> invoke() = object : ListIndexInstance<A> {}
  }
}
