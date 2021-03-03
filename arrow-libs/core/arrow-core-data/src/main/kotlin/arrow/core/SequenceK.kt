package arrow.core

object SequenceK {
  fun <B, C, D> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    map: (B, C) -> D
  ): Sequence<D> =
    mapN(
      b,
      c,
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit)
    ) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

  fun <B, C, D, E> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    map: (B, C, D) -> E
  ): Sequence<E> =
    mapN(
      b,
      c,
      d,
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit)
    ) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

  fun <B, C, D, E, F> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    map: (B, C, D, E) -> F
  ): Sequence<F> =
    mapN(
      b,
      c,
      d,
      e,
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit)
    ) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

  fun <B, C, D, E, F, G> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    f: Sequence<F>,
    map: (B, C, D, E, F) -> G
  ): Sequence<G> =
    mapN(
      b,
      c,
      d,
      e,
      f,
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit)
    ) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

  fun <B, C, D, E, F, G, H> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    f: Sequence<F>,
    g: Sequence<G>,
    map: (B, C, D, E, F, G) -> H
  ): Sequence<H> =
    mapN(
      b,
      c,
      d,
      e,
      f,
      g,
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit)
    ) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

  fun <B, C, D, E, F, G, H, I> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    f: Sequence<F>,
    g: Sequence<G>,
    h: Sequence<H>,
    map: (B, C, D, E, F, G, H) -> I
  ): Sequence<I> =
    mapN(
      b,
      c,
      d,
      e,
      f,
      g,
      h,
      sequenceOf(Unit),
      sequenceOf(Unit),
      sequenceOf(Unit)
    ) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

  fun <B, C, D, E, F, G, H, I, J> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    f: Sequence<F>,
    g: Sequence<G>,
    h: Sequence<H>,
    i: Sequence<I>,
    map: (B, C, D, E, F, G, H, I) -> J
  ): Sequence<J> =
    mapN(b, c, d, e, f, g, h, i, sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, e, f, g, h, i, _, _ ->
      map(
        b,
        c,
        d,
        e,
        f,
        g,
        h,
        i
      )
    }

  fun <B, C, D, E, F, G, H, I, J, K> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    f: Sequence<F>,
    g: Sequence<G>,
    h: Sequence<H>,
    i: Sequence<I>,
    j: Sequence<J>,
    map: (B, C, D, E, F, G, H, I, J) -> K
  ): Sequence<K> =
    mapN(b, c, d, e, f, g, h, i, j, sequenceOf(Unit)) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

  fun <B, C, D, E, F, G, H, I, J, K, L> mapN(
    b: Sequence<B>,
    c: Sequence<C>,
    d: Sequence<D>,
    e: Sequence<E>,
    f: Sequence<F>,
    g: Sequence<G>,
    h: Sequence<H>,
    i: Sequence<I>,
    j: Sequence<J>,
    k: Sequence<K>,
    map: (B, C, D, E, F, G, H, I, J, K) -> L
  ): Sequence<L> =
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
