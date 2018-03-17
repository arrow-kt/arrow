package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.higherkind
import arrow.typeclasses.internal.Platform
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine

fun <A> (() -> A).k(): Function0<A> = Function0(this)

operator fun <A> Function0Of<A>.invoke(): A = this.fix().f()

@higherkind
data class Function0<out A>(internal val f: () -> A) : Function0Of<A> {

    fun <B> map(f: (A) -> B): Function0<B> = pure(f(this()))

    fun <B> flatMap(ff: (A) -> Function0Of<B>): Function0<B> = ff(f()).fix()

    fun <B> flatMapIn(context: CoroutineContext, ff: (A) -> Function0Of<B>): Function0<B> =
            {
                val sus: suspend () -> B = { ff(f())() }
                val cont = Platform.awaitableContinuation<B>(context)
                sus.startCoroutine(cont)
                cont.awaitBlocking().fold({ throw it }, ::identity)
            }.k()

    fun <B> coflatMap(f: (Function0Of<A>) -> B): Function0<B> = { f(this) }.k()

    fun <B> ap(ff: Function0Of<(A) -> B>): Function0<B> = ff.fix().flatMap { f -> map(f) }.fix()

    fun extract(): A = f()

    companion object {

        fun <A> pure(a: A): Function0<A> = { a }.k()

        tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForFunction0, Either<A, B>>): B {
            val fa = f(a).fix()()
            return when (fa) {
                is Either.Right<A, B> -> fa.b
                is Either.Left<A, B> -> loop(fa.a, f)
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFunction0, Either<A, B>>): Function0<B> = { loop(a, f) }.k()

    }
}