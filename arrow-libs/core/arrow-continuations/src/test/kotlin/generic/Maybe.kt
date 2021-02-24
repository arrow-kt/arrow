package generic

import arrow.continuations.Reset

sealed class Maybe<out A>
data class Just<A>(val a: A) : Maybe<A>()
object None : Maybe<Nothing>()

object maybeEff { // if you change the name to maybe it breaks the tests in 1.4.10
  inline fun <A> restricted(crossinline c: suspend MaybeEffect<*>.() -> A): Maybe<A> =
    Reset.restricted { Just(c(MaybeEffect { this })) }

  suspend inline operator fun <A> invoke(crossinline c: suspend MaybeEffect<*>.() -> A): Maybe<A> =
    Reset.suspended { Just(c(MaybeEffect { this })) }
}
