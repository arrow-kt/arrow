package arrow.fx.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.resume

internal inline infix fun <A, B, C> ((A) -> B).andThen(crossinline f: (B) -> C): (A) -> C =
  { a -> f(this(a)) }

internal fun Iterable<*>.size(): Int =
  when (this) {
    is Collection -> size
    else -> fold(0) { acc, _ -> acc + 1 }
  }

internal fun <A> (suspend () -> A).startCoroutineUnintercepted(completion: Continuation<A>): Unit =
  createCoroutineUnintercepted(completion).resume(Unit)

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}
