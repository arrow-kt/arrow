package arrow.syntax.applicative

import arrow.*
import arrow.core.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.applicative

inline fun <reified F, A> A.pure(FT: Applicative<F> = applicative()): Kind<F, A> = FT.pure(this)

inline fun <reified F, A, B> Kind<F, A>.ap(FT: Applicative<F> = applicative(), ff: Kind<F, (A) -> B>): Kind<F, B> = FT.ap(this, ff)

inline fun <reified F, A, B, Z> Kind<F, A>.map2(FT: Applicative<F> = applicative(), fb: Kind<F, B>, noinline f: (Tuple2<A, B>) -> Z): Kind<F, Z> =
        FT.map2(this, fb, f)

inline fun <reified F, A, B, Z> Kind<F, A>.map2Eval(FT: Applicative<F> = applicative(), fb: Eval<Kind<F, B>>, noinline f: (Tuple2<A, B>) -> Z): Eval<Kind<F, Z>> =
        FT.map2Eval(this, fb, f)

fun <HKF, A, Z> Kind<HKF, A>.product(AP: Applicative<HKF>, other: Kind<HKF, Z>): Kind<HKF, Tuple2<A, Z>> = AP.product(this, other)

fun <HKF, A, B, Z> Kind<HKF, Tuple2<A, B>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null): Kind<HKF, Tuple3<A, B, Z>> =
        AP.map(AP.product(this, other), { Tuple3(it.a.a, it.a.b, it.b) })

fun <HKF, A, B, C, Z> Kind<HKF, Tuple3<A, B, C>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null): Kind<HKF, Tuple4<A, B, C, Z>> =
        AP.map(AP.product(this, other), { Tuple4(it.a.a, it.a.b, it.a.c, it.b) })

fun <HKF, A, B, C, D, Z> Kind<HKF, Tuple4<A, B, C, D>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null): Kind<HKF, Tuple5<A, B, C, D, Z>> =
        AP.map(AP.product(this, other), { Tuple5(it.a.a, it.a.b, it.a.c, it.a.d, it.b) })

fun <HKF, A, B, C, D, E, Z> Kind<HKF, Tuple5<A, B, C, D, E>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null): Kind<HKF, Tuple6<A, B, C, D, E, Z>> =
        AP.map(AP.product(this, other), { Tuple6(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.b) })

fun <HKF, A, B, C, D, E, F, Z> Kind<HKF, Tuple6<A, B, C, D, E, F>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null): Kind<HKF, Tuple7<A, B, C, D, E, F, Z>> =
        AP.map(AP.product(this, other), { Tuple7(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.b) })

fun <HKF, A, B, C, D, E, F, G, Z> Kind<HKF, Tuple7<A, B, C, D, E, F, G>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null): Kind<HKF, Tuple8<A, B, C, D, E, F, G, Z>> =
        AP.map(AP.product(this, other), { Tuple8(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.b) })

fun <HKF, A, B, C, D, E, F, G, H, Z> Kind<HKF, Tuple8<A, B, C, D, E, F, G, H>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null,
        dummyImplicit7: Any? = null): Kind<HKF, Tuple9<A, B, C, D, E, F, G, H, Z>> =
        AP.map(AP.product(this, other), { Tuple9(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.a.h, it.b) })

fun <HKF, A, B, C, D, E, F, G, H, I, Z> Kind<HKF, Tuple9<A, B, C, D, E, F, G, H, I>>.product(
        AP: Applicative<HKF>,
        other: Kind<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null,
        dummyImplicit7: Any? = null,
        dummyImplicit9: Any? = null): Kind<HKF, Tuple10<A, B, C, D, E, F, G, H, I, Z>> =
        AP.map(AP.product(this, other), { Tuple10(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.a.h, it.a.i, it.b) })

fun <HKF, A, B> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>): Kind<HKF, Tuple2<A, B>> =
        a.product(this, b)

fun <HKF, A, B, C> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>): Kind<HKF, Tuple3<A, B, C>> =
        a.product(this, b).product(this, c)

fun <HKF, A, B, C, D> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>): Kind<HKF, Tuple4<A, B, C, D>> =
        a.product(this, b).product(this, c).product(this, d)

fun <HKF, A, B, C, D, E> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>): Kind<HKF, Tuple5<A, B, C, D, E>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e)

fun <HKF, A, B, C, D, E, F> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>): Kind<HKF, Tuple6<A, B, C, D, E, F>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)

fun <HKF, A, B, C, D, E, F, G> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>): Kind<HKF, Tuple7<A, B, C, D, E, F, G>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g)

fun <HKF, A, B, C, D, E, F, G, H> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        h: Kind<HKF, H>): Kind<HKF, Tuple8<A, B, C, D, E, F, G, H>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h)

fun <HKF, A, B, C, D, E, F, G, H, I> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        h: Kind<HKF, H>,
        i: Kind<HKF, I>): Kind<HKF, Tuple9<A, B, C, D, E, F, G, H, I>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h).product(this, i)

fun <HKF, A, B, C, D, E, F, G, H, I, J> Applicative<HKF>.tupled(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        h: Kind<HKF, H>,
        i: Kind<HKF, I>,
        j: Kind<HKF, J>): Kind<HKF, Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g)
                .product(this, h).product(this, i).product(this, j)

fun <HKF, A, B, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        lbd: (Tuple2<A, B>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b), lbd)

fun <HKF, A, B, C, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        lbd: (Tuple3<A, B, C>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c), lbd)

fun <HKF, A, B, C, D, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        lbd: (Tuple4<A, B, C, D>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d), lbd)

fun <HKF, A, B, C, D, E, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        lbd: (Tuple5<A, B, C, D, E>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e), lbd)

fun <HKF, A, B, C, D, E, F, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        lbd: (Tuple6<A, B, C, D, E, F>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f), lbd)

fun <HKF, A, B, C, D, E, F, G, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        lbd: (Tuple7<A, B, C, D, E, F, G>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g), lbd)

fun <HKF, A, B, C, D, E, F, G, H, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        h: Kind<HKF, H>,
        lbd: (Tuple8<A, B, C, D, E, F, G, H>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)
                .product(this, g).product(this, h), lbd)

fun <HKF, A, B, C, D, E, F, G, H, I, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        h: Kind<HKF, H>,
        i: Kind<HKF, I>,
        lbd: (Tuple9<A, B, C, D, E, F, G, H, I>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)
                .product(this, g).product(this, h).product(this, i), lbd)

fun <HKF, A, B, C, D, E, F, G, H, I, J, Z> Applicative<HKF>.map(
        a: Kind<HKF, A>,
        b: Kind<HKF, B>,
        c: Kind<HKF, C>,
        d: Kind<HKF, D>,
        e: Kind<HKF, E>,
        f: Kind<HKF, F>,
        g: Kind<HKF, G>,
        h: Kind<HKF, H>,
        i: Kind<HKF, I>,
        j: Kind<HKF, J>,
        lbd: (Tuple10<A, B, C, D, E, F, G, H, I, J>) -> Z): Kind<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)
                .product(this, g).product(this, h).product(this, i).product(this, j), lbd)

inline fun <reified F, A, B> merge(
        op1: () -> A,
        op2: () -> B, AP: Applicative<F> = applicative()): Kind<F, Tuple2<A, B>> =
        AP.tupled(
                AP.pure(op1()),
                AP.pure(op2())
        )
