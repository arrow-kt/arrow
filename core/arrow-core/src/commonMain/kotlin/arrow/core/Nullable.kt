@file:Suppress("NAME_SHADOWING")

package arrow.core

import kotlin.jvm.JvmStatic

public object Nullable {

  @JvmStatic
  public inline fun <A, R> zip(a: A?, fn: (A) -> R): R? =
    zip(a, Unit) { a, _ -> fn(a) }

  @JvmStatic
  public inline fun <A, B, R> zip(a: A?, b: B?, fn: (A, B) -> R): R? =
    zip(a, b, Unit) { a, b, _ -> fn(a, b) }

  @JvmStatic
  public inline fun <A, B, C, R> zip(a: A?, b: B?, c: C?, fn: (A, B, C) -> R): R? =
    zip(a, b, c, Unit) { a, b, c, _ -> fn(a, b, c) }

  @JvmStatic
  public inline fun <A, B, C, D, R> zip(a: A?, b: B?, c: C?, d: D?, fn: (A, B, C, D) -> R): R? =
    zip(a, b, c, d, Unit) { a, b, c, d, _ -> fn(a, b, c, d) }

  @JvmStatic
  public inline fun <A, B, C, D, E, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, fn: (A, B, C, D, E) -> R): R? =
    zip(a, b, c, d, e, Unit) { a, b, c, d, e, _ -> fn(a, b, c, d, e) }

  @JvmStatic
  public inline fun <A, B, C, D, E, F, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, fn: (A, B, C, D, E, F) -> R): R? =
    zip(a, b, c, d, e, f, Unit) { a, b, c, d, e, f, _ -> fn(a, b, c, d, e, f) }

  @JvmStatic
  public inline fun <A, B, C, D, E, F, G, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, fn: (A, B, C, D, E, F, G) -> R): R? =
    zip(a, b, c, d, e, f, g, Unit) { a, b, c, d, e, f, g, _ -> fn(a, b, c, d, e, f, g) }

  @JvmStatic
  public inline fun <A, B, C, D, E, F, G, H, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, fn: (A, B, C, D, E, F, G, H) -> R): R? =
    zip(a, b, c, d, e, f, g, h, Unit) { a, b, c, d, e, f, g, h, _ -> fn(a, b, c, d, e, f, g, h) }

  @JvmStatic
  public inline fun <A, B, C, D, E, F, G, H, I, R> zip(
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

  @JvmStatic
  public inline fun <A, B, C, D, E, F, G, H, I, J, R> zip(
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
