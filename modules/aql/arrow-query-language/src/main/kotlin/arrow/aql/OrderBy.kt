package arrow.aql

import arrow.core.ForId
import arrow.core.identity
import arrow.core.value
import arrow.instances.id.applicative.just
import arrow.typeclasses.Foldable
import arrow.typeclasses.Order

sealed class Ord {
  object Asc : Ord()
  object Desc : Ord()
}

interface OrderBy<F> {

  fun foldable(): Foldable<F>

  fun <A, Z : Comparable<Z>> Query<F, A, Z>.order(direction: Ord): Query<ForId, List<Z>, List<Z>> =
    foldable().run {
      val list = from.foldLeft(emptyList<Z>()) { list, a -> list + select(a) }
      Query(
        select = ::identity,
        from = when (direction) {
          Ord.Asc -> list.sorted()
          Ord.Desc -> list.sortedDescending()
        }.just()
      )
    }

  private data class DelegatingComparator<A>(val order: Order<A>, val direction: Ord) : Comparator<A> {
    override fun compare(a: A, other: A): Int = order.run {
      val ord = a.compareTo(other)
      when (direction) {
        Ord.Asc -> ord
        Ord.Desc -> -ord
      }
    }
  }

  fun <A, Z> Query<F, A, Z>.order(direction: Ord, order: Order<Z>, dummy: Unit = Unit): Query<ForId, List<Z>, List<Z>> =
    foldable().run {
      Query(
        select = ::identity,
        from = from.foldLeft(emptyList<Z>()) { list, a ->
          list + select(a)
        }.sortedWith(DelegatingComparator(order, direction)).just()
      )
    }

  fun <X, Z> Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>>.order(keyOrder: Ord, order: Order<X>): Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>> {
    val sortedMap = from.value().toSortedMap(kotlin.Comparator { o1, o2 ->
      val result = order.run { o1.compareTo(o2) }
      when (keyOrder) {
        Ord.Asc -> -result
        Ord.Desc -> result
      }
    })
    return Query(
      select = ::identity,
      from = sortedMap.toMap().just()
    )
  }

  fun <Z> Query<ForId, List<Z>, List<Z>>.value(): List<Z> =
    this@value.from.value()

  fun <Z, X> Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>>.value(dummy: Unit = Unit): Map<X, List<Z>> =
    this@value.from.value()

}