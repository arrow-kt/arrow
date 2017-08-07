package kategory

import kategory.effects.internal.AndThen
import kategory.effects.internal.Platform.onceOnly
import kategory.effects.internal.Platform.unsafeResync
import kategory.effects.internal.error

@higherkind sealed class IO<out A> : IOKind<A> {

    abstract fun <B> map(f: (A) -> B): IO<B>

    fun <B> flatMap(f: (A) -> IO<B>): IO<B> =
            flatMapTotal(
                    AndThen {
                        try {
                            f(it)
                        } catch (error: Throwable) {
                            RaiseError<B>(error)
                        }
                    })

    internal abstract fun <B> flatMapTotal(f: AndThen<A, IO<B>>): IO<B>

    abstract fun attempt(): IO<Either<Throwable, A>>

    fun runAsync(cb: (Either<Throwable, A>) -> IO<Unit>): IO<Unit> =
            IO { unsafeRunAsync(cb.andThen { it.unsafeRunAsync { } }) }

    fun unsafeRunAsync(cb: (Either<Throwable, A>) -> Unit): Unit =
            unsafeStep().unsafeRunAsyncTotal(cb)

    abstract fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit)

    @Suppress("UNCHECKED_CAST")
    fun unsafeStep(): IO<A> {
        var current: IO<A> = this
        while (true) {
            when (current) {
                is Suspend -> {
                    current = current.cont(Unit)
                }
                is BindSuspend<*, A> -> {
                    val cont: IO<Any?> = current.cont(Unit)
                    val f: AndThen<Any?, IO<A>> = current.f as AndThen<Any?, IO<A>>
                    current = cont.flatMapTotal(f)
                }
                else -> return current
            }
        }
    }

    fun unsafeRunSync(): A =
            unsafeRunTimed(Duration.INFINITE).fold({ throw IllegalArgumentException("IO execution should yield a valid result") }, { it })

    fun unsafeRunTimed(limit: Duration): Option<A> =
            unsafeStep().unsafeRunTimedTotal(limit)

    abstract fun unsafeRunTimedTotal(limit: Duration): Option<A>

    companion object : IOInstances, GlobalInstance<Monad<IOHK>>() {
        internal fun <A, B> mapDefault(t: IO<A>, f: (A) -> B): IO<B> =
                t.flatMap(f.andThen { Pure(it) })

        internal fun <A> attemptValue(): AndThen<A, IO<Either<Throwable, A>>> =
                AndThen({ a: A -> Pure(Either.Right(a)) }, { e -> Pure(Either.Left(e)) })

        operator fun <A> invoke(f: (Unit) -> A): IO<A> =
                suspend { Pure(f(Unit)) }

        override fun <A> pure(a: A): IO<A> =
                Pure(a)

        fun <A> suspend(f: (Unit) -> IO<A>): IO<A> =
                Suspend(AndThen { _ ->
                    try {
                        f(Unit)
                    } catch (throwable: Throwable) {
                        raiseError(throwable)
                    }
                })

        fun <A> raiseError(throwable: Throwable): IO<A> =
                RaiseError(throwable)

        fun <A> async(k: ((Either<Throwable, A>) -> Unit) -> Unit): IO<A> =
                Async { callBack ->
                    onceOnly(callBack).let {
                        try {
                            k(it)
                        } catch (throwable: Throwable) {
                            it(Either.Left(throwable))
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

        fun functor(): Functor<IOHK> = this

        fun applicative(): Applicative<IOHK> = this

        fun monad(): Monad<IOHK> = this

        fun <A> semigroup(SG: Semigroup<A>): IOSemigroup<A> = object : IOSemigroup<A> {
            override fun SG(): Semigroup<A> = SG
        }

        fun <A> monoid(SM: Monoid<A>): IOMonoid<A> = object : IOMonoid<A> {
            override fun SM(): Monoid<A> = SM
        }
    }
}

internal data class Pure<out A>(val a: A) : IO<A>() {
    override fun <B> flatMapTotal(f: AndThen<A, IO<B>>): IO<B> =
            Suspend(AndThen({ _: Unit -> a }.andThen(f)))

    override fun <B> map(f: (A) -> B): IO<B> =
            try {
                Pure(f(a))
            } catch (exception: Throwable) {
                RaiseError(exception)
            }

    override fun attempt(): IO<Either<Throwable, A>> =
            Pure(Either.Right(a))

    override fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit) =
            cb(Either.Right(a))

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> =
            Option.Some(a)
}

internal data class RaiseError<out A>(val exception: Throwable) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> =
            RaiseError(exception)

    override fun <B> flatMapTotal(f: AndThen<A, IO<B>>): IO<B> =
            Suspend(AndThen { f.error(exception, { RaiseError(it) }) })

    override fun attempt(): IO<Either<Throwable, A>> =
            Pure(Either.Left(exception))

    override fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit) =
            cb(Either.Left(exception))

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> =
            throw exception
}

