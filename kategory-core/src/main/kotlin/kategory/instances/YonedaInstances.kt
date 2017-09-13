package kategory

interface YonedaFunctorInstance<U> : Functor<YonedaKindPartial<U>> {

    fun FU(): Functor<U>

    override fun <A, B> map(fa: HK<YonedaKindPartial<U>, A>, f: (A) -> B): Yoneda<U, B> = fa.ev().map(f, FU())
}

object YonedaFunctorInstanceImplicits {
    @JvmStatic
    fun <U> instance(FU: Functor<U>): YonedaFunctorInstance<U> = object : YonedaFunctorInstance<U> {
        override fun FU(): Functor<U> = FU
    }
}