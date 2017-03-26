package katz

interface Functor<F> {
    fun <A : Any, B : Any> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>
}