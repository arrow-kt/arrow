package arrow.typeclasses

import arrow.Kind2

interface Category<F> {

  fun <A> id(): Kind2<F, A, A>

  fun <A, B, C> Kind2<F, B, C>.compose(arr: Kind2<F, A, B>): Kind2<F, A, C>

  fun <A, B, C> Kind2<F, A, B>.andThen(arr: Kind2<F, B, C>): Kind2<F, A, C> = arr.compose(this)
}