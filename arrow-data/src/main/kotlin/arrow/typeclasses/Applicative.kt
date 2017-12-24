@file:Suppress("UNUSED_PARAMETER")

package arrow

interface Applicative<F> : Functor<F>, Typeclass {

    fun <A> pure(a: A): HK<F, A>

    fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>

    fun <A, B> product(fa: HK<F, A>, fb: HK<F, B>): HK<F, Tuple2<A, B>> = ap(fb, map(fa) { a: A -> { b: B -> Tuple2(a, b) } })

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: HK<F, A>, fb: HK<F, B>, f: (Tuple2<A, B>) -> Z): HK<F, Z> = map(product(fa, fb), f)

    fun <A, B, Z> map2Eval(fa: HK<F, A>, fb: Eval<HK<F, B>>, f: (Tuple2<A, B>) -> Z): Eval<HK<F, Z>> = fb.map { fc -> map2(fa, fc, f) }
}

// Syntax

inline fun <reified F, A> A.pure(FT: Applicative<F> = applicative()): HK<F, A> = FT.pure(this)

inline fun <reified F, A, B> HK<F, A>.ap(FT: Applicative<F> = applicative(), ff: HK<F, (A) -> B>): HK<F, B> = FT.ap(this, ff)

inline fun <reified F, A, B, Z> HK<F, A>.map2(FT: Applicative<F> = applicative(), fb: HK<F, B>, noinline f: (Tuple2<A, B>) -> Z): HK<F, Z> =
        FT.map2(this, fb, f)

inline fun <reified F, A, B, Z> HK<F, A>.map2Eval(FT: Applicative<F> = applicative(), fb: Eval<HK<F, B>>, noinline f: (Tuple2<A, B>) -> Z): Eval<HK<F, Z>> =
        FT.map2Eval(this, fb, f)

//Applicative Builder

data class Tuple2<out A, out B>(val a: A, val b: B) {
    fun reverse(): Tuple2<B, A> = Tuple2(b, a)
    companion object
}

data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) {
    fun reverse(): Tuple3<C, B, A> = Tuple3(c, b, a)
    companion object
}

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) {
    fun reverse(): Tuple4<D, C, B, A> = Tuple4(d, c, b, a)
    companion object
}

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) {
    fun reverse(): Tuple5<E, D, C, B, A> = Tuple5(e, d, c, b, a)
    companion object
}

data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) {
    fun reverse(): Tuple6<F, E, D, C, B, A> = Tuple6(f, e, d, c, b, a)
    companion object
}

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G) {
    fun reverse(): Tuple7<G, F, E, D, C, B, A> = Tuple7(g, f, e, d, c, b, a)
    companion object
}

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H) {
    fun reverse(): Tuple8<H, G, F, E, D, C, B, A> = Tuple8(h, g, f, e, d, c, b, a)
    companion object
}

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G,
                                                                                 val h: H, val i: I) {
    fun reverse(): Tuple9<I, H, G, F, E, D, C, B, A> = Tuple9(i, h, g, f, e, d, c, b, a)
    companion object
}

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G,
                                                                                         val h: H, val i: I, val j: J) {
    fun reverse(): Tuple10<J, I, H, G, F, E, D, C, B, A> = Tuple10(j, i, h, g, f, e, d, c, b, a)
    companion object
}

infix operator fun <A, B, C, D, E, F, G, H, I, J> Tuple9<A, B, C, D, E, F, G, H, I>.plus(j: J): Tuple10<A, B, C, D, E, F, G, H, I, J> =
        Tuple10(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, j)

infix operator fun <A, B, C, D, E, F, G, H, I> Tuple8<A, B, C, D, E, F, G, H>.plus(i: I): Tuple9<A, B, C, D, E, F, G, H, I> =
        Tuple9(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, i)

infix operator fun <A, B, C, D, E, F, G, H> Tuple7<A, B, C, D, E, F, G>.plus(h: H): Tuple8<A, B, C, D, E, F, G, H> =
        Tuple8(this.a, this.b, this.c, this.d, this.e, this.f, this.g, h)

infix operator fun <A, B, C, D, E, F, G> Tuple6<A, B, C, D, E, F>.plus(g: G): Tuple7<A, B, C, D, E, F, G> =
        Tuple7(this.a, this.b, this.c, this.d, this.e, this.f, g)

infix operator fun <A, B, C, D, E, F> Tuple5<A, B, C, D, E>.plus(f: F): Tuple6<A, B, C, D, E, F> = Tuple6(this.a, this.b, this.c, this.d, this.e, f)
infix operator fun <A, B, C, D, E> Tuple4<A, B, C, D>.plus(e: E): Tuple5<A, B, C, D, E> = Tuple5(this.a, this.b, this.c, this.d, e)
infix operator fun <A, B, C, D> Tuple3<A, B, C>.plus(d: D): Tuple4<A, B, C, D> = Tuple4(this.a, this.b, this.c, d)
infix operator fun <A, B, C> Tuple2<A, B>.plus(c: C): Tuple3<A, B, C> = Tuple3(this.a, this.b, c)
infix fun <A, B> A.toT(b: B): Tuple2<A, B> = Tuple2(this, b)

fun <HKF, A, Z> HK<HKF, A>.product(AP: Applicative<HKF>, other: HK<HKF, Z>): HK<HKF, Tuple2<A, Z>> = AP.product(this, other)

fun <HKF, A, B, Z> HK<HKF, Tuple2<A, B>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null): HK<HKF, Tuple3<A, B, Z>> =
        AP.map(AP.product(this, other), { Tuple3(it.a.a, it.a.b, it.b) })

