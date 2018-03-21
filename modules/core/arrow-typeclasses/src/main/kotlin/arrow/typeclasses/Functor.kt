package arrow.typeclasses

import arrow.*
import arrow.core.Tuple2

interface Functor<F> {

    fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B>

    fun <A, B> lift(f: (A) -> B): (Kind<F, A>) -> Kind<F, B> =
            { fa: Kind<F, A> ->
                map(fa, f)
            }

    fun <A> void(fa: Kind<F, A>): Kind<F, Unit> = map(fa, { _ -> Unit })

    fun <A, B> fproduct(fa: Kind<F, A>, f: (A) -> B): Kind<F, Tuple2<A, B>> = map(fa, { a -> Tuple2(a, f(a)) })

    fun <A, B> `as`(fa: Kind<F, A>, b: B): Kind<F, B> = map(fa, { _ -> b })

    fun <A, B> tupleLeft(fa: Kind<F, A>, b: B): Kind<F, Tuple2<B, A>> = map(fa, { a -> Tuple2(b, a) })

    fun <A, B> tupleRight(fa: Kind<F, A>, b: B): Kind<F, Tuple2<A, B>> = map(fa, { a -> Tuple2(a, b) })

}

fun <F, B, A : B> Functor<F>.widen(fa: Kind<F, A>): Kind<F, B> = fa