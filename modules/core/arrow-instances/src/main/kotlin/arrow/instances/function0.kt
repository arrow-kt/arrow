package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(Function0::class)
interface Function0FunctorInstance : Functor<ForFunction0> {
    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.extract().map(f)
}

@instance(Function0::class)
interface Function0ApplicativeInstance : Applicative<ForFunction0> {
    override fun <A, B> ap(fa: Function0Of<A>, ff: Function0Of<kotlin.Function1<A, B>>): Function0<B> =
            fa.extract().ap(ff)

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0MonadInstance : Monad<ForFunction0> {
    override fun <A, B> ap(fa: Function0Of<A>, ff: Function0Of<kotlin.Function1<A, B>>): Function0<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: Function0Of<A>, f: kotlin.Function1<A, Function0Of<B>>): Function0<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)
}

@instance(Function0::class)
interface Function0ComonadInstance : Comonad<ForFunction0> {
    override fun <A, B> coflatMap(fa: Function0Of<A>, f: kotlin.Function1<Function0Of<A>, B>): Function0<B> =
            fa.extract().coflatMap(f)

    override fun <A> extractM(fa: Function0Of<A>): A =
            fa.extract().extractM()

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.extract().map(f)
}

@instance(Function0::class)
interface Function0BimonadInstance : Bimonad<ForFunction0> {
    override fun <A, B> ap(fa: Function0Of<A>, ff: Function0Of<kotlin.Function1<A, B>>): Function0<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: Function0Of<A>, f: kotlin.Function1<A, Function0Of<B>>): Function0<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
            Function0.tailRecM(a, f)

    override fun <A, B> map(fa: Function0Of<A>, f: kotlin.Function1<A, B>): Function0<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Function0<A> =
            Function0.pure(a)

    override fun <A, B> coflatMap(fa: Function0Of<A>, f: kotlin.Function1<Function0Of<A>, B>): Function0<B> =
            fa.extract().coflatMap(f)

    override fun <A> extractM(fa: Function0Of<A>): A =
            fa.extract().extractM()
}
