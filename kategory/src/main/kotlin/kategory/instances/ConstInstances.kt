package kategory

import kategory.typeclasses.TraverseFilter

interface ConstInstances<A> :
        Applicative<ConstKindPartial<A>>,
        Traverse<ConstKindPartial<A>>,
        TraverseFilter<ConstKindPartial<A>> {

    fun MA(): Monoid<A>

    override fun <T> pure(a: T): Const<A, T> = ConstMonoid<A, T>(MA()).empty().ev()

    override fun <T, U> ap(fa: HK<ConstKindPartial<A>, T>, ff: HK<ConstKindPartial<A>, (T) -> U>): Const<A, U> = fa.ap(ff, MA())

    override fun <T, U> map(fa: HK<ConstKindPartial<A>, T>, f: (T) -> U): Const<A, U> = fa.ev().retag()

    override fun <T, U> foldL(fa: HK<ConstKindPartial<A>, T>, b: U, f: (U, T) -> U): U = b

    override fun <T, U> foldR(fa: HK<ConstKindPartial<A>, T>, lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

    override fun <G, T, U> traverseFilter(fa: HK<ConstKindPartial<A>, T>, f: (T) -> HK<G, Option<U>>, GA: Applicative<G>):
            HK<G, HK<ConstKindPartial<A>, U>> = fa.ev().traverseFilter(f, GA)

    override fun <G, T, U> traverse(fa: HK<ConstKindPartial<A>, T>, f: (T) -> HK<G, U>, GA: Applicative<G>):
            HK<G, HK<ConstKindPartial<A>, U>> = fa.ev().traverse(f, GA)

    companion object {
        operator fun <A> invoke(MA: Monoid<A>): ConstInstances<A> = object : kategory.ConstInstances<A> {
            override fun MA(): kategory.Monoid<A> = MA
        }
    }
}

interface ConstMonoid<A, T> : Monoid<ConstKind<A, T>> {

    fun MA(): Monoid<A>

    override fun combine(a: ConstKind<A, T>, b: ConstKind<A, T>): Const<A, T> = a.combine(b, MA())

    override fun empty(): Const<A, T> = Const(MA().empty())

    companion object {
        operator fun <A, T> invoke(MA: Monoid<A>): ConstMonoid<A, T> = object : ConstMonoid<A, T> {
            override fun MA(): Monoid<A> = MA
        }
    }
}