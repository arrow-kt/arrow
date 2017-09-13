package kategory

fun <A, T> ConstKind<A, T>.value(): A = this.ev().value

@higherkind data class Const<out A, out T>(val value: A) : ConstKind<A, T> {

    @Suppress("UNCHECKED_CAST")
    fun <U> retag(): Const<A, U> = this as Const<A, U>

    fun <F, U> traverse(f: (T) -> HK<F, U>, FA: Applicative<F>): HK<F, Const<A, U>> = FA.pure(retag())

    fun <F, U> traverseFilter(f: (T) -> HK<F, Option<U>>, FA: Applicative<F>): HK<F, Const<A, U>> = FA.pure(retag())

    companion object {
        fun <T, A> pure(a: A): Const<A, T> = Const(a)

        inline fun <reified A> applicative(MA: Monoid<A> = kategory.monoid<A>()): Applicative<ConstKindPartial<A>> =
                ConstApplicativeInstanceImplicits.instance(MA)

        inline fun <reified A> traverse(MA: Monoid<A> = kategory.monoid<A>()): Traverse<ConstKindPartial<A>> =
                ConstTraverseFilterInstanceImplicits.instance()

        inline fun <reified A> traverseFilter(MA: Monoid<A> = kategory.monoid<A>()): TraverseFilter<ConstKindPartial<A>> =
                ConstTraverseFilterInstanceImplicits.instance()

        inline fun <reified A, T> semigroup(SA: Semigroup<A> = kategory.semigroup<A>()): Semigroup<ConstKind<A, T>> =
                ConstSemigroupInstanceImplicits.instance(SA)

        inline fun <reified A, T> monoid(MA: Monoid<A> = kategory.monoid<A>()): Monoid<ConstKind<A, T>> =
                ConstMonoidInstanceImplicits.instance(MA)

    }
}

fun <A, T> ConstKind<A, T>.combine(that: ConstKind<A, T>, SG: Semigroup<A>): Const<A, T> = Const(SG.combine(this.value(), that.value()))

fun <A, T, U> ConstKind<A, T>.ap(ff: ConstKind<A, (T) -> U>, SG: Semigroup<A>): Const<A, U> = ff.ev().retag<U>().combine(this.ev().retag(), SG)

fun <A> A.const(): Const<A, Nothing> = Const(this)
