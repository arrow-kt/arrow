@file:Suppress("UNCHECKED_CAST")

package arrow.generic.tup

fun <A, A1, TS : TupN> Tup1Cons<A, TS>.map1(f: (A) -> A1): Tup1Cons<A1, TS> =
  Tup(f(data[0] as A), *data.copyOfRange(1, data.size))

fun <A, B, B1, TS : TupN> Tup2Cons<A, B, TS>.map2(f: (B) -> B1): Tup2Cons<A, B1, TS> =
  Tup(data[0], f(data[1] as B), *data.copyOfRange(2, data.size))

fun <A, B, C, C1, TS : TupN> Tup3Cons<A, B, C, TS>.map3(f: (C) -> C1): Tup3Cons<A, B, C1, TS> =
  Tup(*data.copyOfRange(0, 2), f(data[2] as C), *data.copyOfRange(3, data.size))

fun <A, B, C, D, D1, TS : TupN>
  Tup4Cons<A, B, C, D, TS>.map4(f: (D) -> D1): Tup4Cons<A, B, C, D1, TS> =
  Tup(*data.copyOfRange(0, 3), f(data[3] as D), *data.copyOfRange(4, data.size))

fun <A, B, C, D, E, E1, TS : TupN>
  Tup5Cons<A, B, C, D, E, TS>.map5(f: (E) -> E1): Tup5Cons<A, B, C, D, E1, TS> =
  Tup(*data.copyOfRange(0, 4), f(data[4] as E), *data.copyOfRange(5, data.size))

fun <A, B, C, D, E, F, F1, TS : TupN>
  Tup6Cons<A, B, C, D, E, F, TS>.map6(f: (F) -> F1): Tup6Cons<A, B, C, D, E, F1, TS> =
  Tup(*data.copyOfRange(0, 5), f(data[5] as F), *data.copyOfRange(6, data.size))

fun <A, B, C, D, E, F, G, G1, TS : TupN>
  Tup7Cons<A, B, C, D, E, F, G, TS>.map7(f: (G) -> G1): Tup7Cons<A, B, C, D, E, F, G1, TS> =
  Tup(*data.copyOfRange(0, 6), f(data[6] as G), *data.copyOfRange(7, data.size))

fun <A, B, C, D, E, F, G, H, H1, TS : TupN>
  Tup8Cons<A, B, C, D, E, F, G, H, TS>.map8(f: (H) -> H1): Tup8Cons<A, B, C, D, E, F, G, H1, TS> =
  Tup(*data.copyOfRange(0, 7), f(data[7] as H), *data.copyOfRange(8, data.size))

fun <A, B, C, D, E, F, G, H, I, I1, TS : TupN>
  Tup9Cons<A, B, C, D, E, F, G, H, I, TS>.map9(f: (I) -> I1)
  : Tup9Cons<A, B, C, D, E, F, G, H, I1, TS> =
  Tup(*data.copyOfRange(0, 8), f(data[8] as I), *data.copyOfRange(9, data.size))

fun <A, B, C, D, E, F, G, H, I, J, J1, TS : TupN>
  Tup10Cons<A, B, C, D, E, F, G, H, I, J, TS>.map10(f: (J) -> J1)
  : Tup10Cons<A, B, C, D, E, F, G, H, I, J1, TS> =
  Tup(*data.copyOfRange(0, 9), f(data[9] as J), *data.copyOfRange(10, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, K1, TS : TupN>
  Tup11Cons<A, B, C, D, E, F, G, H, I, J, K, TS>.map11(f: (K) -> K1)
  : Tup11Cons<A, B, C, D, E, F, G, H, I, J, K1, TS> =
  Tup(*data.copyOfRange(0, 10), f(data[10] as K), *data.copyOfRange(11, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, L1, TS : TupN>
  Tup12Cons<A, B, C, D, E, F, G, H, I, J, K, L, TS>.map12(f: (L) -> L1)
  : Tup12Cons<A, B, C, D, E, F, G, H, I, J, K, L1, TS> =
  Tup(*data.copyOfRange(0, 11), f(data[11] as L), *data.copyOfRange(12, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, M1, TS : TupN>
  Tup13Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, TS>.map13(f: (M) -> M1)
  : Tup13Cons<A, B, C, D, E, F, G, H, I, J, K, L, M1, TS> =
  Tup(*data.copyOfRange(0, 12), f(data[12] as M), *data.copyOfRange(13, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, N1, TS : TupN>
  Tup14Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, TS>.map14(f: (N) -> N1)
  : Tup14Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N1, TS> =
  Tup(*data.copyOfRange(0, 13), f(data[13] as N), *data.copyOfRange(14, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, O1, TS : TupN>
  Tup15Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, TS>.map15(f: (O) -> O1)
  : Tup15Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O1, TS> =
  Tup(*data.copyOfRange(0, 14), f(data[14] as O), *data.copyOfRange(15, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, P1, TS : TupN>
  Tup16Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, TS>.map16(f: (P) -> P1)
  : Tup16Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P1, TS> =
  Tup(*data.copyOfRange(0, 15), f(data[15] as P), *data.copyOfRange(16, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, Q1, TS : TupN>
  Tup17Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, TS>.map17(f: (Q) -> Q1)
  : Tup17Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q1, TS> =
  Tup(*data.copyOfRange(0, 16), f(data[16] as Q), *data.copyOfRange(17, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, R1, TS : TupN>
  Tup18Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, TS>.map18(f: (R) -> R1)
  : Tup18Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R1, TS> =
  Tup(*data.copyOfRange(0, 17), f(data[17] as R), *data.copyOfRange(18, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, S1, TS : TupN>
  Tup19Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, TS>.map19(f: (S) -> S1)
  : Tup19Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S1, TS> =
  Tup(*data.copyOfRange(0, 18), f(data[18] as S), *data.copyOfRange(19, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, T1, TS : TupN>
  Tup20Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, TS>.map20(f: (T) -> T1)
  : Tup20Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T1, TS> =
  Tup(*data.copyOfRange(0, 19), f(data[19] as T), *data.copyOfRange(20, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, U1, TS : TupN>
  Tup21Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, TS>.map21(f: (U) -> U1)
  : Tup21Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U1, TS> =
  Tup(*data.copyOfRange(0, 20), f(data[20] as U), *data.copyOfRange(21, data.size))

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, V1, TS : TupN>
  Tup22Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, TS>.map22(
  f: (V) -> V1
): Tup22Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V1, TS> =
  Tup(*data.copyOfRange(0, 21), f(data[21] as V), *data.copyOfRange(22, data.size))
