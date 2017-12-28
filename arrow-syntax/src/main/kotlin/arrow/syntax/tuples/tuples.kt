package arrow.syntax.tuples

import arrow.*

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

