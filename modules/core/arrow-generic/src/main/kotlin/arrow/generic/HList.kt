package arrow.generic

/**
 * A heterogeneous list of values that preserves type information
 *
 * [HList] is a *singly linked list* where:
 * - [HNil] is the empty list
 * - [HCons] holds one payload value ([HCons.head]) and a reference to the rest of the list ([HCons.tail])
 */
sealed class HList {
  abstract fun size(): Int
}

data class HCons<out H, out T : HList>(val head: H, val tail: T) : HList() {
  override fun size(): Int = 1 + tail.size()
}

object HNil : HList() {
  override fun size(): Int = 0
}

/**
 * HList supported arity is up to product of 22 elements
 */
typealias HList1<A> = HCons<A, HNil>

typealias HList2<A, B> = HCons<A, HCons<B, HNil>>
typealias HList3<A, B, C> = HCons<A, HCons<B, HCons<C, HNil>>>
typealias HList4<A, B, C, D> = HCons<A, HCons<B, HCons<C, HCons<D, HNil>>>>
typealias HList5<A, B, C, D, E> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HNil>>>>>
typealias HList6<A, B, C, D, E, F> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HNil>>>>>>
typealias HList7<A, B, C, D, E, F, G> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HNil>>>>>>>
typealias HList8<A, B, C, D, E, F, G, H> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HNil>>>>>>>>
typealias HList9<A, B, C, D, E, F, G, H, I> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HNil>>>>>>>>>
typealias HList10<A, B, C, D, E, F, G, H, I, J> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HNil>>>>>>>>>>
typealias HList11<A, B, C, D, E, F, G, H, I, J, K> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HNil>>>>>>>>>>>
typealias HList12<A, B, C, D, E, F, G, H, I, J, K, L> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HNil>>>>>>>>>>>>
typealias HList13<A, B, C, D, E, F, G, H, I, J, K, L, M> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HNil>>>>>>>>>>>>>
typealias HList14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HNil>>>>>>>>>>>>>>
typealias HList15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HNil>>>>>>>>>>>>>>>
typealias HList16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HNil>>>>>>>>>>>>>>>>
typealias HList17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HCons<Q, HNil>>>>>>>>>>>>>>>>>
typealias HList18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HCons<Q, HCons<R, HNil>>>>>>>>>>>>>>>>>>
typealias HList19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HCons<Q, HCons<R, HCons<S, HNil>>>>>>>>>>>>>>>>>>>
typealias HList20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HCons<Q, HCons<R, HCons<S, HCons<T, HNil>>>>>>>>>>>>>>>>>>>>
typealias HList21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HCons<Q, HCons<R, HCons<S, HCons<T, HCons<U, HNil>>>>>>>>>>>>>>>>>>>>>
typealias HList22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = HCons<A, HCons<B, HCons<C, HCons<D, HCons<E, HCons<F, HCons<G, HCons<H, HCons<I, HCons<J, HCons<K, HCons<L, HCons<M, HCons<N, HCons<O, HCons<P, HCons<Q, HCons<R, HCons<S, HCons<T, HCons<U, HCons<V, HNil>>>>>>>>>>>>>>>>>>>>>>

/**
 * HList factories create proper heterogeneous lists that preserve type information up to arity 22
 */
fun <A> hListOf(a: A): HList1<A> = HList1(a, HNil)

fun <A, B> hListOf(a: A, b: B): HList2<A, B> = HList2(a, HCons(b, HNil))
fun <A, B, C> hListOf(a: A, b: B, c: C): HList3<A, B, C> = HList3(a, HCons(b, HCons(c, HNil)))
fun <A, B, C, D> hListOf(a: A, b: B, c: C, d: D): HList4<A, B, C, D> = HList4(a, HCons(b, HCons(c, HCons(d, HNil))))
fun <A, B, C, D, E> hListOf(a: A, b: B, c: C, d: D, e: E): HList5<A, B, C, D, E> = HList5(a, HCons(b, HCons(c, HCons(d, HCons(e, HNil)))))
fun <A, B, C, D, E, F> hListOf(a: A, b: B, c: C, d: D, e: E, f: F): HList6<A, B, C, D, E, F> = HList6(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HNil))))))
fun <A, B, C, D, E, F, G> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G): HList7<A, B, C, D, E, F, G> = HList7(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HNil)))))))
fun <A, B, C, D, E, F, G, H> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H): HList8<A, B, C, D, E, F, G, H> = HList8(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HNil))))))))
fun <A, B, C, D, E, F, G, H, I> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I): HList9<A, B, C, D, E, F, G, H, I> = HList9(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HNil)))))))))
fun <A, B, C, D, E, F, G, H, I, J> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J): HList10<A, B, C, D, E, F, G, H, I, J> = HList10(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HNil))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K): HList11<A, B, C, D, E, F, G, H, I, J, K> = HList11(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HNil)))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L): HList12<A, B, C, D, E, F, G, H, I, J, K, L> = HList12(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HNil))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M): HList13<A, B, C, D, E, F, G, H, I, J, K, L, M> = HList13(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HNil)))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N): HList14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> = HList14(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HNil))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O): HList15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> = HList15(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HNil)))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P): HList16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> = HList16(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HNil))))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q): HList17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> = HList17(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HCons(q, HNil)))))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R): HList18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> = HList18(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HCons(q, HCons(r, HNil))))))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S): HList19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> = HList19(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HCons(q, HCons(r, HCons(s, HNil)))))))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T): HList20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = HList20(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HCons(q, HCons(r, HCons(s, HCons(t, HNil))))))))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U): HList21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = HList21(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HCons(q, HCons(r, HCons(s, HCons(t, HCons(u, HNil)))))))))))))))))))))
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> hListOf(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J, k: K, l: L, m: M, n: N, o: O, p: P, q: Q, r: R, s: S, t: T, u: U, v: V): HList22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = HList22(a, HCons(b, HCons(c, HCons(d, HCons(e, HCons(f, HCons(g, HCons(h, HCons(i, HCons(j, HCons(k, HCons(l, HCons(m, HCons(n, HCons(o, HCons(p, HCons(q, HCons(r, HCons(s, HCons(t, HCons(u, HCons(v, HNil))))))))))))))))))))))
