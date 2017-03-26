package katz

import katz.typeclasses.*
import katz.typeclasses.HK
import katz.typeclasses.Monad
import katz.typeclasses.MonadError
import kotlin.coroutines.experimental.startCoroutine

typealias HK<F, A> = katz.typeclasses.HK<F, A>
typealias Functor<F> = katz.typeclasses.Functor<F>
typealias Applicative<F> = katz.typeclasses.Applicative<F>
typealias ApplicativeError<F, E> = katz.typeclasses.ApplicativeError<F, E>
typealias Monad<F> = katz.typeclasses.Monad<F>
typealias MonadError<F, E> = katz.typeclasses.MonadError<F, E>

fun <F, B> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    val f: suspend MonadContinuation<F, *>.() -> HK<F, B> = { c() }
    f.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}

fun <F, B> MonadError<F, Throwable>.binding(c: suspend MonadErrorContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    val f: suspend MonadErrorContinuation<F, *>.() -> HK<F, B> = { c() }
    f.startCoroutine(continuation, continuation)
    return continuation.returnedMonad
}
