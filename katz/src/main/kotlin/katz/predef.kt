package katz

import katz.typeclasses.*
import katz.typeclasses.HK
import katz.typeclasses.Monad
import katz.typeclasses.MonadError
import kotlin.coroutines.experimental.startCoroutine

/**
 * Package level exports
 */
typealias HK<F, A> = katz.typeclasses.HK<F, A>
typealias Functor<F> = katz.typeclasses.Functor<F>
typealias Applicative<F> = katz.typeclasses.Applicative<F>
typealias ApplicativeError<F, E> = katz.typeclasses.ApplicativeError<F, E>
typealias Monad<F> = katz.typeclasses.Monad<F>
typealias MonadError<F, E> = katz.typeclasses.MonadError<F, E>

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside `MonadContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    val f: suspend MonadContinuation<F, *>.() -> HK<F, B> = { c() }
    f.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}

/**
 * Entry point for monad bindings which enables for comprehensions. The underlying impl is based on coroutines.
 * A coroutines is initiated and inside `MonadErrorContinuation` suspended yielding to `flatMap` once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 *
 * This one operates over MonadError instances that can support `Throwable` in their error type automatically lifting
 * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
 */
fun <F, B> MonadError<F, Throwable>.binding(c: suspend MonadErrorContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    val f: suspend MonadErrorContinuation<F, *>.() -> HK<F, B> = { c() }
    f.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}
