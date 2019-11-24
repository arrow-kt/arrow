package arrow.typeclasses

import arrow.Kind

interface Repeat<F> : Zip<F> {
  fun <A> repeat(a: A): Kind<F, A>
}
