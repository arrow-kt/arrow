package arrow.core

object ListK {

  inline fun <B, C, D> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    map: (B, C) -> D
  ): List<D> =
    mapN(b, c, unit, unit, unit, unit, unit, unit, unit, unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

  inline fun <B, C, D, E> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    map: (B, C, D) -> E
  ): List<E> =
    mapN(b, c, d, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

  inline fun <B, C, D, E, F> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    map: (B, C, D, E) -> F
  ): List<F> =
    mapN(b, c, d, e, unit, unit, unit, unit, unit, unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

  inline fun <B, C, D, E, F, G> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    map: (B, C, D, E, F) -> G
  ): List<G> =
    mapN(b, c, d, e, f, unit, unit, unit, unit, unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

  inline fun <B, C, D, E, F, G, H> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    g: Iterable<G>,
    map: (B, C, D, E, F, G) -> H
  ): List<H> =
    mapN(b, c, d, e, f, g, unit, unit, unit, unit) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

  inline fun <B, C, D, E, F, G, H, I> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    g: Iterable<G>,
    h: Iterable<H>,
    map: (B, C, D, E, F, G, H) -> I
  ): List<I> =
    mapN(b, c, d, e, f, g, h, unit, unit, unit) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

  inline fun <B, C, D, E, F, G, H, I, J> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    g: Iterable<G>,
    h: Iterable<H>,
    i: Iterable<I>,
    map: (B, C, D, E, F, G, H, I) -> J
  ): List<J> =
    mapN(b, c, d, e, f, g, h, i, unit, unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

  inline fun <B, C, D, E, F, G, H, I, J, K> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    g: Iterable<G>,
    h: Iterable<H>,
    i: Iterable<I>,
    j: Iterable<J>,
    map: (B, C, D, E, F, G, H, I, J) -> K
  ): List<K> =
    mapN(b, c, d, e, f, g, h, i, j, unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

  inline fun <B, C, D, E, F, G, H, I, J, K, L> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    g: Iterable<G>,
    h: Iterable<H>,
    i: Iterable<I>,
    j: Iterable<J>,
    k: Iterable<K>,
    map: (B, C, D, E, F, G, H, I, J, K) -> L
  ): List<L> =
    b.flatMap { bb ->
      c.flatMap { cc ->
        d.flatMap { dd ->
          e.flatMap { ee ->
            f.flatMap { ff ->
              g.flatMap { gg ->
                h.flatMap { hh ->
                  i.flatMap { ii ->
                    j.flatMap { jj ->
                      k.map { kk ->
                        map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
}
