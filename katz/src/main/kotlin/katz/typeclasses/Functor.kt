package katz.typeclasses

import katz.typeclasses.HK

interface Functor<F> {
    fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>
}