package kategory

interface ConstFunctorInstance<A> : Functor<ConstKindPartial<A>> {
    override fun <T, U> map(fa: ConstKind<A, T>, f: (T) -> U): Const<A, U> = fa.ev().retag()
}

object ConstFunctorInstanceImplicits {
    @JvmStatic
    fun <A> instance(): ConstFunctorInstance<A> = object : ConstFunctorInstance<A> {}
}

interface ConstApplicativeInstance<A> : Applicative<ConstKindPartial<A>> {

    fun MA(): Monoid<A>

    override fun <T, U> map(fa: ConstKind<A, T>, f: (T) -> U): Const<A, U> = fa.ev().retag()

    override fun <T> pure(a: T): Const<A, T> = ConstMonoid<A, T>(MA()).empty().ev()

    override fun <T, U> ap(fa: ConstKind<A, T>, ff: ConstKind<A, (T) -> U>): Const<A, U> = fa.ap(ff, MA())
}

object ConstApplicativeInstanceImplicits {
    @JvmStatic fun <A> instance(MA: Monoid<A>): ConstApplicativeInstance<A> = object : ConstApplicativeInstance<A> {
        override fun MA(): Monoid<A> = MA
    }
}

interface ConstFoldableInstance<A> : Foldable<ConstKindPartial<A>> {

    override fun <T, U> foldL(fa: ConstKind<A, T>, b: U, f: (U, T) -> U): U = b

    override fun <T, U> foldR(fa: ConstKind<A, T>, lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

}

object ConstFoldableInstanceImplicits {
    @JvmStatic
    fun <A> instance(): ConstFoldableInstance<A> = object : ConstFoldableInstance<A> {}
}

interface ConstTraverseInstance<X> : Traverse<ConstKindPartial<X>> {

    override fun <T, U> map(fa: ConstKind<X, T>, f: (T) -> U): Const<X, U> = fa.ev().retag()

    override fun <T, U> foldL(fa: ConstKind<X, T>, b: U, f: (U, T) -> U): U = b

    override fun <T, U> foldR(fa: ConstKind<X, T>, lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

    override fun <G, A, B> traverse(fa: ConstKind<X, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, ConstKind<X, B>> =
            fa.ev().traverse(f, GA)
}

object ConstTraverseInstanceImplicits {
    @JvmStatic fun <A> instance(): ConstTraverseInstance<A> = object : ConstTraverseInstance<A> {}
}

interface ConstTraverseFilterInstance<X> : ConstTraverseInstance<X>, TraverseFilter<ConstKindPartial<X>> {

    override fun <G, A, B> traverseFilter(fa: ConstKind<X, A>, f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, ConstKind<X, B>> =
            fa.ev().traverseFilter(f, GA)
}

object ConstTraverseFilterInstanceImplicits {
    @JvmStatic fun <A> instance(): ConstTraverseFilterInstance<A> = object : ConstTraverseFilterInstance<A> {}
}


interface ConstSemigroup<A, T> : Semigroup<ConstKind<A, T>> {

    fun SA(): Semigroup<A>

    override fun combine(a: ConstKind<A, T>, b: ConstKind<A, T>): Const<A, T> = a.combine(b, SA())

    companion object {
        operator fun <A, T> invoke(SA: Semigroup<A>): ConstSemigroup<A, T> = object : ConstSemigroup<A, T> {
            override fun SA(): Semigroup<A> = SA
        }
    }
}

object ConstSemigroupInstanceImplicits {
    @JvmStatic fun <A, T> instance(SA: Semigroup<A>): ConstSemigroup<A, T> = object : ConstSemigroup<A, T> {
        override fun SA(): Semigroup<A> = SA
    }
}

interface ConstMonoid<A, T> : ConstSemigroup<A, T>, Monoid<ConstKind<A, T>> {

    fun MA(): Monoid<A>

    override fun empty(): Const<A, T> = Const(MA().empty())

    companion object {
        operator fun <A, T> invoke(MA: Monoid<A>): ConstMonoid<A, T> = object : ConstMonoid<A, T> {
            override fun SA(): Semigroup<A> = MA
            override fun MA(): Monoid<A> = MA
        }
    }
}

object ConstMonoidInstanceImplicits {
    @JvmStatic fun <A, T> instance(MA: Monoid<A>): ConstMonoid<A, T> = object : ConstMonoid<A, T> {
        override fun SA(): Semigroup<A> = MA
        override fun MA(): Monoid<A> = MA
    }
}