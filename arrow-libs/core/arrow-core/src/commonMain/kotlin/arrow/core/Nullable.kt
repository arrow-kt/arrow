@file:Suppress("NAME_SHADOWING")

package arrow.core

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmStatic

@OptIn(ExperimentalContracts::class)
public object Nullable {

  @JvmStatic
  @Deprecated(
    "Prefer using the let",
    ReplaceWith("a?.let(fn)"
    )
  )
  public inline fun <A, R> zip(a: A?, fn: (A) -> R): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, Unit) { a, _ -> fn(a) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith("nullable<A, B> { fn(a.bind<A>(), b.bind<B>()) }", "arrow.core.raise.nullable")
  )
  public inline fun <A, B, R> zip(a: A?, b: B?, fn: (A, B) -> R): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, Unit) { a, b, _ -> fn(a, b) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>()) }", "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, R> zip(a: A?, b: B?, c: C?, fn: (A, B, C) -> R): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, Unit) { a, b, c, _ -> fn(a, b, c) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>()) }", "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, R> zip(a: A?, b: B?, c: C?, d: D?, fn: (A, B, C, D) -> R): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, Unit) { a, b, c, d, _ -> fn(a, b, c, d) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D, E> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>(), e.bind<E>()) }",
      "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, E, R> zip(a: A?, b: B?, c: C?, d: D?, e: E?, fn: (A, B, C, D, E) -> R): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, Unit) { a, b, c, d, e, _ -> fn(a, b, c, d, e) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D, E, F> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>(), e.bind<E>(), f.bind<F>()) }",
      "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, E, F, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, fn: (A, B, C, D, E, F) -> R
  ): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, Unit) { a, b, c, d, e, f, _ -> fn(a, b, c, d, e, f) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D, E, F, G> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>(), e.bind<E>(), f.bind<F>(), g.bind<G>()) }",
      "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, E, F, G, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, fn: (A, B, C, D, E, F, G) -> R
  ): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, g, Unit) { a, b, c, d, e, f, g, _ -> fn(a, b, c, d, e, f, g) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D, E, F, G, H> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>(), e.bind<E>(), f.bind<F>(), g.bind<G>(), h.bind<H>()) }",
      "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, E, F, G, H, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, fn: (A, B, C, D, E, F, G, H) -> R
  ): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, g, h, Unit) { a, b, c, d, e, f, g, h, _ -> fn(a, b, c, d, e, f, g, h) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D, E, F, G, H, I> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>(), e.bind<E>(), f.bind<F>(), g.bind<G>(), h.bind<H>(), i.bind<I>()) }",
      "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, E, F, G, H, I, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, i: I?, fn: (A, B, C, D, E, F, G, H, I) -> R
  ): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return zip(a, b, c, d, e, f, g, h, i, Unit) { a, b, c, d, e, f, g, h, i, _ -> fn(a, b, c, d, e, f, g, h, i) }
  }

  @JvmStatic
  @Deprecated(
    "Prefer using the inline nullable DSL",
    ReplaceWith(
      "nullable<A, B, C, D, E, F, G, H, I, J> { fn(a.bind<A>(), b.bind<B>(), c.bind<C>(), d.bind<D>(), e.bind<E>(), f.bind<F>(), g.bind<G>(), h.bind<H>(), i.bind<I>(), j.bind<J>()) }",
      "arrow.core.raise.nullable"
    )
  )
  public inline fun <A, B, C, D, E, F, G, H, I, J, R> zip(
    a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, i: I?, j: J?, fn: (A, B, C, D, E, F, G, H, I, J) -> R
  ): R? {
    contract { callsInPlace(fn, InvocationKind.AT_MOST_ONCE) }
    return a?.let { a ->
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
}
