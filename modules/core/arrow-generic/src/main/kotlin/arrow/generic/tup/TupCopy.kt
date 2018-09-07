@file:Suppress("UNCHECKED_CAST")

package arrow.generic.tup

fun <TS : TupN> Tup0Cons<TS>.copy(): Tup0 = Tup(*data.copyOfRange(0, data.size))

fun <A, TS : TupN> Tup1Cons<A, TS>.copy(a: A = data[0] as A): Tup1Cons<A, TS> =
  Tup(a, *data.copyOfRange(1, data.size))

fun <A, B, TS : TupN> Tup2Cons<A, B, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B
): Tup2Cons<A, B, TS> = Tup(a, b, *data.copyOfRange(2, data.size))

fun <A, B, C, TS : TupN> Tup3Cons<A, B, C, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C
): Tup3Cons<A, B, C, TS> =
  Tup(a, b, c, *data.copyOfRange(3, data.size))

fun <A, B, C, D, TS : TupN> Tup4Cons<A, B, C, D, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D
): Tup4Cons<A, B, C, D, TS> =
  Tup(a, b, c, d, *data.copyOfRange(4, data.size))

fun <A, B, C, D, E, TS : TupN> Tup5Cons<A, B, C, D, E, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E
): Tup5Cons<A, B, C, D, E, TS> =
  Tup(a, b, c, d, e, *data.copyOfRange(5, data.size))

fun <A, B, C, D, E, F, TS : TupN> Tup6Cons<A, B, C, D, E, F, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F
): Tup6Cons<A, B, C, D, E, F, TS> =
  Tup(a, b, c, d, e, f, *data.copyOfRange(6, data.size))

fun <A, B, C, D, E, F, G, TS : TupN> Tup7Cons<A, B, C, D, E, F, G, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G
): Tup7Cons<A, B, C, D, E, F, G, TS> =
  Tup(a, b, c, d, e, f, g, *data.copyOfRange(7, data.size))

fun <A, B, C, D, E, F, G, H, TS : TupN> Tup8Cons<A, B, C, D, E, F, G, H, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H
): Tup8Cons<A, B, C, D, E, F, G, H, TS> =
  Tup(a, b, c, d, e, f, g, h, *data.copyOfRange(8, data.size))

fun <A, B, C, D, E, F, G, H, I, TS : TupN> Tup9Cons<A, B, C, D, E, F, G, H, I, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I
): Tup9Cons<A, B, C, D, E, F, G, H, I, TS> =
  Tup(a, b, c, d, e, f, g, h, i, *data.copyOfRange(9, data.size))

fun <A, B, C, D, E, F, G, H, I, J, TS : TupN> Tup10Cons<A, B, C, D, E, F, G, H, I, J, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J
): Tup10Cons<A, B, C, D, E, F, G, H, I, J, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, *data.copyOfRange(10, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, TS : TupN> Tup11Cons<A, B, C, D, E, F, G, H, I, J, K, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K
): Tup11Cons<A, B, C, D, E, F, G, H, I, J, K, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, *data.copyOfRange(11, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, TS : TupN>
  Tup12Cons<A, B, C, D, E, F, G, H, I, J, K, L, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L
): Tup12Cons<A, B, C, D, E, F, G, H, I, J, K, L, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, *data.copyOfRange(12, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, TS : TupN>
  Tup13Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M
): Tup13Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, *data.copyOfRange(13, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, TS : TupN>
  Tup14Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N
): Tup14Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, *data.copyOfRange(14, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, TS : TupN>
  Tup15Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O
): Tup15Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, *data.copyOfRange(15, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, TS : TupN>
  Tup16Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P
): Tup16Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, *data.copyOfRange(16, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, TS : TupN>
  Tup17Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q
): Tup17Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, *data.copyOfRange(17, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, TS : TupN>
  Tup18Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R
): Tup18Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, *data.copyOfRange(18, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, TS : TupN>
  Tup19Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S
): Tup19Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, *data.copyOfRange(19, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, TS : TupN>
  Tup20Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S, t: T = data[19] as T
): Tup20Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, *data.copyOfRange(20, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, TS : TupN>
  Tup21Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S, t: T = data[19] as T,
  u: U = data[20] as U
): Tup21Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u,
      *data.copyOfRange(21, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, TS : TupN>
  Tup22Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, TS>.copy(
  a: A = data[0] as A, b: B = data[1] as B, c: C = data[2] as C, d: D = data[3] as D,
  e: E = data[4] as E, f: F = data[5] as F, g: G = data[6] as G, h: H = data[7] as H,
  i: I = data[8] as I, j: J = data[9] as J, k: K = data[10] as K, l: L = data[11] as L,
  m: M = data[12] as M, n: N = data[13] as N, o: O = data[14] as O, p: P = data[15] as P,
  q: Q = data[16] as Q, r: R = data[17] as R, s: S = data[18] as S, t: T = data[19] as T,
  u: U = data[20] as U, v: V = data[21] as V
): Tup22Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, TS> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v,
      *data.copyOfRange(22, data.size))
