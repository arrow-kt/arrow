package arrow.aql

import arrow.core.None
import arrow.core.some
import arrow.typeclasses.FunctorFilter

interface Where<F> {

  fun functorFilter(): FunctorFilter<F>

  infix fun <A, Z> Query<F, A, Z>.where(predicate: A.() -> Boolean): Query<F, A, Z> =
    functorFilter().run {
      copy(from = from.filterMap {
        if (predicate(it)) it.some()
        else None
      })
    }

  infix fun <A, Z> Query<F, A, Z>.whereSelection(predicate: Z.() -> Boolean): Query<F, A, Z> =
    functorFilter().run {
      copy(from = from.filterMap {
        if (predicate(select(it))) it.some()
        else None
      })
    }

  infix fun <A, Z> Query<F, A, Z>.having(predicate: A.() -> Boolean): Query<F, A, Z> =
    functorFilter().run {
      copy(from = from.filterMap {
        if (predicate(it)) it.some()
        else None
      })
    }
}
