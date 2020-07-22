@file:Suppress("NAME_SHADOWING")
package arrow.core

inline fun <A, R> mapN(a: A?, fn: (A) -> R): R? =
  mapN(a, Unit) { a, _ -> fn(a) }

inline fun <A, B, R> mapN(a: A?, b: B?, fn: (A, B) -> R): R? =
  mapN(a, b, Unit) { a, b, _ -> fn(a, b) }

inline fun <A, B, C, R> mapN(a: A?, b: B?, c: C?, fn: (A, B, C) -> R): R? =
  mapN(a, b, c, Unit) { a, b, c, _ -> fn(a, b, c) }

inline fun <A, B, C, D, R> mapN(a: A?, b: B?, c: C?, d: D?, fn: (A, B, C, D) -> R): R? =
  mapN(a, b, c, d, Unit) { a, b, c, d, _ -> fn(a, b, c, d) }

inline fun <A, B, C, D, E, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, fn: (A, B, C, D, E) -> R): R? =
  mapN(a, b, c, d, e, Unit) { a, b, c, d, e, _ -> fn(a, b, c, d, e) }

inline fun <A, B, C, D, E, F, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, fn: (A, B, C, D, E, F) -> R): R? =
  mapN(a, b, c, d, e, f, Unit) { a, b, c, d, e, f, _ -> fn(a, b, c, d, e, f) }

inline fun <A, B, C, D, E, F, G, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, fn: (A, B, C, D, E, F, G) -> R): R? =
  mapN(a, b, c, d, e, f, g, Unit) { a, b, c, d, e, f, g, _ -> fn(a, b, c, d, e, f, g) }

inline fun <A, B, C, D, E, F, G, H, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, fn: (A, B, C, D, E, F, G, H) -> R): R? =
  mapN(a, b, c, d, e, f, g, h, Unit) { a, b, c, d, e, f, g, h, _ -> fn(a, b, c, d, e, f, g, h) }

inline fun <A, B, C, D, E, F, G, H, I, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, i: I?, fn: (A, B, C, D, E, F, G, H, I) -> R): R? =
  a?.let { a -> b?.let { b -> c?.let { c -> d?.let { d -> e?.let { e -> f?.let { f -> g?.let { g -> h?.let { h -> i?.let { i ->
    fn(a, b, c, d, e, f, g, h, i)
  } } } } } } } } }
