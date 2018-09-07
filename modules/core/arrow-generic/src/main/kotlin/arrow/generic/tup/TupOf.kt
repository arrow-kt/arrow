package arrow.generic.tup

fun tupOf(): Tup0 = Tup()

fun <A> tupOf(a: A): Tup1<A> = Tup(a)

fun <A, B> tupOf(a: A, b: B): Tup2<A, B> = Tup(a, b)

fun <A, B, C> tupOf(a: A, b: B, c: C): Tup3<A, B, C> = Tup(a, b, c)

fun <A, B, C, D> tupOf(a: A, b: B, c: C, d: D): Tup4<A, B, C, D> = Tup(a, b, c, d)

fun <A, B, C, D, E> tupOf(a: A, b: B, c: C, d: D, e: E): Tup5<A, B, C, D, E> = Tup(a, b, c, d, e)

fun <A, B, C, D, E, F> tupOf(a: A, b: B, c: C, d: D, e: E, f: F): Tup6<A, B, C, D, E, F> =
  Tup(a, b, c, d, e, f)

fun <A, B, C, D, E, F, G> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G
): Tup7<A, B, C, D, E, F, G> =
  Tup(a, b, c, d, e, f, g)

fun <A, B, C, D, E, F, G, H> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H
): Tup8<A, B, C, D, E, F, G, H> =
  Tup(a, b, c, d, e, f, g, h)

fun <A, B, C, D, E, F, G, H, I> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I
): Tup9<A, B, C, D, E, F, G, H, I> =
  Tup(a, b, c, d, e, f, g, h, i)

fun <A, B, C, D, E, F, G, H, I, J> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J
): Tup10<A, B, C, D, E, F, G, H, I, J> =
  Tup(a, b, c, d, e, f, g, h, i, j)

fun <A, B, C, D, E, F, G, H, I, J, K> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K
): Tup11<A, B, C, D, E, F, G, H, I, J, K> =
  Tup(a, b, c, d, e, f, g, h, i, j, k)

fun <A, B, C, D, E, F, G, H, I, J, K, L> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L
): Tup12<A, B, C, D, E, F, G, H, I, J, K, L> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M
): Tup13<A, B, C, D, E, F, G, H, I, J, K, L, M> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N
): Tup14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O
): Tup15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P
): Tup16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P,
  q: Q
): Tup17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P,
  q: Q, r: R
): Tup18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P,
  q: Q, r: R, s: S
): Tup19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P,
  q: Q, r: R, s: S, t: T
): Tup20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P,
  q: Q, r: R, s: S, t: T, u: U
): Tup21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> tupOf(
  a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P,
  q: Q, r: R, s: S, t: T, u: U, v: V
): Tup22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =
  Tup(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
