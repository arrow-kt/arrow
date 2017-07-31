package kategory

typealias FreeKind<S, A> = HK2<Free.F, S, A>
typealias FreeF<S> = HK<Free.F, S>

fun <S, A> FreeKind<S, A>.ev(): Free<S, A> =
        this as Free<S, A>

fun <M, S, A> FreeKind<S, A>.foldMapK(f: FunctionK<S, M>, MM: Monad<M>): HK<M, A> =
        (this as Free<S, A>).foldMap(f, MM)

sealed class Free<out S, out A> : FreeKind<S, A> {

    class F private constructor()

    companion object {
        fun <S, A> pure(a: A): Free<S, A> =
                Pure(a)

        fun <S, A> liftF(fa: HK<S, A>): Free<S, A> =
                Suspend(fa)

        fun <S, A> defer(value: () -> Free<S, A>): Free<S, A> =
                pure<S, Unit>(Unit).flatMap { _ -> value() }

        fun <S> functor(): FreeInstances<S> = object : FreeInstances<S> {}

        fun <S> applicative(): FreeInstances<S> = object : FreeInstances<S> {}

        fun <S> monad(): FreeInstances<S> = object : FreeInstances<S> {}
    }

    abstract fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B>

    data class Pure<out S, out A>(val a: A) : Free<S, A>() {
        override fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B> =
                Free.pure(f(a))
    }

    data class Suspend<out S, out A>(val a: HK<S, A>) : Free<S, A>() {
        override fun <O, B> transform(f: (A) -> B, fs: FunctionK<S, O>): Free<O, B> =
                Free.liftF(fs(a)).map(f)
    }

    data class FlatMapped<out S, out A, C>(val c: Free<S, C>, val f: (C) -> Free<S, A>) : Free<S, A>() {
        override fun <O, B> transform(fm: (A) -> B, fs: FunctionK<S, O>): Free<O, B> =
                Free.FlatMapped(c.transform({ it }, fs), { c.flatMap { f(it) }.transform(fm, fs) })
    }

    override fun toString(): String = "Free(...) : toString is not stack-safe"
}

fun <S, A, B> Free<S, A>.map(f: (A) -> B): Free<S, B> =
        flatMap { Free.Pure<S, B>(f(it)) }

fun <S, A, B> Free<S, A>.flatMap(f: (A) -> Free<S, B>): Free<S, B> =
        Free.FlatMapped(this, f)

@Suppress("UNCHECKED_CAST")
tailrec fun <S, A> Free<S, A>.step(): Free<S, A> =
        if (this is Free.FlatMapped<S, A, *> && this.c is Free.FlatMapped<S, *, *>) {
            val g = this.f as (A) -> Free<S, A>
            val c = this.c.c as Free<S, A>
            val f = this.c.f as (A) -> Free<S, A>
            c.flatMap { cc -> f(cc).flatMap(g) }.step()
        } else if (this is Free.FlatMapped<S, A, *> && this.c is Free.Pure<S, *>) {
            val a = this.c.a as A
            val f = this.f as (A) -> Free<S, A>
            f(a).step()
        } else {
            this
        }

@Suppress("UNCHECKED_CAST")
fun <M, S, A> Free<S, A>.foldMap(f: FunctionK<S, M>, MM: Monad<M>): HK<M, A> =
        MM.tailRecM(this) {
            val x = it.step()
            when (x) {
                is Free.Pure<S, A> -> MM.pure(Either.Right(x.a))
                is Free.Suspend<S, A> -> MM.map(f(x.a), { Either.Right(it) })
                is Free.FlatMapped<S, A, *> -> {
                    val g = (x.f as (A) -> Free<S, A>)
                    val c = x.c as Free<S, A>
                    MM.map(c.foldMap(f, MM), { cc -> Either.Left(g(cc)) })
                }
            }
        }

fun <S, A> A.free(): Free<S, A> =
        Free.pure<S, A>(this)
