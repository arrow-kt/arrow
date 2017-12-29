package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*

@instance(Function0::class)
interface Function0FunctorInstance : Functor<Function0HK> {
    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.ev().map(f)
}

@instance(Function0::class)
interface Function0ApplicativeInstance : Applicative<Function0HK> {
    override fun <A, B> ap(fa: Function0Kind<A>, ff: Function0Kind<kotlin.Function1<A, B>>): Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0MonadInstance : Monad<Function0HK> {
    override fun <A, B> ap(fa: Function0Kind<A>, ff: Function0Kind<kotlin.Function1<A, B>>): Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: Function0Kind<A>, f: kotlin.Function1<A, Function0Kind<B>>): Function0<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Kind<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0ComonadInstance : Comonad<Function0HK> {
    override fun <A, B> coflatMap(fa: Function0Kind<A>, f: kotlin.Function1<Function0Kind<A>, B>): Function0<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: Function0Kind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.ev().map(f)
}

@instance(Function0::class)
interface Function0BimonadInstance : Bimonad<Function0HK> {
    override fun <A, B> ap(fa: Function0Kind<A>, ff: Function0Kind<kotlin.Function1<A, B>>): Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: Function0Kind<A>, f: kotlin.Function1<A, Function0Kind<B>>): Function0<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Kind<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Kind<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)

    override fun <A, B> coflatMap(fa: Function0Kind<A>, f: kotlin.Function1<Function0Kind<A>, B>): Function0<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: Function0Kind<A>): A =
            fa.ev().extract()
}
