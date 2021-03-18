@file:Suppress("NAME_SHADOWING")

package arrow.core

object Nullable {

  inline fun <A, R> zip(a: A?, fn: (A) -> R): R? =
    zip(a, Unit) { a, _ -> fn(a) }

  inline fun <A, B, R> zip(a: A?, b: B?, fn: (A, B) -> R): R? =
    zip(a, b, Unit) { a, b, _ -> fn(a, b) }

  inline fun <A, B, C, R> zip(a: A?, b: B?, c: C?, fn: (A, B, C) -> R): R? =
    zip(a, b, c, Unit) { a, b, c, _ -> fn(a, b, c) }

  inline fun <A, B, C, D, R> zip(a: A?, b: B?, c: C?, d: D?, fn: (A, B, C, D) -> R): R? =
    zip(a, b, c, d, Unit) { a, b, c, d, _ -> fn(a, b, c, d) }

  inline fun <A, B, C, D, E, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, fn: (A, B, C, D, E) -> R): R? =
    zip(a, b, c, d, e, Unit) { a, b, c, d, e, _ -> fn(a, b, c, d, e) }

  inline fun <A, B, C, D, E, F, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, fn: (A, B, C, D, E, F) -> R): R? =
    zip(a, b, c, d, e, f, Unit) { a, b, c, d, e, f, _ -> fn(a, b, c, d, e, f) }

  inline fun <A, B, C, D, E, F, G, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, fn: (A, B, C, D, E, F, G) -> R): R? =
    zip(a, b, c, d, e, f, g, Unit) { a, b, c, d, e, f, g, _ -> fn(a, b, c, d, e, f, g) }

  inline fun <A, B, C, D, E, F, G, H, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, fn: (A, B, C, D, E, F, G, H) -> R): R? =
    zip(a, b, c, d, e, f, g, h, Unit) { a, b, c, d, e, f, g, h, _ -> fn(a, b, c, d, e, f, g, h) }

  inline fun <A, B, C, D, E, F, G, H, I, R> zip(
    a: A?,
    b: B?,
    c: C?,
    d: D?,
    e: E?,
    f: F?,
    g: G?,
    h: H?,
    i: I?,
    fn: (A, B, C, D, E, F, G, H, I) -> R
  ): R? =
    zip(a, b, c, d, e, f, g, h, i, Unit) { a, b, c, d, e, f, g, h, i, _ -> fn(a, b, c, d, e, f, g, h, i) }

  inline fun <A, B, C, D, E, F, G, H, I, J, R> zip(
    a: A?,
    b: B?,
    c: C?,
    d: D?,
    e: E?,
    f: F?,
    g: G?,
    h: H?,
    i: I?,
    j: J?,
    fn: (A, B, C, D, E, F, G, H, I, J) -> R
  ): R? =
    a?.let { a ->
      b?.let { b ->
        c?.let { c ->
          d?.let { d ->
            e?.let { e ->
              f?.let { f ->
                g?.let { g ->
                  h?.let { h ->
                    i?.let { i ->
                      j?.let { j ->
                        fn(a, b, c, d, e, f, g, h, i, j)
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

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, fn", "arrow.core.mapN")
)
inline fun <A, R> mapN(a: A?, fn: (A) -> R): R? =
  Nullable.zip(a, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, fn", "arrow.core.mapN")
)
inline fun <A, B, R> mapN(a: A?, b: B?, fn: (A, B) -> R): R? =
  Nullable.zip(a, b, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, fn", "arrow.core.mapN")
)
inline fun <A, B, C, R> mapN(a: A?, b: B?, c: C?, fn: (A, B, C) -> R): R? =
  Nullable.zip(a, b, c, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, d, fn", "arrow.core.mapN")
)
inline fun <A, B, C, D, R> mapN(a: A?, b: B?, c: C?, d: D?, fn: (A, B, C, D) -> R): R? =
  Nullable.zip(a, b, c, d, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, d, e, fn", "arrow.core.mapN")
)
inline fun <A, B, C, D, E, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, fn: (A, B, C, D, E) -> R): R? =
  Nullable.zip(a, b, c, d, e, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, d, e, f, fn", "arrow.core.mapN")
)
inline fun <A, B, C, D, E, F, R> mapN(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, fn: (A, B, C, D, E, F) -> R): R? =
  Nullable.zip(a, b, c, d, e, f, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, d, e, f, g, fn", "arrow.core.mapN")
)
inline fun <A, B, C, D, E, F, G, R> mapN(
  a: A?,
  b: B?,
  c: C?,
  d: D?,
  e: E?,
  f: F?,
  g: G?,
  fn: (A, B, C, D, E, F, G) -> R
): R? =
  Nullable.zip(a, b, c, d, e, f, g, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, d, e, f, g, h, fn", "arrow.core.mapN")
)
inline fun <A, B, C, D, E, F, G, H, R> mapN(
  a: A?,
  b: B?,
  c: C?,
  d: D?,
  e: E?,
  f: F?,
  g: G?,
  h: H?,
  fn: (A, B, C, D, E, F, G, H) -> R
): R? =
  Nullable.zip(a, b, c, d, e, f, g, h, fn)

@Deprecated(
  "Top-level mapN function for A? conflicts with other types such as List<A> and Map<K, V>",
  ReplaceWith("Nullable.zip(a, b, c, d, e, f, g, h, i, fn", "arrow.core.mapN")
)
inline fun <A, B, C, D, E, F, G, H, I, R> mapN(
  a: A?,
  b: B?,
  c: C?,
  d: D?,
  e: E?,
  f: F?,
  g: G?,
  h: H?,
  i: I?,
  fn: (A, B, C, D, E, F, G, H, I) -> R
): R? =
  Nullable.zip(a, b, c, d, e, f, g, h, i, fn)
