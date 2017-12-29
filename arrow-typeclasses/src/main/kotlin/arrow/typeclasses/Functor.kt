package arrow.typeclasses

import arrow.*
import arrow.core.Tuple2

interface Functor<F> : Typeclass {

    fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>

    fun <A, B> lift(f: (A) -> B): (HK<F, A>) -> HK<F, B> =
            { fa: HK<F, A> ->
                map(fa, f)
            }

    fun <A> void(fa: HK<F, A>): HK<F, Unit> = map(fa, { _ -> Unit })

    fun <A, B> fproduct(fa: HK<F, A>, f: (A) -> B): HK<F, Tuple2<A, B>> = map(fa, { a -> Tuple2(a, f(a)) })

    fun <A, B> `as`(fa: HK<F, A>, b: B): HK<F, B> = map(fa, { _ -> b })

    fun <A, B> tupleLeft(fa: HK<F, A>, b: B): HK<F, Tuple2<B, A>> = map(fa, { a -> Tuple2(b, a) })

    fun <A, B> tupleRight(fa: HK<F, A>, b: B): HK<F, Tuple2<A, B>> = map(fa, { a -> Tuple2(a, b) })

}

fun <F, B, A : B> Functor<F>.widen(fa: HK<F, A>): HK<F, B> = fa

inline fun <reified F> functor(): Functor<F> = instance(InstanceParametrizedType(Functor::class.java, listOf(typeLiteral<F>())))