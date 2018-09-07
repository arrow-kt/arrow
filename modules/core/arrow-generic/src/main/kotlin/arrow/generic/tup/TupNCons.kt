package arrow.generic.tup

typealias Tup0Cons<TS> = Tup<TS>

typealias Tup1Cons<A, TS> = Tup0Cons<TupCons<A, TS>>

typealias Tup2Cons<A, B, TS> = Tup1Cons<A, TupCons<B, TS>>

typealias Tup3Cons<A, B, C, TS> = Tup2Cons<A, B, TupCons<C, TS>>

typealias Tup4Cons<A, B, C, D, TS> = Tup3Cons<A, B, C, TupCons<D, TS>>

typealias Tup5Cons<A, B, C, D, E, TS> = Tup4Cons<A, B, C, D, TupCons<E, TS>>

typealias Tup6Cons<A, B, C, D, E, F, TS> = Tup5Cons<A, B, C, D, E, TupCons<F, TS>>

typealias Tup7Cons<A, B, C, D, E, F, G, TS> = Tup6Cons<A, B, C, D, E, F, TupCons<G, TS>>

typealias Tup8Cons<A, B, C, D, E, F, G, H, TS> = Tup7Cons<A, B, C, D, E, F, G, TupCons<H, TS>>

typealias Tup9Cons<A, B, C, D, E, F, G, H, I, TS> = Tup8Cons<A, B, C, D, E, F, G, H, TupCons<I, TS>>

typealias Tup10Cons<A, B, C, D, E, F, G, H, I, J, TS> =
  Tup9Cons<A, B, C, D, E, F, G, H, I, TupCons<J, TS>>

typealias Tup11Cons<A, B, C, D, E, F, G, H, I, J, K, TS> =
  Tup10Cons<A, B, C, D, E, F, G, H, I, J, TupCons<K, TS>>

typealias Tup12Cons<A, B, C, D, E, F, G, H, I, J, K, L, TS> =
  Tup11Cons<A, B, C, D, E, F, G, H, I, J, K, TupCons<L, TS>>

typealias Tup13Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, TS> =
  Tup12Cons<A, B, C, D, E, F, G, H, I, J, K, L, TupCons<M, TS>>

typealias Tup14Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, TS> =
  Tup13Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, TupCons<N, TS>>

typealias Tup15Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, TS> =
  Tup14Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, TupCons<O, TS>>

typealias Tup16Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, TS> =
  Tup15Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, TupCons<P, TS>>

typealias Tup17Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, TS> =
  Tup16Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, TupCons<Q, TS>>

typealias Tup18Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, TS> =
  Tup17Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, TupCons<R, TS>>

typealias Tup19Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, TS> =
  Tup18Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, TupCons<S, TS>>

typealias Tup20Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, TS> =
  Tup19Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, TupCons<T, TS>>

typealias Tup21Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, TS> =
  Tup20Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, TupCons<U, TS>>

typealias Tup22Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, TS> =
  Tup21Cons<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, TupCons<V, TS>>
