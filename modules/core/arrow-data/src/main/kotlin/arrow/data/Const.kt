package arrow.data

import arrow.*
import arrow.core.Option
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup

fun <A, T> ConstOf<A, T>.value(): A = this.fix().value

@higherkind data class Const<out A, out T>(val value: A) : ConstOf<A, T> {

    @Suppress("UNCHECKED_CAST")
    fun <U> retag(): Const<A, U> = this as Const<A, U>

    fun <F, U> traverse(f: (T) -> Kind<F, U>, FA: Applicative<F>): Kind<F, Const<A, U>> = FA.pure(retag())

    fun <F, U> traverseFilter(f: (T) -> Kind<F, Option<U>>, FA: Applicative<F>): Kind<F, Const<A, U>> = FA.pure(retag())

    companion object {
        fun <A, T> pure(a: A): Const<A, T> = Const(a)
    }
}

fun <A, T> ConstOf<A, T>.combine(that: ConstOf<A, T>, SG: Semigroup<A>): Const<A, T> = Const(SG.combine(this.value(), that.value()))

fun <A, T, U> ConstOf<A, T>.ap(ff: ConstOf<A, (T) -> U>, SG: Semigroup<A>): Const<A, U> = ff.fix().retag<U>().combine(this.fix().retag(), SG)

fun <A> A.const(): Const<A, Nothing> = Const(this)
