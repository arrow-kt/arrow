package kategory

fun <A, T> ConstKind<A, T>.value(): A = this.ev().value

typealias ConstF<A> = HK<ConstHK, A>

@higherkind data class Const<out A, out T>(val value: A) : ConstKind<A, T> {

    @Suppress("UNCHECKED_CAST")
    fun <U> retag(): Const<A, U> = this as Const<A, U>

    inline fun <F, U> traverse(f: (T) -> HK<F, U>, FA: Applicative<F>): HK<F, Const<A, U>> = FA.pure(retag())

    companion object {
        fun <T, A> pure(a: A): Const<A, T> = Const(a)

        fun <A> instances(MA: Monoid<A>): ConstInstances<A> = object : ConstInstances<A> {
            override fun MA(): Monoid<A> = MA
        }

        inline fun <reified A> applicative(MA: Monoid<A> = kategory.monoid<A>()): Applicative<ConstF<A>> = instances(MA)

        inline fun <reified A> traverse(MA: Monoid<A> = kategory.monoid<A>()): Traverse<ConstF<A>> = instances(MA)

        fun <A, T> semigroup(MA: Monoid<A>): Semigroup<ConstKind<A, T>> = object : ConstMonoid<A, T> {
            override fun MA(): Monoid<A> = MA
        }

        fun <A, T> monoid(MA: Monoid<A>): Monoid<ConstKind<A, T>> = object : ConstMonoid<A, T> {
            override fun MA(): Monoid<A> = MA
        }
    }
}

fun <A, T> ConstKind<A, T>.combine(that: ConstKind<A, T>, SG: Semigroup<A>): Const<A, T> = Const(SG.combine(this.value(), that.value()))

fun <A, T, U> ConstKind<A, T>.ap(ff: ConstKind<A, (T) -> U>, SG: Semigroup<A>): Const<A, U> = ff.ev().retag<U>().combine(this.ev().retag(), SG)

fun <A> A.const(): Const<A, Nothing> = Const(this)
