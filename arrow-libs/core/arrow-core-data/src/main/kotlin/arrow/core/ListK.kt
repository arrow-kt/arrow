package arrow.core

@Deprecated("ListK object is deprecated, and will be removed in 1.0.0")
object ListK {

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, map)", "arrow.core.mapN")
  )
  inline fun <B, C, D> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    map: (B, C) -> D
  ): List<D> =
    mapN(b, c, unit, unit, unit, unit, unit, unit, unit, unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, map)", "arrow.core.mapN")
  )
  inline fun <B, C, D, E> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    map: (B, C, D) -> E
  ): List<E> =
    mapN(b, c, d, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, map)", "arrow.core.mapN")
  )
  inline fun <B, C, D, E, F> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    map: (B, C, D, E) -> F
  ): List<F> =
    mapN(b, c, d, e, unit, unit, unit, unit, unit, unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, f, map)", "arrow.core.mapN")
  )
  inline fun <B, C, D, E, F, G> mapN(
    b: Iterable<B>,
    c: Iterable<C>,
    d: Iterable<D>,
    e: Iterable<E>,
    f: Iterable<F>,
    map: (B, C, D, E, F) -> G
  ): List<G> =
    mapN(b, c, d, e, f, unit, unit, unit, unit, unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, f, g, map)", "arrow.core.mapN")
  )
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

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, f, g, h, map)", "arrow.core.mapN")
  )
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

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, f, g, h, i, map)", "arrow.core.mapN")
  )
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

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, f, g, h, i, j, map)", "arrow.core.mapN")
  )
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

  @Deprecated(
    "mapN for Iterable has become a top-level function.",
    ReplaceWith("mapN(b, c, d, e, f, g, h, i, j, k, map)", "arrow.core.mapN")
  )
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
  ): List<L> {
    val buffer = ArrayList<L>()
      for (bb in b) {
      for (cc in c) {
        for (dd in d) {
          for (ee in e) {
            for (ff in f) {
              for (gg in g) {
                for (hh in h) {
                  for (ii in i) {
                    for (jj in j) {
                      for (kk in k) {
                        buffer.add(map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk))
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }}
      return buffer
    }
}
