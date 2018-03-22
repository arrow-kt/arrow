package arrow.free

import arrow.Kind
import arrow.core.Either
import arrow.core.FunctionK
import arrow.higherkind
import arrow.typeclasses.Monad

inline fun <reified M, S, A> FreeOf<S, A>.foldMapK(f: FunctionK<S, M>, MM: Monad<M>): Kind<M, A> = (this as Free<S, A>).foldMap(f, MM)

@higherkind
sealed class Free<S, out A> : FreeOf<S, A> {

    companion object {
        fun <S, A> pure(a: A): Free<S, A> = Pure(a)

        fun <S, A> liftF(fa: Kind<S, A>): Free<S, A> = Suspend(fa)

        fun <S, A> defer(value: () -> Free<S, A>): Free<S, A> = pure<S, Unit>(Unit).flatMap { _ -> value() }

        internal fun <F> functionKF(): FunctionK<F, FreePartialOf<F>> =
                object : FunctionK<F, FreePartialOf<F>> {
                    override fun <A> invoke(fa: Kind<F, A>): Free<F, A> =
                            liftF(fa)

                }

        /* FIXME(paco) lookup is broken, not sure what this was meant to do
        internal fun <F> applicativeF(): Applicative<FreePartialOf<F>> =
                object : Applicative<FreePartialOf<F>> {
                    private val applicative: Applicative<FreePartialOf<F>> = arrow.typeclasses.applicative()

                    override fun <A> pure(a: A): Free<F, A> =
                            Companion.pure(a)

                    override fun <A, B> ap(fa: Kind<FreePartialOf<F>, A>, ff: Kind<FreePartialOf<F>, (A) -> B>): Free<F, B> =
                            applicative.ap(fa, ff).fix()
                }*/
    }

    abstract fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B>

    data class Pure<S, out A>(val a: A) : Free<S, A>() {
        override fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B> = pure(f(a))
    }

    data class Suspend<S, out A>(val a: Kind<S, A>) : Free<S, A>() {
        override fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B> = liftF(fs(a)).map(f)
    }

    data class FlatMapped<S, out A, C>(val c: Free<S, C>, val fm: (C) -> Free<S, A>) : Free<S, A>() {
        override fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B> =
                FlatMapped(c.transform({ it }, fs), { c.flatMap { fm(it) }.transform(f, fs) })
    }

    override fun toString(): String = "Free(...) : toString is not stack-safe"
}

fun <S, A, B> Free<S, A>.map(f: (A) -> B): Free<S, B> = flatMap { Free.Pure<S, B>(f(it)) }

fun <S, A, B> Free<S, A>.flatMap(f: (A) -> Free<S, B>): Free<S, B> = Free.FlatMapped(this, f)

fun <S, A, B> Free<S, A>.ap(ff: FreeOf<S, (A) -> B>): Free<S, B> = ff.fix().flatMap { f -> map(f) }.fix()

@Suppress("UNCHECKED_CAST")
tailrec fun <S, A> Free<S, A>.step(): Free<S, A> =
        if (this is Free.FlatMapped<S, A, *> && this.c is Free.FlatMapped<S, *, *>) {
            val g = this.fm as (A) -> Free<S, A>
            val c = this.c.c as Free<S, A>
            val f = this.c.fm as (A) -> Free<S, A>
            c.flatMap { cc -> f(cc).flatMap(g) }.step()
        } else if (this is Free.FlatMapped<S, A, *> && this.c is Free.Pure<S, *>) {
            val a = this.c.a as A
            val f = this.fm as (A) -> Free<S, A>
            f(a).step()
        } else {
            this
        }

@Suppress("UNCHECKED_CAST")
fun <M, S, A> Free<S, A>.foldMap(f: FunctionK<S, M>, MM: Monad<M>): Kind<M, A> =
        MM.tailRecM(this) {
            val x = it.step()
            when (x) {
                is Free.Pure<S, A> -> MM.pure(Either.Right(x.a))
                is Free.Suspend<S, A> -> MM.map(f(x.a), { Either.Right(it) })
                is Free.FlatMapped<S, A, *> -> {
                    val g = (x.fm as (A) -> Free<S, A>)
                    val c = x.c as Free<S, A>
                    MM.map(c.foldMap(f, MM), { cc -> Either.Left(g(cc)) })
                }
            }
        }

fun <S, A> A.free(): Free<S, A> = Free.pure<S, A>(this)

fun <F, A> Free<F, A>.run(M: Monad<F>): Kind<F, A> = this.foldMap(FunctionK.id(), M)

fun <F, A> FreeOf<F, A>.runK(M: Monad<F>): Kind<F, A> = this.fix().foldMap(FunctionK.id(), M)
