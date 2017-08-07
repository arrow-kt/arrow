package kategory

interface CoyonedaInstances<U, P> : Functor<CoyonedaF<U, P>> {
    override fun <A, B> map(fa: HK<CoyonedaF<U, P>, A>, f: (A) -> B): HK<CoyonedaF<U, P>, B> =
            fa.ev().map(f)
}