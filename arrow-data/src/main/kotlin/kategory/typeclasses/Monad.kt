package arrow

import kotlin.coroutines.experimental.startCoroutine

interface Monad<F> : Applicative<F>, Typeclass {

    fun <A, B> flatMap(fa: HK<F, A>, f: (A) -> HK<F, B>): HK<F, B>

    override fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B> = flatMap(ff, { f -> map(fa, f) })

    fun <A> flatten(ffa: HK<F, HK<F, A>>): HK<F, A> = flatMap(ffa, { it })

    fun <A, B> tailRecM(a: A, f: (A) -> HK<F, Either<A, B>>): HK<F, B>

    fun <A, B> followedBy(fa: HK<F, A>, fb: HK<F, B>): HK<F, B> = flatMap(fa, { fb })

    fun <A, B> followedByEval(fa: HK<F, A>, fb: Eval<HK<F, B>>): HK<F, B> = flatMap(fa, { fb.value() })

    fun <A, B> forEffect(fa: HK<F, A>, fb: HK<F, B>): HK<F, A> = flatMap(fa, { a -> map(fb, { a }) })

    fun <A, B> forEffectEval(fa: HK<F, A>, fb: Eval<HK<F, B>>): HK<F, A> = flatMap(fa, { a -> map(fb.value(), { a }) })
}

inline fun <F, A, B> Monad<F>.mproduct(fa: HK<F, A>, crossinline f: (A) -> HK<F, B>): HK<F, Tuple2<A, B>> =
        flatMap(fa, { a -> map(f(a), { a toT it }) })

inline fun <F, B> Monad<F>.ifM(fa: HK<F, Boolean>, crossinline ifTrue: () -> HK<F, B>, crossinline ifFalse: () -> HK<F, B>): HK<F, B> =
        flatMap(fa, { if (it) ifTrue() else ifFalse() })

inline fun <reified F, A, B> HK<F, A>.flatMap(FT: Monad<F> = monad(), noinline f: (A) -> HK<F, B>): HK<F, B> = FT.flatMap(this, f)

inline fun <reified F, A> HK<F, HK<F, A>>.flatten(FT: Monad<F> = monad()): HK<F, A> = FT.flatten(this)

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside [MonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F, B> Monad<F>.binding(c: suspend MonadContinuation<F, *>.() -> HK<F, B>): HK<F, B> {
    val continuation = MonadContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside [StackSafeMonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine.
 *
 * This combinator ultimately returns computations lifting to [Free] to automatically for comprehend in a stack-safe way
 * over any stack-unsafe monads.
 */
fun <F, B> Monad<F>.bindingStackSafe(c: suspend StackSafeMonadContinuation<F, *>.() -> Free<F, B>):
        Free<F, B> {
    val continuation = StackSafeMonadContinuation<F, B>(this)
    c.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
}

inline fun <reified F> monad(): Monad<F> = instance(InstanceParametrizedType(Monad::class.java, listOf(typeLiteral<F>())))
