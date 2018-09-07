@file:Suppress("UNCHECKED_CAST")

package arrow.generic.tup

fun @receiver:Suppress("unused") Tup0Plus.focus0(): Tup0 = Tup()

fun <A> Tup1Plus<A>.focus1(a: A = data[0] as A): Tup1<A> = Tup(a)

fun <A, B> Tup2Plus<A, B>.focus2(a: A = data[0] as A, b: B = data[1] as B): Tup2<A, B> = Tup(a, b)

fun <A, B, C> Tup3Plus<A, B, C>.focus3(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C
): Tup3<A, B, C> =
  Tup(a, b, c)

fun <A, B, C, D> Tup4Plus<A, B, C, D>.focus4(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D
): Tup4<A, B, C, D> =
  Tup(a, b, c, d)

fun <A, B, C, D, E> Tup5Plus<A, B, C, D, E>.focus5(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E
): Tup5<A, B, C, D, E> =
  Tup(a, b, c, d, e)

fun <A, B, C, D, E, F> Tup6Plus<A, B, C, D, E, F>.focus6(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F
): Tup6<A, B, C, D, E, F> =
  Tup(a, b, c, d, e, f)

fun <A, B, C, D, E, F, G> Tup7Plus<A, B, C, D, E, F, G>.focus7(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G
): Tup7<A, B, C, D, E, F, G> =
  Tup(a, b, c, d, e, f, g)

fun <A, B, C, D, E, F, G, H> Tup8Plus<A, B, C, D, E, F, G, H>.focus8(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H
): Tup8<A, B, C, D, E, F, G, H> =
  Tup(a, b, c, d, e, f, g, h)

fun <A, B, C, D, E, F, G, H, I> Tup9Plus<A, B, C, D, E, F, G, H, I>.focus9(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I
): Tup9<A, B, C, D, E, F, G, H, I> =
  Tup(a, b, c, d, e, f, g, h, i)

fun <A, B, C, D, E, F, G, H, I, J> Tup10Plus<A, B, C, D, E, F, G, H, I, J>.focus10(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J
): Tup10<A, B, C, D, E, F, G, H, I, J> =
  Tup(a, b, c, d, e, f, g, h, i, j)

fun <A, B, C, D, E, F, G, H, I, J, K> Tup11Plus<A, B, C, D, E, F, G, H, I, J, K>.focus11(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K
): Tup11<A, B, C, D, E, F, G, H, I, J, K> =
  Tup(a, b, c, d, e, f, g, h, i, j, k)

fun <A, B, C, D, E, F, G, H, I, J, K, L>
  Tup12Plus<A, B, C, D, E, F, G, H, I, J, K, L>.focus12(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L
): Tup12<A, B, C, D, E, F, G, H, I, J, K, L> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M>
  Tup13Plus<A, B, C, D, E, F, G, H, I, J, K, L, M>.focus13(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M
): Tup13<A, B, C, D, E, F, G, H, I, J, K, L, M> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N>
  Tup14Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.focus14(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N
): Tup14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
  Tup15Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.focus15(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O
): Tup15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
  Tup16Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.focus16(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P
): Tup16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>
  Tup17Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.focus17(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q
): Tup17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>
  Tup18Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.focus18(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R
): Tup18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>
  Tup19Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.focus19(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S
): Tup19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>
  Tup20Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.focus20(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S, t: T = data[19] as T
): Tup20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>
  Tup21Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.focus21(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S, t: T = data[19] as T,
  u: U = data[20] as U
): Tup21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
  Tup22Plus<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>.focus22(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S, t: T = data[19] as T,
  u: U = data[20] as U, v: V = data[21] as V
): Tup22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
