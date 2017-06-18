package kategory

class CoYonedaFunctor<U, P> : Functor<CoYonedaF<U, P>> {
    override fun <A, B> map(fa: HK<CoYonedaF<U, P>, A>, f: (A) -> B): HK<CoYonedaF<U, P>, B> =
            fa.ev().map(f)
}