package arrow.ap.objects.autofold

import arrow.autofold

@autofold
sealed class FailGenerics<A> {
  data class Second<A, B>(val a: A, val b: B) : FailGenerics<A>()
}
