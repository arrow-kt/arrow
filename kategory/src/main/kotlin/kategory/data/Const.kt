package kategory

import kategory.typeclasses.TraverseFilter
import sun.java2d.pipe.AlphaPaintPipe

fun <A, T> ConstKind<A, T>.value(): A = this.ev().value

@higherkind data class Const<out A, out T>(val value: A) : ConstKind<A, T> {

    @Suppress("UNCHECKED_CAST")
    fun <U> retag(): Const<A, U> = this as Const<A, U>

    inline fun <F, U> traverseFilter(f: (T) -> HK<F, Option<U>>, FA: Applicative<F>): HK<F, Const<A, U>> =
            FA.pure(retag())

    inline fun <F, U> traverse(f: (T) -> HK<F, U>, FA: Applicative<F>): HK<F, Const<A, U>> =
            FA.pure(retag())

    companion object {
        fun <T, A> pure(a: A): Const<A, T> = Const(a)

        inline fun <reified A> instances(MA: Monoid<A> = kategory.monoid<A>()): ConstInstances<A> = ConstInstances(MA)

        inline fun <reified A> applicative(MA: Monoid<A> = kategory.monoid<A>()): Applicative<ConstKindPartial<A>> = instances(MA)

        inline fun <reified A> traverseFilter(MA: Monoid<A> = kategory.monoid<A>()): TraverseFilter<ConstKindPartial<A>> = instances(MA)

        inline fun <reified A> traverse(MA: Monoid<A> = kategory.monoid<A>()): Traverse<ConstKindPartial<A>> = instances(MA)

        inline fun <reified A, T> semigroup(MA: Monoid<A> = kategory.monoid<A>()): Semigroup<ConstKind<A, T>> = ConstMonoid(MA)

        inline fun <reified A, T> monoid(MA: Monoid<A> = kategory.monoid<A>()): Monoid<ConstKind<A, T>> = ConstMonoid(MA)
    }
}

fun <A, T> ConstKind<A, T>.combine(that: ConstKind<A, T>, SG: Semigroup<A>): Const<A, T> = Const(SG.combine(this.value(), that.value()))

fun <A, T, U> ConstKind<A, T>.ap(ff: ConstKind<A, (T) -> U>, SG: Semigroup<A>): Const<A, U> = ff.ev().retag<U>().combine(this.ev().retag(), SG)

fun <A> A.const(): Const<A, Nothing> = Const(this)
