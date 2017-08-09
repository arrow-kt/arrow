package kategory

interface YonedaInstances<U> : Functor<YonedaF<U>> {

    fun FM(): Functor<U>

    override fun <A, B> map(fa: HK<YonedaF<U>, A>, f: (A) -> B): HK<YonedaF<U>, B> = fa.ev().map(f, FM())
}