package arrow.typeclasses

import arrow.Kind
import arrow.TC
import arrow.core.Either
import arrow.core.Eval
import arrow.typeclass
import kotlin.coroutines.experimental.startCoroutine

@typeclass
interface Monad<F> : Applicative<F>, TC {

    fun <A, B> flatMap(fa: Kind<F, A>, f: (A) -> Kind<F, B>): Kind<F, B>

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> = flatMap(fa, { a -> pure(f(a)) })

    override fun <A, B> ap(fa: Kind<F, A>, ff: Kind<F, (A) -> B>): Kind<F, B> = flatMap(ff, { f -> map(fa, f) })

    fun <A> flatten(ffa: Kind<F, Kind<F, A>>): Kind<F, A> = flatMap(ffa, { it })

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B>

    fun <A, B> followedBy(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, B> = flatMap(fa, { fb })

    fun <A, B> followedByEval(fa: Kind<F, A>, fb: Eval<Kind<F, B>>): Kind<F, B> = flatMap(fa, { fb.value() })

    fun <A, B> forEffect(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, A> = flatMap(fa, { a -> map(fb, { a }) })

    fun <A, B> forEffectEval(fa: Kind<F, A>, fb: Eval<Kind<F, B>>): Kind<F, A> = flatMap(fa, { a -> map(fb.value(), { a }) })
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying implementation is based on coroutines.
 * A coroutine is initiated and suspended inside [MonadErrorContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    val wrapReturn: suspend MonadContinuation<F, *>.() -> Kind<F, B> = { pure(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}
