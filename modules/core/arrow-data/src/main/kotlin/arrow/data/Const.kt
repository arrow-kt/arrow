package arrow.data

import arrow.*
import arrow.core.Option
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup

fun <A, T> ConstKind<A, T>.value(): A = this.reify().value

@higherkind data class Const<out A, out T>(val value: A) : ConstKind<A, T> {

    @Suppress("UNCHECKED_CAST")
    fun <U> retag(): Const<A, U> = this as Const<A, U>

    fun <F, U> traverse(f: (T) -> Kind<F, U>, FA: Applicative<F>): Kind<F, Const<A, U>> = FA.pure(retag())

    fun <F, U> traverseFilter(f: (T) -> Kind<F, Option<U>>, FA: Applicative<F>): Kind<F, Const<A, U>> = FA.pure(retag())

    companion object {
        fun <A, T> pure(a: A): Const<A, T> = Const(a)
    }
}

fun <A, T> ConstKind<A, T>.combine(that: ConstKind<A, T>, SG: Semigroup<A>): Const<A, T> = Const(SG.combine(this.value(), that.value()))

fun <A, T, U> ConstKind<A, T>.ap(ff: ConstKind<A, (T) -> U>, SG: Semigroup<A>): Const<A, U> = ff.reify().retag<U>().combine(this.reify().retag(), SG)

fun <A> A.const(): Const<A, Nothing> = Const(this)
