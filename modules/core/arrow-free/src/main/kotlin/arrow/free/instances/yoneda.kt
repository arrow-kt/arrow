package arrow.free.instances

import arrow.*
import arrow.free.*
import arrow.typeclasses.Functor

@instance(Yoneda::class)
interface YonedaFunctorInstance<U> : Functor<YonedaPartialOf<U>> {
    override fun <A, B> map(fa: YonedaOf<U, A>, f: (A) -> B): Yoneda<U, B> = fa.extract().map(f)
}
