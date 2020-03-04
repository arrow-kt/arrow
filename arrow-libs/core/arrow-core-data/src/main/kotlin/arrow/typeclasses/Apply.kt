@file:Suppress("UNUSED_PARAMETER")

package arrow.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9

interface Apply<F> : Functor<F> {

  /**
   * Given both the value and the function are within [F], **ap**ply the function to the value.
   *
   * ```kotlin:ank:playground
   * import arrow.core.Option
   * import arrow.core.Some
   * import arrow.core.none
   *
   * fun main() {
   *   //sampleStart
   *   val someF: Option<(Int) -> Long> = Some { i: Int -> i.toLong() + 1 }
   *
   *   val a = Some(3).ap(someF)
   *   val b = none<Int>().ap(someF)
   *   val c = Some(3).ap(none<(Int) -> Long>())
   *   //sampleEnd
   *   println("a: $a, b: $b, c: $c")
   * }
   * ```
   */
  @Deprecated(
    "ap will have its type signature changed to fun <A, B> Kind<F, (A) -> B>.ap(ff: Kind<F, A>): Kind<F, B> in future versions. You can either keep it as is and change it then, or use mapN as a stable replacement",
    ReplaceWith("mapN(this, ff) { (a, f) -> f(a) }")
  )
  fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B>

  @Deprecated(
    "apEval will have its type signature changed to fun <A, B> Kind<F, (A) -> B>.ap(ff: Eval<Kind<F, A>>): Eval<Kind<F, B>> in future versions. You can either keep it as is and change it then, or use map2Eval as a stable replacement",
    ReplaceWith("map2Eval(ff) { (a, f) -> f(a) }")
  )
  fun <A, B> Kind<F, A>.apEval(ff: Eval<Kind<F, (A) -> B>>): Eval<Kind<F, B>> = ff.map { this.ap(it) }

