package arrow.aql

import arrow.core.*
import arrow.data.ForListK
import arrow.data.fix
import arrow.data.mapOf
import arrow.typeclasses.Foldable

interface GroupBy<F> {

  fun foldable(): Foldable<F>

  infix fun <A, Z, X> Query<F, A, Z>.groupBy(group: Z.() -> X): Query<ForId, Map<X, List<Z>>, Map<X, List<Z>>> =
    foldable().run {
      Query(select = ::identity, from =
      Id.just(from.foldLeft(emptyMap()) { map, a ->
        val z: Z = select.invoke(a)
        val key: X = group(z)
        val grouped: List<Z> = map[key]?.let { it + z } ?: listOf(z)
        map + mapOf(key toT grouped)
      }))
    }

  fun <Z, X> Query<ForListK, Map<X, List<Z>>, Map<X, List<Z>>>.value(): Map<X, List<Z>> =
    foldable().run {
      this@value.from.fix().firstOrNone().getOrElse { emptyMap() }
    }

}