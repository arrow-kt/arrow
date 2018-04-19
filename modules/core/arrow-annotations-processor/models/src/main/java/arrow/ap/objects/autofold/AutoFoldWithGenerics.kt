package arrow.ap.objects.autofold

import arrow.autofold

@autofold
sealed class AutoFoldWithGenerics<out A, out B, out C> {
  data class First<A>(val a: A) : AutoFoldWithGenerics<A, Nothing, Nothing>()
  data class Second<A, B>(val a: A, val b: B) : AutoFoldWithGenerics<A, B, Nothing>()
}