  fun <A, B, Z> Kind<F, A>.map2Eval(fb: Eval<Kind<F, B>>, f: (Tuple2<A, B>) -> Z): Eval<Kind<F, Z>> =
    apEval(fb.map { it.map { b -> { a: A -> f(Tuple2(a, b)) } } })

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, lbd)")
  )
  fun <A, B, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    lbd: (Tuple2<A, B>) -> Z
  ): Kind<F, Z> =
    a.product(b).map(lbd)

  fun <A, B, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    lbd: (Tuple2<A, B>) -> Z
  ): Kind<F, Z> =
    a.product(b).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, lbd)")
  )
  fun <A, B, C, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    lbd: (Tuple3<A, B, C>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).map(lbd)

  fun <A, B, C, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    lbd: (Tuple3<A, B, C>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, lbd)")
  )
  fun <A, B, C, D, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    lbd: (Tuple4<A, B, C, D>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).map(lbd)

  fun <A, B, C, D, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    lbd: (Tuple4<A, B, C, D>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, e, lbd)")
  )
  fun <A, B, C, D, E, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    lbd: (Tuple5<A, B, C, D, E>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).map(lbd)

  fun <A, B, C, D, E, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    lbd: (Tuple5<A, B, C, D, E>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, e, f, lbd)")
  )
  fun <A, B, C, D, E, FF, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    lbd: (Tuple6<A, B, C, D, E, FF>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f).map(lbd)

  fun <A, B, C, D, E, FF, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    lbd: (Tuple6<A, B, C, D, E, FF>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, e, f, g, lbd)")
  )
  fun <A, B, C, D, E, FF, G, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    lbd: (Tuple7<A, B, C, D, E, FF, G>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f).product(g).map(lbd)

  fun <A, B, C, D, E, FF, G, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    lbd: (Tuple7<A, B, C, D, E, FF, G>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f).product(g).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, e, f, g, h, lbd)")
  )
  fun <A, B, C, D, E, FF, G, H, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    lbd: (Tuple8<A, B, C, D, E, FF, G, H>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f)
      .product(g).product(h).map(lbd)

  fun <A, B, C, D, E, FF, G, H, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    lbd: (Tuple8<A, B, C, D, E, FF, G, H>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f)
      .product(g).product(h).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, e, f, g, h, i, lbd)")
  )
  fun <A, B, C, D, E, FF, G, H, I, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    lbd: (Tuple9<A, B, C, D, E, FF, G, H, I>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f)
      .product(g).product(h).product(i).map(lbd)

  fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    lbd: (Tuple9<A, B, C, D, E, FF, G, H, I>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f)
      .product(g).product(h).product(i).map(lbd)

  @Deprecated(
    "map is being renamed to mapN",
    ReplaceWith("mapN(a, b, c, d, e, f, g, h, i, j, lbd)")
  )
  fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>,
    lbd: (Tuple10<A, B, C, D, E, FF, G, H, I, J>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f)
      .product(g).product(h).product(i).product(j).map(lbd)

  fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>,
    lbd: (Tuple10<A, B, C, D, E, FF, G, H, I, J>) -> Z
  ): Kind<F, Z> =
    a.product(b).product(c).product(d).product(e).product(f)
      .product(g).product(h).product(i).product(j).map(lbd)

  fun <A, B, Z> Kind<F, A>.map2(fb: Kind<F, B>, f: (Tuple2<A, B>) -> Z): Kind<F, Z> =
    product(fb).map(f)

  fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    ap(fb.map { b: B -> { a: A -> Tuple2(a, b) } })

  fun <A, B, Z> Kind<F, Tuple2<A, B>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit
  ): Kind<F, Tuple3<A, B, Z>> =
    other.product(this).map { Tuple3(it.b.a, it.b.b, it.a) }

  fun <A, B, C, Z> Kind<F, Tuple3<A, B, C>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit
  ): Kind<F, Tuple4<A, B, C, Z>> =
    other.product(this).map { Tuple4(it.b.a, it.b.b, it.b.c, it.a) }

  fun <A, B, C, D, Z> Kind<F, Tuple4<A, B, C, D>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit,
    dummyImplicit3: Unit = Unit
  ): Kind<F, Tuple5<A, B, C, D, Z>> =
    other.product(this).map { Tuple5(it.b.a, it.b.b, it.b.c, it.b.d, it.a) }

  fun <A, B, C, D, E, Z> Kind<F, Tuple5<A, B, C, D, E>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit,
    dummyImplicit3: Unit = Unit,
    dummyImplicit4: Unit = Unit
  ): Kind<F, Tuple6<A, B, C, D, E, Z>> =
    other.product(this).map { Tuple6(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.a) }

  fun <A, B, C, D, E, FF, Z> Kind<F, Tuple6<A, B, C, D, E, FF>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit,
    dummyImplicit3: Unit = Unit,
    dummyImplicit4: Unit = Unit,
    dummyImplicit5: Unit = Unit
  ): Kind<F, Tuple7<A, B, C, D, E, FF, Z>> =
    other.product(this).map { Tuple7(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.a) }

  fun <A, B, C, D, E, FF, G, Z> Kind<F, Tuple7<A, B, C, D, E, FF, G>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit,
    dummyImplicit3: Unit = Unit,
    dummyImplicit4: Unit = Unit,
    dummyImplicit5: Unit = Unit,
    dummyImplicit6: Unit = Unit
  ): Kind<F, Tuple8<A, B, C, D, E, FF, G, Z>> =
    other.product(this).map { Tuple8(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.b.g, it.a) }

  fun <A, B, C, D, E, FF, G, H, Z> Kind<F, Tuple8<A, B, C, D, E, FF, G, H>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit,
    dummyImplicit3: Unit = Unit,
    dummyImplicit4: Unit = Unit,
    dummyImplicit5: Unit = Unit,
    dummyImplicit6: Unit = Unit,
    dummyImplicit7: Unit = Unit
  ): Kind<F, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    other.product(this).map { Tuple9(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.b.g, it.b.h, it.a) }

  fun <A, B, C, D, E, FF, G, H, I, Z> Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>>.product(
    other: Kind<F, Z>,
    dummyImplicit: Unit = Unit,
    dummyImplicit2: Unit = Unit,
    dummyImplicit3: Unit = Unit,
    dummyImplicit4: Unit = Unit,
    dummyImplicit5: Unit = Unit,
    dummyImplicit6: Unit = Unit,
    dummyImplicit7: Unit = Unit,
    dummyImplicit9: Unit = Unit
  ): Kind<F, Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    other.product(this).map { Tuple10(it.b.a, it.b.b, it.b.c, it.b.d, it.b.e, it.b.f, it.b.g, it.b.h, it.b.i, it.a) }

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b)")
  )
  fun <A, B> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>
  ): Kind<F, Tuple2<A, B>> =
    a.product(b)

  fun <A, B> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>
  ): Kind<F, Tuple2<A, B>> =
    a.product(b)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c)")
  )
  fun <A, B, C> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>
  ): Kind<F, Tuple3<A, B, C>> =
    a.product(b).product(c)

  fun <A, B, C> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>
  ): Kind<F, Tuple3<A, B, C>> =
    a.product(b).product(c)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d)")
  )
  fun <A, B, C, D> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>
  ): Kind<F, Tuple4<A, B, C, D>> =
    a.product(b).product(c).product(d)

  fun <A, B, C, D> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>
  ): Kind<F, Tuple4<A, B, C, D>> =
    a.product(b).product(c).product(d)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d, e)")
  )
  fun <A, B, C, D, E> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>
  ): Kind<F, Tuple5<A, B, C, D, E>> =
    a.product(b).product(c).product(d).product(e)

  fun <A, B, C, D, E> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>
  ): Kind<F, Tuple5<A, B, C, D, E>> =
    a.product(b).product(c).product(d).product(e)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d, e, f)")
  )
  fun <A, B, C, D, E, FF> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>
  ): Kind<F, Tuple6<A, B, C, D, E, FF>> =
    a.product(b).product(c).product(d).product(e).product(f)

  fun <A, B, C, D, E, FF> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>
  ): Kind<F, Tuple6<A, B, C, D, E, FF>> =
    a.product(b).product(c).product(d).product(e).product(f)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d, e, f, g)")
  )
  fun <A, B, C, D, E, FF, G> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>
  ): Kind<F, Tuple7<A, B, C, D, E, FF, G>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g)

  fun <A, B, C, D, E, FF, G> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>
  ): Kind<F, Tuple7<A, B, C, D, E, FF, G>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d, e, f, g, h)")
  )
  fun <A, B, C, D, E, FF, G, H> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>
  ): Kind<F, Tuple8<A, B, C, D, E, FF, G, H>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g).product(h)

  fun <A, B, C, D, E, FF, G, H> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>
  ): Kind<F, Tuple8<A, B, C, D, E, FF, G, H>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g).product(h)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d, e, f, g, h, i)")
  )
  fun <A, B, C, D, E, FF, G, H, I> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>
  ): Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g).product(h).product(i)

  fun <A, B, C, D, E, FF, G, H, I> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>
  ): Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g).product(h).product(i)

  @Deprecated(
    "tupled is being renamed to tupledN",
    ReplaceWith("tupledN(a, b, c, d, e, f, g, h, i, j)")
  )
  fun <A, B, C, D, E, FF, G, H, I, J> tupled(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>
  ): Kind<F, Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g)
      .product(h).product(i).product(j)

  fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
    a: Kind<F, A>,
    b: Kind<F, B>,
    c: Kind<F, C>,
    d: Kind<F, D>,
    e: Kind<F, E>,
    f: Kind<F, FF>,
    g: Kind<F, G>,
    h: Kind<F, H>,
    i: Kind<F, I>,
    j: Kind<F, J>
  ): Kind<F, Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
    a.product(b).product(c).product(d).product(e).product(f).product(g)
      .product(h).product(i).product(j)

  /**
   * Given two actions, it performs them sequentially.
   * Ignores the result of the first action.
   *
   * This is equivalent to *> in Haskell.
   */
  fun <A, B> Kind<F, A>.followedBy(fb: Kind<F, B>): Kind<F, B> =
    mapN(this, fb) { (_, right) -> right }

  /**
   * Given two actions, it performs them sequentially.
   * Discards the result of the second action.
   *
   * This is equivalent to <* in Haskell.
   */
  fun <A, B> Kind<F, A>.apTap(fb: Kind<F, B>): Kind<F, A> =
    mapN(this, fb) { (left, _) -> left }
}
