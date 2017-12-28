package arrow.free.instances

import arrow.*

@instance(Const::class)
interface ConstFunctorInstance<A> : Functor<ConstKindPartial<A>> {
    override fun <T, U> map(fa: ConstKind<A, T>, f: (T) -> U): Const<A, U> = fa.ev().retag()
}

@instance(Const::class)
interface ConstApplicativeInstance<A> : Applicative<ConstKindPartial<A>> {

    fun MA(): Monoid<A>

    override fun <T, U> map(fa: ConstKind<A, T>, f: (T) -> U): Const<A, U> = fa.ev().retag()

    override fun <T> pure(a: T): Const<A, T> = object : ConstMonoidInstance<A, T> {
        override fun SA(): Monoid<A> = MA()
    }.empty().ev()

    override fun <T, U> ap(fa: ConstKind<A, T>, ff: ConstKind<A, (T) -> U>): Const<A, U> = fa.ap(ff, MA())
}

@instance(Const::class)
interface ConstFoldableInstance<A> : Foldable<ConstKindPartial<A>> {

    override fun <T, U> foldLeft(fa: ConstKind<A, T>, b: U, f: (U, T) -> U): U = b

    override fun <T, U> foldRight(fa: ConstKind<A, T>, lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

}

@instance(Const::class)
interface ConstTraverseInstance<X> : ConstFoldableInstance<X>, Traverse<ConstKindPartial<X>> {

    override fun <T, U> map(fa: ConstKind<X, T>, f: (T) -> U): Const<X, U> = fa.ev().retag()

    override fun <G, A, B> traverse(fa: ConstKind<X, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, ConstKind<X, B>> =
            fa.ev().traverse(f, GA)
}

@instance(Const::class)
interface ConstSemigroupInstance<A, T> : Semigroup<ConstKind<A, T>> {

    fun SA(): Semigroup<A>

    override fun combine(a: ConstKind<A, T>, b: ConstKind<A, T>): Const<A, T> = a.combine(b, SA())

}

@instance(Const::class)
interface ConstMonoidInstance<A, T> : ConstSemigroupInstance<A, T>, Monoid<ConstKind<A, T>> {

    override fun SA(): Monoid<A>

    override fun empty(): Const<A, T> = Const(SA().empty())

}

@instance(Const::class)
interface ConstEqInstance<A, T> : Eq<Const<A, T>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Const<A, T>, b: Const<A, T>): Boolean =
            EQ().eqv(a.value, b.value)
}