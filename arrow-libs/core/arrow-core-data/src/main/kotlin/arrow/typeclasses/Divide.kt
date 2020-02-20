package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.identity
import arrow.core.toT

/**
 * [Divide] is a typeclass that models the divide part of divide and conquer.
 *
 * [Divide] basically states: Given a Kind<F, A> and a Kind<F, B> and a way to turn C into a tuple of A and B it gives you a Kind<F, C>
 *
 * A useful example could be deriving serializers for a datatype from simpler serializers
 *
 * ```kotlin:ank:playground
 * import arrow.typeclasses.Divide
 * import arrow.Kind
 * import arrow.core.Tuple2
 * import arrow.core.toT
 * import com.example.domain.*
 *
 * data class User(val name: String, val age: Int)
 *
 * val stringSerializer = Serializer<String> { "STRING: $it" }
 * val intSerializer = Serializer<Int> { "INT: $it" }
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *    val userSerializer: Serializer<User> = Serializer.divide().divide(
 *      stringSerializer,
 *      intSerializer
 *   ) { user: User ->
 *      user.name toT user.age
 *   }.fix()
 *
 *   val user = User("John", 31)
 *
 *   val result = userSerializer.func(user)
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ank_macro_hierarchy(arrow.typeclasses.Divide)
 */
interface Divide<F> : Contravariant<F> {

