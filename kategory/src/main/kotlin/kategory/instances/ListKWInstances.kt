package kategory

/**
 * Created by marc on 1/8/17.
 */
interface ListKWInstances :
        Functor<ListKW.F>,
        Applicative<ListKW.F>,
        Monad<ListKW.F>{

    override fun <A> pure(a: A): ListKW<A> = ListKW.listOfK(a)

    override fun <A, B> flatMap(fa: HK<ListKW.F, A>, f: (A) -> HK<ListKW.F, B>): ListKW<B> {
        return fa.ev().flatMap { f(it).ev() }
    }

    override fun <A, B> map(fa: HK<ListKW.F, A>, f: (A) -> B): HK<ListKW.F, B> {
        return fa.ev().map(f)
    }


    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ListKW.F, Either<A, B>>): ListKW<B> {
        return f(a).ev().flatMap {
            when (it) {
                is Either.Left -> tailRecM(it.a, f)
                is Either.Right -> pure(it.b)
            }
        }
    }

}
interface ListKWSemigroup<A> : Semigroup<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = a + b
}

interface ListKWSemigroupK : SemigroupK<ListKW.F> {
    override fun <A> combineK(x: HK<ListKW.F, A>, y: HK<ListKW.F, A>): ListKW<A> = x.ev() + y.ev()
}

interface ListKWMonoid<A> : Monoid<ListKW<A>> {
    override fun empty(): ListKW<A> = ListKW.listOfK()

    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = a + b
}