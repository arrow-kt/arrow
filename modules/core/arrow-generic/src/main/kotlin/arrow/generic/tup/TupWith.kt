package arrow.generic.tup

infix fun <A, B> A.with(b: B): Tup2<A, B> = Tup(this, b)

infix fun <A> Tup0.with(a: A): Tup1<A> = Tup(a)

@JvmName("tup1With")
infix fun <A, B> Tup1<A>.with(b: B): Tup2<A, B> = Tup(data[0], b)

@JvmName("tup2With")
infix fun <A, B, C> Tup2<A, B>.with(c: C): Tup3<A, B, C> = Tup(*data.copyOfRange(0, 2), c)

@JvmName("tup3With")
infix fun <A, B, C, D> Tup3<A, B, C>.with(d: D): Tup4<A, B, C, D> = Tup(*data.copyOfRange(0, 3), d)

@JvmName("tup4With")
infix fun <A, B, C, D, E> Tup4<A, B, C, D>.with(e: E): Tup5<A, B, C, D, E> =
  Tup(*data.copyOfRange(0, 4), e)

@JvmName("tup5With")
infix fun <A, B, C, D, E, F> Tup5<A, B, C, D, E>.with(f: F): Tup6<A, B, C, D, E, F> =
  Tup(*data.copyOfRange(0, 5), f)

@JvmName("tup6With")
infix fun <A, B, C, D, E, F, G> Tup6<A, B, C, D, E, F>.with(g: G): Tup7<A, B, C, D, E, F, G> =
  Tup(*data.copyOfRange(0, 6), g)

@JvmName("tup7With")
infix fun <A, B, C, D, E, F, G, H>
  Tup7<A, B, C, D, E, F, G>.with(h: H): Tup8<A, B, C, D, E, F, G, H> =
  Tup(*data.copyOfRange(0, 7), h)

@JvmName("tup8With")
infix fun <A, B, C, D, E, F, G, H, I>
  Tup8<A, B, C, D, E, F, G, H>.with(i: I): Tup9<A, B, C, D, E, F, G, H, I> =
  Tup(*data.copyOfRange(0, 8), i)

@JvmName("tup9With")
infix fun <A, B, C, D, E, F, G, H, I, J>
  Tup9<A, B, C, D, E, F, G, H, I>.with(j: J): Tup10<A, B, C, D, E, F, G, H, I, J> =
  Tup(*data.copyOfRange(0, 9), j)

@JvmName("tup10With")
infix fun <A, B, C, D, E, F, G, H, I, J, K>
  Tup10<A, B, C, D, E, F, G, H, I, J>.with(k: K): Tup11<A, B, C, D, E, F, G, H, I, J, K> =
  Tup(*data.copyOfRange(0, 10), k)

@JvmName("tup11With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L>
  Tup11<A, B, C, D, E, F, G, H, I, J, K>.with(l: L): Tup12<A, B, C, D, E, F, G, H, I, J, K, L> =
  Tup(*data.copyOfRange(0, 11), l)

@JvmName("tup12With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M>
  Tup12<A, B, C, D, E, F, G, H, I, J, K, L>.with(m: M)
  : Tup13<A, B, C, D, E, F, G, H, I, J, K, L, M> =
  Tup(*data.copyOfRange(0, 12), m)

@JvmName("tup13With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N>
  Tup13<A, B, C, D, E, F, G, H, I, J, K, L, M>.with(n: N)
  : Tup14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> =
  Tup(*data.copyOfRange(0, 13), n)

@JvmName("tup14With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
  Tup14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>.with(o: O)
  : Tup15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> =
  Tup(*data.copyOfRange(0, 14), o)

@JvmName("tup15With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
  Tup15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>.with(p: P)
  : Tup16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> =
  Tup(*data.copyOfRange(0, 15), p)

@JvmName("tup16With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>
  Tup16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>.with(q: Q)
  : Tup17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> =
  Tup(*data.copyOfRange(0, 16), q)

@JvmName("tup17With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>
  Tup17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>.with(r: R)
  : Tup18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> =
  Tup(*data.copyOfRange(0, 17), r)

@JvmName("tup18With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>
  Tup18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>.with(s: S)
  : Tup19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> =
  Tup(*data.copyOfRange(0, 18), s)

@JvmName("tup19With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>
  Tup19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>.with(t: T)
  : Tup20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> =
  Tup(*data.copyOfRange(0, 19), t)

@JvmName("tup20With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>
  Tup20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>.with(u: U)
  : Tup21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> =
  Tup(*data.copyOfRange(0, 20), u)

@JvmName("tup21With")
infix fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
  Tup21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>.with(v: V)
  : Tup22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> =
  Tup(*data.copyOfRange(0, 21), v)
