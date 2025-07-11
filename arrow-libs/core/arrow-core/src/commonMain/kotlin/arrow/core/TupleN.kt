@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public operator fun <A, B, C> Pair<A, B>.plus(c: C): Triple<A, B, C> = Triple(this.first, this.second, c)
public operator fun <A, B, C, D> Triple<A, B, C>.plus(d: D): Tuple4<A, B, C, D> = Tuple4(this.first, this.second, this.third, d)
public operator fun <A, B, C, D, E> Tuple4<A, B, C, D>.plus(e: E): Tuple5<A, B, C, D, E> = Tuple5(this.first, this.second, this.third, this.fourth, e)
public operator fun <A, B, C, D, E, F> Tuple5<A, B, C, D, E>.plus(f: F): Tuple6<A, B, C, D, E, F> = Tuple6(this.first, this.second, this.third, this.fourth, this.fifth, f)
public operator fun <A, B, C, D, E, F, G> Tuple6<A, B, C, D, E, F>.plus(g: G): Tuple7<A, B, C, D, E, F, G> = Tuple7(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, g)
public operator fun <A, B, C, D, E, F, G, H> Tuple7<A, B, C, D, E, F, G>.plus(h: H): Tuple8<A, B, C, D, E, F, G, H> = Tuple8(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, h)
public operator fun <A, B, C, D, E, F, G, H, I> Tuple8<A, B, C, D, E, F, G, H>.plus(i: I): Tuple9<A, B, C, D, E, F, G, H, I> = Tuple9(this.first, this.second, this.third, this.fourth, this.fifth, this.sixth, this.seventh, this.eighth, i)
