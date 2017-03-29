/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

interface Applicative<F> : Functor<F> {

    fun <A> pure(a: A): HK<F, A>

    fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>

    fun <A, B> product(fa: HK<F, A>, fb: HK<F, B>): HK<F, Tuple2<A, B>> =
            ap(fb, map(fa) { a: A -> { b: B -> Tuple2(a, b) } })

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: HK<F, A>, fb: HK<F, B>, f: (Tuple2<A, B>) -> Z): HK<F, Z> =
            map(product(fa, fb), f)
}

data class Tuple2<out A, out B>(val a: A, val b: B)
data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C)
data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)
data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E)
data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F)
data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G)
data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H)
data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I)
data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J)

fun <μ, A, Z> HK<μ, A>.product(AP: Applicative<μ>, other: HK<μ, Z>): HK<μ, Tuple2<A, Z>> =
        AP.product(this, other)

fun <μ, A, B, Z> HK<μ, Tuple2<A, B>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null): HK<μ, Tuple3<A, B, Z>> =
        AP.map(AP.product(this, other), { Tuple3(it.a.a, it.a.b, it.b) })

fun <μ, A, B, C, Z> HK<μ, Tuple3<A, B, C>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null): HK<μ, Tuple4<A, B, C, Z>> =
        AP.map(AP.product(this, other), { Tuple4(it.a.a, it.a.b, it.a.c, it.b) })

fun <μ, A, B, C, D, Z> HK<μ, Tuple4<A, B, C, D>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null): HK<μ, Tuple5<A, B, C, D, Z>> =
        AP.map(AP.product(this, other), { Tuple5(it.a.a, it.a.b, it.a.c, it.a.d, it.b) })

fun <μ, A, B, C, D, E, Z> HK<μ, Tuple5<A, B, C, D, E>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null): HK<μ, Tuple6<A, B, C, D, E, Z>> =
        AP.map(AP.product(this, other), { Tuple6(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.b) })

fun <μ, A, B, C, D, E, F, Z> HK<μ, Tuple6<A, B, C, D, E, F>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null): HK<μ, Tuple7<A, B, C, D, E, F, Z>> =
        AP.map(AP.product(this, other), { Tuple7(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.b) })

fun <μ, A, B, C, D, E, F, G, Z> HK<μ, Tuple7<A, B, C, D, E, F, G>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null): HK<μ, Tuple8<A, B, C, D, E, F, G, Z>> =
        AP.map(AP.product(this, other), { Tuple8(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.b) })

fun <μ, A, B, C, D, E, F, G, H, Z> HK<μ, Tuple8<A, B, C, D, E, F, G, H>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null,
        dummyImplicit7: Any? = null): HK<μ, Tuple9<A, B, C, D, E, F, G, H, Z>> =
        AP.map(AP.product(this, other), { Tuple9(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.a.h, it.b) })

fun <μ, A, B, C, D, E, F, G, H, I, Z> HK<μ, Tuple9<A, B, C, D, E, F, G, H, I>>.product(
        AP: Applicative<μ>,
        other: HK<μ, Z>,
        dummyImplicit: Any? = null,
        dummyImplicit2: Any? = null,
        dummyImplicit3: Any? = null,
        dummyImplicit4: Any? = null,
        dummyImplicit5: Any? = null,
        dummyImplicit6: Any? = null,
        dummyImplicit7: Any? = null,
        dummyImplicit9: Any? = null): HK<μ, Tuple10<A, B, C, D, E, F, G, H, I, Z>> =
        AP.map(AP.product(this, other), { Tuple10(it.a.a, it.a.b, it.a.c, it.a.d, it.a.e, it.a.f, it.a.g, it.a.h, it.a.i, it.b) })

fun <μ, A, B> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>): HK<μ, Tuple2<A, B>> =
        a.product(this, b)

fun <μ, A, B, C> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>): HK<μ, Tuple3<A, B, C>> =
        a.product(this, b).product(this, c)

fun <μ, A, B, C, D> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>): HK<μ, Tuple4<A, B, C, D>> =
        a.product(this, b).product(this, c).product(this, d)

fun <μ, A, B, C, D, E> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>): HK<μ, Tuple5<A, B, C, D, E>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e)

fun <μ, A, B, C, D, E, F> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>): HK<μ, Tuple6<A, B, C, D, E, F>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f)

fun <μ, A, B, C, D, E, F, G> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>): HK<μ, Tuple7<A, B, C, D, E, F, G>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g)

fun <μ, A, B, C, D, E, F, G, H> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        h: HK<μ, H>): HK<μ, Tuple8<A, B, C, D, E, F, G, H>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h)

fun <μ, A, B, C, D, E, F, G, H, I> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        h: HK<μ, H>,
        i: HK<μ, I>): HK<μ, Tuple9<A, B, C, D, E, F, G, H, I>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h).product(this, i)

fun <μ, A, B, C, D, E, F, G, H, I, J> Applicative<μ>.tupled(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        h: HK<μ, H>,
        i: HK<μ, I>,
        j: HK<μ, J>): HK<μ, Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h).product(this, i).product(this, j)

fun <μ, A, B, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        λ: (Tuple2<A, B>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b), λ)

fun <μ, A, B, C, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        λ: (Tuple3<A, B, C>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c), λ)

fun <μ, A, B, C, D, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        λ: (Tuple4<A, B, C, D>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d), λ)

fun <μ, A, B, C, D, E, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        λ: (Tuple5<A, B, C, D, E>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e), λ)

fun <μ, A, B, C, D, E, F, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        λ: (Tuple6<A, B, C, D, E, F>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f), λ)

fun <μ, A, B, C, D, E, F, G, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        λ: (Tuple7<A, B, C, D, E, F, G>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g), λ)

fun <μ, A, B, C, D, E, F, G, H, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        h: HK<μ, H>,
        λ: (Tuple8<A, B, C, D, E, F, G, H>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h), λ)

fun <μ, A, B, C, D, E, F, G, H, I, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        h: HK<μ, H>,
        i: HK<μ, I>,
        λ: (Tuple9<A, B, C, D, E, F, G, H, I>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h).product(this, i), λ)

fun <μ, A, B, C, D, E, F, G, H, I, J, Z> Applicative<μ>.map(
        a: HK<μ, A>,
        b: HK<μ, B>,
        c: HK<μ, C>,
        d: HK<μ, D>,
        e: HK<μ, E>,
        f: HK<μ, F>,
        g: HK<μ, G>,
        h: HK<μ, H>,
        i: HK<μ, I>,
        j: HK<μ, J>,
        λ: (Tuple10<A, B, C, D, E, F, G, H, I, J>) -> Z): HK<μ, Z> =
        this.map(a.product(this, b).product(this, c).product(this, d).product(this, e).product(this, f).product(this, g).product(this, h).product(this, i).product(this, j), λ)