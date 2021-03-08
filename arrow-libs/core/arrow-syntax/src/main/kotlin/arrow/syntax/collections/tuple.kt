package arrow.syntax.collections

import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.Tuple10
import arrow.core.Tuple11
import arrow.core.Tuple12
import arrow.core.Tuple13
import arrow.core.Tuple14
import arrow.core.Tuple15
import arrow.core.Tuple16
import arrow.core.Tuple17
import arrow.core.Tuple18
import arrow.core.Tuple19
import arrow.core.Tuple20
import arrow.core.Tuple21
import arrow.core.Tuple22

@Deprecated(
  "Tuple2 is deprecated in favor of Kotlin's Pair",
  ReplaceWith("Tuple3(this.a, this.b, c)", "arrow.core.Tuple3")
)
operator fun <A, B, C> Tuple2<A, B>.plus(c: C): Tuple3<A, B, C> = Tuple3(this.a, this.b, c)

@Deprecated(
  "Tuple3 is deprecated in favor of Kotlin's Triple",
  ReplaceWith("Tuple4(this.a, this.b, this.c, d)", "arrow.core.Tuple4")
)
operator fun <A, B, C, D> Tuple3<A, B, C>.plus(d: D): Tuple4<A, B, C, D> = Tuple4(this.a, this.b, this.c, d)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(e)", "arrow.core.plus")
)
operator fun <A, B, C, D, E> Tuple4<A, B, C, D>.plus(e: E): Tuple5<A, B, C, D, E> = Tuple5(this.a, this.b, this.c, this.d, e)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(f)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F> Tuple5<A, B, C, D, E>.plus(f: F): Tuple6<A, B, C, D, E, F> = Tuple6(this.a, this.b, this.c, this.d, this.e, f)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(g)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G> Tuple6<A, B, C, D, E, F>.plus(g: G): Tuple7<A, B, C, D, E, F, G> = Tuple7(this.a, this.b, this.c, this.d, this.e, this.f, g)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(h)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H> Tuple7<A, B, C, D, E, F, G>.plus(h: H): Tuple8<A, B, C, D, E, F, G, H> = Tuple8(this.a, this.b, this.c, this.d, this.e, this.f, this.g, h)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(i)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I> Tuple8<A, B, C, D, E, F, G, H>.plus(i: I): Tuple9<A, B, C, D, E, F, G, H, I> = Tuple9(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, i)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(j)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J> Tuple9<A, B, C, D, E, F, G, H, I>.plus(j: J): Tuple10<A, B, C, D, E, F, G, H, I, J> = Tuple10(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, j)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(k)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K> Tuple10<A, B, C, D, E, F, G, H, I, J>.plus(k: K): Tuple11<A, B, C, D, E, F, G, H, I, J, K> = Tuple11(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, k)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(l)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L> Tuple11<A, B, C, D, E, F, G, H, I, J, K>.plus(l: L): Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> = Tuple12(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, l)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(m)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Tuple12<A, B, C, D, E, F, G, H, I, J, K, L>.plus(m: M): Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> = Tuple13(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, m)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(n)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M>.plus(n: N): Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = Tuple14(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, n)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(o)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.plus(o: O): Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = Tuple15(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, o)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(p)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.plus(p: P): Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = Tuple16(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, p)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(q)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.plus(q: Q): Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = Tuple17(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, this.p, q)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(r)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Tuple17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.plus(r: R): Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = Tuple18(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, this.p, this.q, r)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(s)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Tuple18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.plus(s: S): Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Tuple19(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, this.p, this.q, this.r, s)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(t)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Tuple19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.plus(t: T): Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Tuple20(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, this.p, this.q, this.r, this.s, t)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(u)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Tuple20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.plus(u: U): Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Tuple21(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, this.p, this.q, this.r, this.s, this.t, u)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("plus(v)", "arrow.core.plus")
)
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Tuple21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.plus(v: V): Tuple22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = Tuple22(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j, this.k, this.l, this.m, this.n, this.o, this.p, this.q, this.r, this.s, this.t, this.u, v)
