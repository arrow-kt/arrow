package generic

import arrow.continuations.Effect

fun interface MaybeEffect<A> : Effect<Maybe<A>> {
  suspend fun <B> Maybe<B>.bind(): B =
    when (this) {
      is Just -> a
      is None -> control().shift(None)
    }
}
