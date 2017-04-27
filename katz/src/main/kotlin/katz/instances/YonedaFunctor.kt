package katz

class YonedaFunctor<U>(val FM: Functor<U>) : Functor<YonedaF<U>> {
    override fun <A, B> map(fa: HK<YonedaF<U>, A>, f: (A) -> B): HK<YonedaF<U>, B> =
            fa.ev().map(f, FM)
}