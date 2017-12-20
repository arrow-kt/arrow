package arrow

@instance(Yoneda::class)
interface YonedaFunctorInstance<U> : Functor<YonedaKindPartial<U>> {
    override fun <A, B> map(fa: YonedaKind<U, A>, f: (A) -> B): Yoneda<U, B> = fa.ev().map(f)
}
