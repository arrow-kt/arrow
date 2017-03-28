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


fun <F, A, B> HK<F, A>.product(AP: Applicative<F>, other: HK<F, B>): HK<F, Tuple2<A, B>> =
        AP.product(this, other)

fun <F, A, B, C> HK<F, Tuple2<A, B>>.product(AP: Applicative<F>, other: HK<F, C>, dummyImplicit : Any? = null): HK<F, Tuple3<A, B, C>> =
        AP.map(AP.product(this, other), { Tuple3(it.a.a, it.a.b, it.b) })

fun <F, A, B, C, D> HK<F, Tuple3<A, B, C>>.product(AP: Applicative<F>, other: HK<F, D>, dummyImplicit : Any? = null, dummyImplicit2 : Any? = null): HK<F, Tuple4<A, B, C, D>> =
        AP.map(AP.product(this, other), { Tuple4(it.a.a, it.a.b, it.a.c, it.b) })

fun <F, A, B> Applicative<F>.tupled(a : HK<F, A>, b : HK<F, B>) : HK<F, Tuple2<A, B>> =
        a.product(this, b)

fun <F, A, B, C> Applicative<F>.tupled(a : HK<F, A>, b : HK<F, B>, c : HK<F, C>) : HK<F, Tuple3<A, B, C>> =
        a.product(this, b).product(this, c)

fun <F, A, B, C, D> Applicative<F>.tupled(a : HK<F, A>, b : HK<F, B>, c : HK<F, C>, d : HK<F, D>) : HK<F, Tuple4<A, B, C, D>> =
        a.product(this, b).product(this, c).product(this, d)

fun <F, A, B, Z> Applicative<F>.map(a : HK<F, A>, b : HK<F, B>, f: (Tuple2<A, B>) -> Z) : HK<F, Z> =
        this.map(a.product(this, b), f)

fun <F, A, B, C, Z> Applicative<F>.map(a : HK<F, A>, b : HK<F, B>, c : HK<F, C>, f: (Tuple3<A, B, C>) -> Z) : HK<F, Z>  =
        this.map(a.product(this, b).product(this, c), f)

fun <F, A, B, C, D, Z> Applicative<F>.map(a : HK<F, A>, b : HK<F, B>, c : HK<F, C>, d : HK<F, D>, f: (Tuple4<A, B, C, D>) -> Z) : HK<F, Z>  =
        this.map(a.product(this, b).product(this, c).product(this, d), f)
