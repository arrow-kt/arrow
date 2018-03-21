@file:Suppress("UNUSED_PARAMETER")

package arrow.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2

interface Applicative<F> : Functor<F> {

    fun <A> pure(a: A): Kind<F, A>

    fun <A, B> ap(fa: Kind<F, A>, ff: Kind<F, (A) -> B>): Kind<F, B>

    fun <A, B> product(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, Tuple2<A, B>> = ap(fb, map(fa) { a: A -> { b: B -> Tuple2(a, b) } })

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: Kind<F, A>, fb: Kind<F, B>, f: (Tuple2<A, B>) -> Z): Kind<F, Z> = map(product(fa, fb), f)

    fun <A, B, Z> map2Eval(fa: Kind<F, A>, fb: Eval<Kind<F, B>>, f: (Tuple2<A, B>) -> Z): Eval<Kind<F, Z>> = fb.map { fc -> map2(fa, fc, f) }
}