package arrow.typeclasses

import arrow.Kind
import arrow.TC
import arrow.core.identity
import arrow.typeclass
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.MonadErrorBlockingContinuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext

@typeclass
interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F>, TC {

    fun <A> ensure(fa: Kind<F, A>, error: () -> E, predicate: (A) -> Boolean): Kind<F, A> =
            flatMap(fa, {
                if (predicate(it)) pure(it)
                else raiseError(error())
            })

    /**
     * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
     * A coroutine is initiated and suspended inside [BindingCatchContinuation] yielding to [Monad.flatMap] or [Monad.flatMapIn].
     * Once all the binds are completed the underlying data type is returned from the act of executing the coroutine.
     *
     * These for comprehensions enable working for any error [E] as long as a conversion function from any [Throwable].
     */
    fun <B> bindingCatch(context: CoroutineContext = EmptyCoroutineContext, catch: (Throwable) -> E, c: suspend BindingCatchContinuation<F, E, *>.() -> B): Kind<F, B> =
            MonadErrorBlockingContinuation.bindingCatch(this, catch, context) { pure(c(it)) }
}

fun <F, B> MonadError<F, Throwable>.bindingCatch(context: CoroutineContext, c: suspend BindingCatchContinuation<F, Throwable, *>.() -> B): Kind<F, B> =
        bindingCatch(context, ::identity, c)