fun <HKF, A, B, C, Z> HK<HKF, Tuple3<A, B, C>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null): HK<HKF, Tuple4<A, B, C, Z>> =
        AP.map(AP.product(this, other), { Tuple4(it.a.a, it.a.b, it.a.c, it.b) })

fun <HKF, A, B, C, D, Z> HK<HKF, Tuple4<A, B, C, D>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null): HK<HKF, Tuple5<A, B, C, D, Z>> =
        AP.map(AP.product(this, other), { Tuple5(it.a.a, it.a.b, it.a.c, it.a.d, it.b) })

fun <HKF, A, B, C, D, E, Z> HK<HKF, Tuple5<A, B, C, D, E>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null): HK<HKF, Tuple6<A, B, C, D, E, Z>> =
        AP.map(AP.product(this, other), { Tuple6(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.b) })

fun <HKF, A, B, C, D, E, F, Z> HK<HKF, Tuple6<A, B, C, D, E, F>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null): HK<HKF, Tuple7<A, B, C, D, E, F, Z>> =
        AP.map(AP.product(this, other), { Tuple7(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.b) })

fun <HKF, A, B, C, D, E, F, G, Z> HK<HKF, Tuple7<A, B, C, D, E, F, G>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null): HK<HKF, Tuple8<A, B, C, D, E, F, G, Z>> =
        AP.map(AP.product(this, other), { Tuple8(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.b) })

fun <HKF, A, B, C, D, E, F, G, H, Z> HK<HKF, Tuple8<A, B, C, D, E, F, G, H>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null,
        dummyImplicit7: Any? = null): HK<HKF, Tuple9<A, B, C, D, E, F, G, H, Z>> =
        AP.map(AP.product(this, other), { Tuple9(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.a.h, it.b) })

fun <HKF, A, B, C, D, E, F, G, H, I, Z> HK<HKF, Tuple9<A, B, C, D, E, F, G, H, I>>.product(
        AP: Applicative<HKF>,
        other: HK<HKF, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null,
        dummyImplicit7: Any? = null,
        dummyImplicit9: Any? = null): HK<HKF, Tuple10<A, B, C, D, E, F, G, H, I, Z>> =
        AP.map(AP.product(this, other), { Tuple10(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.a.h, it.a.i, it.b) })

fun <HKF, A, B> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>): HK<HKF, Tuple2<A, B>> =
        a.product(this, b)

fun <HKF, A, B, C> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>): HK<HKF, Tuple3<A, B, C>> =
        a.product(this, b).product(this, c)

fun <HKF, A, B, C, D> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>): HK<HKF, Tuple4<A, B, C, D>> =
        a.product(this, b).product(this, c).product(this, d)

fun <HKF, A, B, C, D, E> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>): HK<HKF, Tuple5<A, B, C, D, E>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e)

fun <HKF, A, B, C, D, E, F> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>): HK<HKF, Tuple6<A, B, C, D, E, F>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)

fun <HKF, A, B, C, D, E, F, G> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>): HK<HKF, Tuple7<A, B, C, D, E, F, G>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g)

fun <HKF, A, B, C, D, E, F, G, H> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        h: HK<HKF, H>): HK<HKF, Tuple8<A, B, C, D, E, F, G, H>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h)

fun <HKF, A, B, C, D, E, F, G, H, I> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        h: HK<HKF, H>,
        i: HK<HKF, I>): HK<HKF, Tuple9<A, B, C, D, E, F, G, H, I>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h).product(this, i)

fun <HKF, A, B, C, D, E, F, G, H, I, J> Applicative<HKF>.tupled(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        h: HK<HKF, H>,
        i: HK<HKF, I>,
        j: HK<HKF, J>): HK<HKF, Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g)
                .product(this, h).product(this, i).product(this, j)

fun <HKF, A, B, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        lbd: (Tuple2<A, B>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b), lbd)

fun <HKF, A, B, C, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        lbd: (Tuple3<A, B, C>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c), lbd)

fun <HKF, A, B, C, D, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        lbd: (Tuple4<A, B, C, D>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d), lbd)

fun <HKF, A, B, C, D, E, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        lbd: (Tuple5<A, B, C, D, E>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e), lbd)

fun <HKF, A, B, C, D, E, F, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        lbd: (Tuple6<A, B, C, D, E, F>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f), lbd)

fun <HKF, A, B, C, D, E, F, G, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        lbd: (Tuple7<A, B, C, D, E, F, G>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g), lbd)

fun <HKF, A, B, C, D, E, F, G, H, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        h: HK<HKF, H>,
        lbd: (Tuple8<A, B, C, D, E, F, G, H>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)
                .product(this, g).product(this, h), lbd)

fun <HKF, A, B, C, D, E, F, G, H, I, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        h: HK<HKF, H>,
        i: HK<HKF, I>,
        lbd: (Tuple9<A, B, C, D, E, F, G, H, I>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)
                .product(this, g).product(this, h).product(this, i), lbd)

fun <HKF, A, B, C, D, E, F, G, H, I, J, Z> Applicative<HKF>.map(
        a: HK<HKF, A>,
        b: HK<HKF, B>,
        c: HK<HKF, C>,
        d: HK<HKF, D>,
        e: HK<HKF, E>,
        f: HK<HKF, F>,
        g: HK<HKF, G>,
        h: HK<HKF, H>,
        i: HK<HKF, I>,
        j: HK<HKF, J>,
        lbd: (Tuple10<A, B, C, D, E, F, G, H, I, J>) -> Z): HK<HKF, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)
                .product(this, g).product(this, h).product(this, i).product(this, j), lbd)

inline fun <reified F> applicative(): Applicative<F> = instance(InstanceParametrizedType(Applicative::class.java, listOf(typeLiteral<F>())))
