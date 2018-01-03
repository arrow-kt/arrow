package arrow.free.instances

import arrow.*
import arrow.free.*
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance(Cofree::class)
interface CofreeFunctorInstance<S> : Functor<CofreeKindPartial<S>>, TC {
    override fun <A, B> map(fa: CofreeKind<S, A>, f: (A) -> B): Cofree<S, B> = fa.ev().map(f)
}

@instance(Cofree::class)
interface CofreeComonadInstance<S> : CofreeFunctorInstance<S>, Comonad<CofreeKindPartial<S>>, TC {
    override fun <A, B> coflatMap(fa: CofreeKind<S, A>, f: (CofreeKind<S, A>) -> B): Cofree<S, B> = fa.ev().coflatMap(f)

    override fun <A> extract(fa: CofreeKind<S, A>): A = fa.ev().extract()

    override fun <A> duplicate(fa: CofreeKind<S, A>): HK<CofreeKindPartial<S>, Cofree<S, A>> = fa.ev().duplicate()
}
