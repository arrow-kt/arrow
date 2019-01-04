package arrow.effects.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

internal object ImmediateContext : ContinuationInterceptor {

  override val key: CoroutineContext.Key<*> = ContinuationInterceptor

  override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> = continuation

}
