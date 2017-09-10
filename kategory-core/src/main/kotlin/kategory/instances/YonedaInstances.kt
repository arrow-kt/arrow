package kategory

interface YonedaInstances<U> : Functor<YonedaKindPartial<U>> {

    fun FM(): Functor<U>

    override fun <A, B> map(fa: HK<YonedaKindPartial<U>, A>, f: (A) -> B): HK<YonedaKindPartial<U>, B> = fa.ev().map(f, FM())
}