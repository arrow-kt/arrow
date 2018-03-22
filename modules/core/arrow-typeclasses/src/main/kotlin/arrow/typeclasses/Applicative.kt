@file:Suppress("UNUSED_PARAMETER")

package arrow.typeclasses

import arrow.Kind
import arrow.core.*

interface Applicative<F> : Functor<F> {

    fun <A> pure(a: A): Kind<F, A>

    fun <A, B> ap(fa: Kind<F, A>, ff: Kind<F, (A) -> B>): Kind<F, B>

    fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>> =
            ap(fb, map(this) { a: A -> { b: B -> Tuple2(a, b) } })

    override fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: Kind<F, A>, fb: Kind<F, B>, f: (Tuple2<A, B>) -> Z): Kind<F, Z> = map(fa.product(fb), f)

    fun <A, B, Z> map2Eval(fa: Kind<F, A>, fb: Eval<Kind<F, B>>, f: (Tuple2<A, B>) -> Z): Eval<Kind<F, Z>> = fb.map { fc -> map2(fa, fc, f) }

    fun <A, B, Z> Kind<F, Tuple2<A, B>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null): Kind<F, Tuple3<A, B, Z>> =
            map(other.product(this), { Tuple3(it.b.a, it.b.b, it.a) })

    fun <A, B, C, Z> Kind<F, Tuple3<A, B, C>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null): Kind<F, Tuple4<A, B, C, Z>> =
            map(other.product(this), { Tuple4(it.b.a, it.b.b, it.b.c, it.a) })

    fun <A, B, C, D, Z> Kind<F, Tuple4<A, B, C, D>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null,
            dummyImplicit3: Any? = null): Kind<F, Tuple5<A, B, C, D, Z>> =
            map(other.product(this), { Tuple5(it.b.a, it.b.b, it.b.c, it.b.d, it.a) })

    fun <A, B, C, D, E, Z> Kind<F, Tuple5<A, B, C, D, E>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null,
            dummyImplicit3: Any? = null,
            dummyImplicit4: Any? = null): Kind<F, Tuple6<A, B, C, D, E, Z>> =
            map(other.product(this), { Tuple6(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.a) })

    fun <A, B, C, D, E, FF, Z> Kind<F, Tuple6<A, B, C, D, E, FF>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null,
            dummyImplicit3: Any? = null,
            dummyImplicit4: Any? = null,
            dummyImplicit5: Any? = null): Kind<F, Tuple7<A, B, C, D, E, FF, Z>> =
            map(other.product(this), { Tuple7(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.a) })

    fun <A, B, C, D, E, FF, G, Z> Kind<F, Tuple7<A, B, C, D, E, FF, G>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null,
            dummyImplicit3: Any? = null,
            dummyImplicit4: Any? = null,
            dummyImplicit5: Any? = null,
            dummyImplicit6: Any? = null): Kind<F, Tuple8<A, B, C, D, E, FF, G, Z>> =
            map(other.product(this), { Tuple8(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.b.g, it.a) })

    fun <A, B, C, D, E, FF, G, H, Z> Kind<F, Tuple8<A, B, C, D, E, FF, G, H>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null,
            dummyImplicit3: Any? = null,
            dummyImplicit4: Any? = null,
            dummyImplicit5: Any? = null,
            dummyImplicit6: Any? = null,
            dummyImplicit7: Any? = null): Kind<F, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
            map(other.product(this), { Tuple9(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.b.g, it.b.h, it.a) })

    fun <A, B, C, D, E, FF, G, H, I, Z> Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>>.product(
            other: Kind<F, Z>,
            dummyImplicit: Any? = null,
            dummyImplicit2: Any? = null,
            dummyImplicit3: Any? = null,
            dummyImplicit4: Any? = null,
            dummyImplicit5: Any? = null,
            dummyImplicit6: Any? = null,
            dummyImplicit7: Any? = null,
            dummyImplicit9: Any? = null): Kind<F, Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
            map(other.product(this), { Tuple10(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.b.g, it.b.h, it.b.i, it.a) })

    fun <A, B> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>): Kind<F, Tuple2<A, B>> =
            a.product(b)

    fun <A, B, C> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>): Kind<F, Tuple3<A, B, C>> =
            a.product(b).product(c)

    fun <A, B, C, D> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>): Kind<F, Tuple4<A, B, C, D>> =
            a.product(b).product(c).product(d)

    fun <A, B, C, D, E> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>): Kind<F, Tuple5<A, B, C, D, E>> =
            a.product(b).product(c).product(d).product(e)

    fun <A, B, C, D, E, FF> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>): Kind<F, Tuple6<A, B, C, D, E, FF>> =
            a.product(b).product(c).product(d).product(e).product(f)

    fun <A, B, C, D, E, FF, G> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>): Kind<F, Tuple7<A, B, C, D, E, FF, G>> =
            a.product(b).product(c).product(d).product(e).product(f).product(g)

    fun <A, B, C, D, E, FF, G, H> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            h: Kind<F, H>): Kind<F, Tuple8<A, B, C, D, E, FF, G, H>> =
            a.product(b).product(c).product(d).product(e).product(f).product(g).product(h)

    fun <A, B, C, D, E, FF, G, H, I> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            h: Kind<F, H>,
            i: Kind<F, I>): Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>> =
            a.product(b).product(c).product(d).product(e).product(f).product(g).product(h).product(i)

    fun <A, B, C, D, E, FF, G, H, I, J> tupled(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            h: Kind<F, H>,
            i: Kind<F, I>,
            j: Kind<F, J>): Kind<F, Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
            a.product(b).product(c).product(d).product(e).product(f).product(g)
                    .product(h).product(i).product(j)

    fun <A, B, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            lbd: (Tuple2<A, B>) -> Z): Kind<F, Z> =
            this.map(a.product(b), lbd)

    fun <A, B, C, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            lbd: (Tuple3<A, B, C>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c), lbd)

    fun <A, B, C, D, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            lbd: (Tuple4<A, B, C, D>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d), lbd)

    fun <A, B, C, D, E, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            lbd: (Tuple5<A, B, C, D, E>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d).product(e), lbd)

    fun <A, B, C, D, E, FF, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            lbd: (Tuple6<A, B, C, D, E, FF>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d).product(e).product(f), lbd)

    fun <A, B, C, D, E, FF, G, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            lbd: (Tuple7<A, B, C, D, E, FF, G>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d).product(e).product(f).product(g), lbd)

    fun <A, B, C, D, E, FF, G, H, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            h: Kind<F, H>,
            lbd: (Tuple8<A, B, C, D, E, FF, G, H>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d).product(e).product(f)
                    .product(g).product(h), lbd)

    fun <A, B, C, D, E, FF, G, H, I, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            h: Kind<F, H>,
            i: Kind<F, I>,
            lbd: (Tuple9<A, B, C, D, E, FF, G, H, I>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d).product(e).product(f)
                    .product(g).product(h).product(i), lbd)

    fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
            a: Kind<F, A>,
            b: Kind<F, B>,
            c: Kind<F, C>,
            d: Kind<F, D>,
            e: Kind<F, E>,
            f: Kind<F, FF>,
            g: Kind<F, G>,
            h: Kind<F, H>,
            i: Kind<F, I>,
            j: Kind<F, J>,
            lbd: (Tuple10<A, B, C, D, E, FF, G, H, I, J>) -> Z): Kind<F, Z> =
            this.map(a.product(b).product(c).product(d).product(e).product(f)
                    .product(g).product(h).product(i).product(j), lbd)

}
