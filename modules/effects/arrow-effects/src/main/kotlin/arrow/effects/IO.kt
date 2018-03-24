package arrow.effects

import arrow.core.*
import arrow.core.Either.Left
import arrow.effects.internal.Platform.maxStackDepthSize
import arrow.effects.internal.Platform.onceOnly
import arrow.effects.internal.Platform.unsafeResync
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.Proc
import arrow.higherkind

@higherkind
sealed class IO<out A> : IOOf<A> {

    companion object {

        fun <A> pure(a: A): IO<A> = Pure(a)

        fun <A> raiseError(e: Throwable): IO<A> = RaiseError(e)

        internal fun <A, B> mapDefault(t: IOOf<A>, f: (A) -> B): IO<B> = Map(t, f, 0)

        operator fun <A> invoke(f: () -> A): IO<A> = suspend { Pure(f()) }

        fun <A> suspend(f: () -> IOOf<A>): IO<A> = Suspend(f)

        fun <A> async(k: Proc<A>): IO<A> =
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

        val lazy: IO<Unit> =
                invoke { }

        fun <A> eval(eval: Eval<A>): IO<A> =
                when (eval) {
                    is Eval.Now -> pure(eval.value)
                    else -> invoke { eval.value() }
                }

        fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
                f(a).fix().flatMap {
                    when (it) {
                        is Either.Left -> tailRecM(it.a, f)
                        is Either.Right -> IO.pure(it.b)
                    }
                }
    }

    abstract fun <B> map(f: (A) -> B): IO<B>

    fun <B> flatMap(f: (A) -> IOOf<B>): IO<B> =
            Bind(this, { f(it).fix() })

    fun attempt(): IO<Either<Throwable, A>> =
            Bind(this, IOFrame.any())

    fun runAsync(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
            IO { unsafeRunAsync(cb.andThen { it.fix().unsafeRunAsync { } }) }

    fun unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
            IORunLoop.start(this, cb)

    fun unsafeRunSync(): A =
            unsafeRunTimed(Duration.INFINITE)
                    .fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, { it })

    fun unsafeRunTimed(limit: Duration): Option<A> = IORunLoop.step(this).unsafeRunTimedTotal(limit)

    internal abstract fun unsafeRunTimedTotal(limit: Duration): Option<A>

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

    internal data class Suspend<out A>(val thunk: () -> IOOf<A>) : IO<A>() {
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

    internal data class Map<E, out A>(val source: IOOf<E>, val g: (E) -> A, val index: Int) : IO<A>(), (E) -> IO<A> {
        override fun invoke(value: E): IO<A> = pure(g(value))

        override fun <B> map(f: (A) -> B): IO<B> =
                // Allowed to do maxStackDepthSize map operations in sequence before
                // starting a new Map fusion in order to avoid stack overflows
                if (index != maxStackDepthSize) Map(source, g.andThen(f), index + 1)
                else Map(this, f, 0)

        override fun unsafeRunTimedTotal(limit: Duration): Option<A> = throw AssertionError("Unreachable")
    }
}

fun <A, B> IO<A>.ap(dummy: Unit? = null, ff: IOOf<(A) -> B>): IO<B> =
        flatMap { a -> ff.fix().map({ it(a) }) }

fun <A> IO<A>.handleErrorWith(dummy: Unit? = null, f: (Throwable) -> IOOf<A>): IO<A> =
        IO.Bind(this, IOFrame.errorHandler(f))

fun <A> A.liftIO(): IO<A> = IO.pure(this)