  /**
   * Divide takes two data-types of type `Kind<F, A>` and `Kind<F, B>` and produces a type of `Kind<F, C>` when given
   *  a function from `C -> Tuple2<A, B>`
   *
   * ```kotlin:ank:playground
   * import arrow.typeclasses.Divide
   * import arrow.Kind
   * import arrow.core.Tuple2
   * import arrow.core.toT
   * import com.example.domain.*
   *
   * data class User(val name: String, val age: Int)
   *
   * val stringSerializer = Serializer<String> { "STRING: $it" }
   * val intSerializer = Serializer<Int> { "INT: $it" }
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *    val userSerializer: Serializer<User> = Serializer.divide().divide(
   *      stringSerializer,
   *      intSerializer
   *   ) { user: User ->
   *      user.name toT user.age
   *   }.fix()
   *
   *   val user = User("John", 31)
   *
   *   val result = userSerializer.func(user)
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B, Z> divide(fa: Kind<F, A>, fb: Kind<F, B>, f: (Z) -> Tuple2<A, B>): Kind<F, Z>

  fun <A, B> Kind<F, A>.product(other: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    divide(this, other, ::identity)

  fun <A, B, C> Kind<F, Tuple2<A, B>>.product(
    other: Kind<F, C>,
    dummy: Unit = Unit
  ): Kind<F, Tuple3<A, B, C>> =
    divide(this, other) { Tuple2(it.a toT it.b, it.c) }

  fun <A, B, C, D> Kind<F, Tuple3<A, B, C>>.product(
    other: Kind<F, D>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit
  ): Kind<F, Tuple4<A, B, C, D>> =
    divide(this, other) { Tuple2(Tuple3(it.a, it.b, it.c), it.d) }

  fun <A, B, C, D, E> Kind<F, Tuple4<A, B, C, D>>.product(
    other: Kind<F, E>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit,
    dummy3: Unit = Unit
  ): Kind<F, Tuple5<A, B, C, D, E>> =
    divide(this, other) { Tuple2(Tuple4(it.a, it.b, it.c, it.d), it.e) }

  fun <A, B, C, D, E, FF> Kind<F, Tuple5<A, B, C, D, E>>.product(
    other: Kind<F, FF>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit,
    dummy3: Unit = Unit,
    dummy4: Unit = Unit
  ): Kind<F, Tuple6<A, B, C, D, E, FF>> =
    divide(this, other) { Tuple2(Tuple5(it.a, it.b, it.c, it.d, it.e), it.f) }

  fun <A, B, C, D, E, FF, G> Kind<F, Tuple6<A, B, C, D, E, FF>>.product(
    other: Kind<F, G>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit,
    dummy3: Unit = Unit,
    dummy4: Unit = Unit,
    dummy5: Unit = Unit
  ): Kind<F, Tuple7<A, B, C, D, E, FF, G>> =
    divide(this, other) { Tuple2(Tuple6(it.a, it.b, it.c, it.d, it.e, it.f), it.g) }

  fun <A, B, C, D, E, FF, G, H> Kind<F, Tuple7<A, B, C, D, E, FF, G>>.product(
    other: Kind<F, H>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit,
    dummy3: Unit = Unit,
    dummy4: Unit = Unit,
    dummy5: Unit = Unit,
    dummy6: Unit = Unit
  ): Kind<F, Tuple8<A, B, C, D, E, FF, G, H>> =
    divide(this, other) { Tuple2(Tuple7(it.a, it.b, it.c, it.d, it.e, it.f, it.g), it.h) }

  fun <A, B, C, D, E, FF, G, H, I> Kind<F, Tuple8<A, B, C, D, E, FF, G, H>>.product(
    other: Kind<F, I>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit,
    dummy3: Unit = Unit,
    dummy4: Unit = Unit,
    dummy5: Unit = Unit,
    dummy6: Unit = Unit,
    dummy7: Unit = Unit
  ): Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>> =
    divide(this, other) { Tuple2(Tuple8(it.a, it.b, it.c, it.d, it.e, it.f, it.g, it.h), it.i) }

  fun <A, B, C, D, E, FF, G, H, I, J> Kind<F, Tuple9<A, B, C, D, E, FF, G, H, I>>.product(
    other: Kind<F, J>,
    dummy: Unit = Unit,
    dummy2: Unit = Unit,
    dummy3: Unit = Unit,
    dummy4: Unit = Unit,
    dummy5: Unit = Unit,
    dummy6: Unit = Unit,
    dummy7: Unit = Unit,
    dummy8: Unit = Unit
  ): Kind<F, Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
    divide(this, other) { Tuple2(Tuple9(it.a, it.b, it.c, it.d, it.e, it.f, it.g, it.h, it.i), it.j) }

  fun <A, B, C, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    f: (Z) -> Tuple3<A, B, C>
  ): Kind<F, Z> = divide(fa, fb.product(fc)) {
    val (a, b, c) = f(it)
    a toT (b toT c)
  }

  fun <A, B, C, D, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    f: (Z) -> Tuple4<A, B, C, D>
  ): Kind<F, Z> = divide(fa, fb.product(fc).product(fd)) {
    val (a, b, c, d) = f(it)
    a toT Tuple3(b, c, d)
  }

  fun <A, B, C, D, E, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    f: (Z) -> Tuple5<A, B, C, D, E>
  ): Kind<F, Z> = divide(fa, fb.product(fc).product(fd).product(fe)) {
    val (a, b, c, d, e) = f(it)
    a toT Tuple4(b, c, d, e)
  }

  fun <A, B, C, D, E, FF, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    f: (Z) -> Tuple6<A, B, C, D, E, FF>
  ): Kind<F, Z> = divide(
    fa,
    fb.product(fc).product(fd).product(fe).product(ff)
  ) {
    val (a, b, c, d, e, fff) = f(it)
    a toT Tuple5(b, c, d, e, fff)
  }

  fun <A, B, C, D, E, FF, G, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    f: (Z) -> Tuple7<A, B, C, D, E, FF, G>
  ): Kind<F, Z> = divide(
    fa,
    fb.product(fc).product(fd).product(fe).product(ff).product(fg)
  ) {
    val (a, b, c, d, e, fff, g) = f(it)
    a toT Tuple6(b, c, d, e, fff, g)
  }

  fun <A, B, C, D, E, FF, G, H, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    f: (Z) -> Tuple8<A, B, C, D, E, FF, G, H>
  ): Kind<F, Z> = divide(
    fa,
    fb.product(fc).product(fd).product(fe).product(ff).product(fg).product(fh)
  ) {
    val (a, b, c, d, e, fff, g, h) = f(it)
    a toT Tuple7(b, c, d, e, fff, g, h)
  }

  fun <A, B, C, D, E, FF, G, H, I, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    f: (Z) -> Tuple9<A, B, C, D, E, FF, G, H, I>
  ): Kind<F, Z> = divide(
    fa,
    fb.product(fc).product(fd).product(fe).product(ff).product(fg).product(fh)
      .product(fi)
  ) {
    val (a, b, c, d, e, fff, g, h, i) = f(it)
    a toT Tuple8(b, c, d, e, fff, g, h, i)
  }

  fun <A, B, C, D, E, FF, G, H, I, J, Z> divide(
    fa: Kind<F, A>,
    fb: Kind<F, B>,
    fc: Kind<F, C>,
    fd: Kind<F, D>,
    fe: Kind<F, E>,
    ff: Kind<F, FF>,
    fg: Kind<F, G>,
    fh: Kind<F, H>,
    fi: Kind<F, I>,
    fj: Kind<F, J>,
    f: (Z) -> Tuple10<A, B, C, D, E, FF, G, H, I, J>
  ): Kind<F, Z> = divide(
    fa,
    fb.product(fc).product(fd).product(fe).product(ff).product(fg).product(fh)
      .product(fi).product(fj)
  ) {
    val (a, b, c, d, e, fff, g, h, i, j) = f(it)
    a toT Tuple9(b, c, d, e, fff, g, h, i, j)
  }
}
