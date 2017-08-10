package kategory

interface ConstInstances<A> :
        Applicative<ConstF<A>>,
        Traverse<ConstF<A>> {

    fun MA(): Monoid<A>

    override fun <T> pure(a: T): Const<A, T> = Const.monoid<A, T>(MA()).empty().ev()

    override fun <T, U> ap(fa: HK<ConstF<A>, T>, ff: HK<ConstF<A>, (T) -> U>): Const<A, U> = fa.ap(ff, MA())

    override fun <T, U> map(fa: HK<ConstF<A>, T>, f: (T) -> U): Const<A, U> = fa.ev().retag()

    override fun <T, U> foldL(fa: HK<ConstF<A>, T>, b: U, f: (U, T) -> U): U = b

    override fun <T, U> foldR(fa: HK<ConstF<A>, T>, lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

    override fun <G, T, U> traverse(fa: HK<ConstF<A>, T>, f: (T) -> HK<G, U>, GA: Applicative<G>): HK<G, HK<ConstF<A>, U>> = fa.ev().traverse(f, GA)
}

interface ConstMonoid<A, T> : Monoid<ConstKind<A, T>> {

    fun MA(): Monoid<A>

    override fun combine(a: ConstKind<A, T>, b: ConstKind<A, T>): Const<A, T> = a.combine(b, MA())

    override fun empty(): Const<A, T> = Const(MA().empty())
}