internal data class Suspend<out A>(val cont: AndThen<Unit, IO<A>>) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> =
            mapDefault(this, f)

    override fun <B> flatMapTotal(f: AndThen<A, IO<B>>): IO<B> =
            BindSuspend(cont, f)

    override fun attempt(): IO<Either<Throwable, A>> =
            BindSuspend(cont, attemptValue())

    override fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit) =
            throw AssertionError("Unreachable")

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> =
            throw AssertionError("Unreachable")
}

internal data class BindSuspend<E, out A>(val cont: AndThen<Unit, IO<E>>, val f: AndThen<E, IO<A>>) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> =
            mapDefault(this, f)

    override fun <B> flatMapTotal(ff: AndThen<A, IO<B>>): IO<B> =
            BindSuspend(cont, f.andThen(AndThen({ it.flatMapTotal(ff) }, { ff.error(it, { RaiseError(it) }) })))

    override fun attempt(): IO<Either<Throwable, A>> =
            BindSuspend(AndThen { _ -> this }, attemptValue())

    override fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit) =
            throw AssertionError("Unreachable")

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> =
            throw AssertionError("Unreachable")
}

internal data class Async<out A>(val cont: ((Either<Throwable, A>) -> Unit) -> Unit) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> =
            mapDefault(this, f)

    override fun <B> flatMapTotal(f: AndThen<A, IO<B>>): IO<B> =
            BindAsync(cont, f)

    override fun attempt(): IO<Either<Throwable, A>> =
            BindAsync(cont, attemptValue())

    override fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit) =
            cont(cb)

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> =
            unsafeResync(this, limit)
}

internal data class BindAsync<E, out A>(val cont: ((Either<Throwable, E>) -> Unit) -> Unit, val f: AndThen<E, IO<A>>) : IO<A>() {
    override fun <B> map(f: (A) -> B): IO<B> =
            mapDefault(this, f)

    override fun <B> flatMapTotal(ff: AndThen<A, IO<B>>): IO<B> =
            BindAsync(cont, f.andThen(AndThen({ it.flatMapTotal(ff) }, { ff.error(it, { RaiseError(it) }) })))

    override fun attempt(): IO<Either<Throwable, A>> =
            BindSuspend(AndThen { _ -> this }, attemptValue())

    override fun unsafeRunAsyncTotal(cb: (Either<Throwable, A>) -> Unit) =
            cont { result ->
                try {
                    when (result) {
                        is Either.Right -> f(result.b).unsafeRunAsync(cb)
                        is Either.Left -> f.error(result.a, { RaiseError(it) }).unsafeRunAsync(cb)
                    }
                } catch (throwable: Throwable) {
                    cb(Either.Left(throwable))
                }
            }

    override fun unsafeRunTimedTotal(limit: Duration): Option<A> =
            unsafeResync(this, limit)
}

fun <A> A.liftIO(): IO<A> =
        IO.pure(this)