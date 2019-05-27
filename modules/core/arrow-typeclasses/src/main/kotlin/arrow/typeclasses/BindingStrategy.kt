package arrow.typeclasses

import arrow.Kind

internal class ContinuationShortcircuitThrowable(val exit: Kind<Any?, Any?>) : Throwable() {
  override fun fillInStackTrace(): Throwable = this
}

sealed class BindingStrategy<out F, out A> {
  data class ContinuationShortCircuit<out F, out A>(val exit: Kind<F, A>) : BindingStrategy<F, A>() {
    val throwable: Throwable = ContinuationShortcircuitThrowable(exit)
  }
  object MultiShot : BindingStrategy<Nothing, Nothing>()
  data class Strict<out F, out A>(val a: A) : BindingStrategy<F, A>()
  data class Suspend<F, A>(val f: suspend () -> A) : BindingStrategy<F, A>()
}
