package kategory

interface CoyonedaInstances<U, P> : Functor<CoyonedaKindPartial<U, P>> {
    override fun <A, B> map(fa: HK<CoyonedaKindPartial<U, P>, A>, f: (A) -> B): HK<CoyonedaKindPartial<U, P>, B> = fa.ev().map(f)
}