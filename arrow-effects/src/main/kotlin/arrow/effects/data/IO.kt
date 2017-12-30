package arrow.effects

import arrow.*
import arrow.core.*
import arrow.core.Either.Left
import arrow.effects.internal.Platform.onceOnly
import arrow.effects.internal.Platform.unsafeResync
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        AsyncContext::class)
sealed class IO<out A> : IOKind<A> {

    companion object {

        fun <A> pure(a: A): IO<A> = Pure(a)

        fun <A> raiseError(e: Throwable): IO<A> = RaiseError(e)

        internal fun <A, B> mapDefault(t: IO<A>, f: (A) -> B): IO<B> = Map(t, f, 0)

        operator fun <A> invoke(f: () -> A): IO<A> = suspend { Pure(f()) }

        fun <A> suspend(f: () -> IO<A>): IO<A> = Suspend(f)

        fun <A> runAsync(k: Proc<A>): IO<A> =
                Async { ff: (Either<Throwable, A>) -> Unit ->
                    onceOnly(ff).let { callback: (Either<Throwable, A>) -> Unit ->
                        try {
                            k(callback)
                        } catch (throwable: Throwable) {
                            callback(Left(throwable))
                        }
                    }
                }

        val unit: IO<Unit> =
                pure(Unit)

        fun <A> eval(eval: Eval<A>): IO<A> =
                when (eval) {
                    is Eval.Now -> pure(eval.value)
                    else -> invoke { eval.value() }
                }

        fun <A, B> tailRecM(a: A, f: (A) -> IOKind<Either<A, B>>): IO<B> =
                f(a).ev().flatMap {
                    when (it) {
                        is Either.Left -> tailRecM(it.a, f)
                        is Either.Right -> IO.pure(it.b)
                    }
                }
    }

    abstract fun <B> map(f: (A) -> B): IO<B>

    fun <B> flatMap(f: (A) -> IOKind<B>): IO<B> =
            Bind(this, { f(it).ev() })

    fun attempt(): IO<Either<Throwable, A>> =
            Bind(this, IOFrame.any())

    fun runAsync(cb: (Either<Throwable, A>) -> IO<Unit>): IO<Unit> =
            IO { unsafeRunAsync(cb.andThen { it.unsafeRunAsync { } }) }

    fun unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
            IORunLoop.start(this, cb)

    fun unsafeRunSync(): A =
            unsafeRunTimed(Duration.INFINITE)
                    .fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, { it })

    fun unsafeRunTimed(limit: Duration): Option<A> = IORunLoop.step(this).unsafeRunTimedTotal(limit)

    internal abstract fun unsafeRunTimedTotal(limit: Duration): Option<A>
}

internal data class Pure<out A>(val a: A) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> = mapDefault(this, f)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = Some(a)
}

internal data class RaiseError(val exception: Throwable) : IO<Nothing>() {
    override fun <B> map(f: (Nothing) -> B): IO<B> = this

    override fun unsafeRunTimedTotal(limit: Duration): Option<Nothing> = throw exception
}

internal data class Delay<out A>(val thunk: () -> A) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> = mapDefault(this, f)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
}

internal data class Suspend<out A>(val thunk: () -> IO<A>) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> = mapDefault(this, f)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
}

internal data class Async<out A>(val cont: Proc<A>) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> = mapDefault(this, f)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = unsafeResync(this, limit)
}

internal data class Bind<E, out A>(val cont: IO<E>, val g: (E) -> IO<A>) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> = mapDefault(this, f)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
}

internal data class Map<E, out A>(val source: IO<E>, val g: (E) -> A, val index: Int) : IO<A>(), (E) -> IO<A> {
    override fun invoke(value: E): IO<A> = pure(g(value))

    override fun <B> map(f: (A) -> B): IO<B> =
            // Allowed to do 32 map operations in sequence before
            // triggering `flatMap` in order to avoid stack overflows
            if (index != 31) Map(source, g.andThen(f), index + 1)
            else flatMap { a -> Pure(f(a)) }

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
}

fun <A, B> IO<A>.ap(ff: IOKind<(A) -> B>): IO<B> =
        flatMap { a -> ff.ev().map({ it(a) }) }

fun <A> IO<A>.handleErrorWith(f: (Throwable) -> IOKind<A>): IO<A> =
        Bind(this, IOFrame.errorHandler(f))

fun <A> A.liftIO(): IO<A> = IO.pure(this)
