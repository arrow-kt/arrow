package kategory

@instance(Yoneda::class)
interface YonedaFunctorInstance<U> : Functor<YonedaKindPartial<U>> {

    fun FU(): Functor<U>

    override fun <A, B> map(fa: YonedaKind<U, A>, f: (A) -> B): Yoneda<U, B> = fa.ev().map(f, FU())
}
