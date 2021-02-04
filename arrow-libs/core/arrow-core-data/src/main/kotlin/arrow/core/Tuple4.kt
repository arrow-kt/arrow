@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple4 private constructor() {
  companion object
}
typealias Tuple4Of<A, B, C, D> = arrow.Kind4<ForTuple4, A, B, C, D>
typealias Tuple4PartialOf<A, B, C> = arrow.Kind3<ForTuple4, A, B, C>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D> Tuple4Of<A, B, C, D>.fix(): Tuple4<A, B, C, D> =
  this as Tuple4<A, B, C, D>

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) : Tuple4Of<A, B, C, D> {

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d)"

  companion object
}

fun <A, B, C, D> Tuple4<A, B, C, D>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          a.hashWithSalt(
            b.hashWithSalt(
              c.hashWithSalt(
                d.hashWithSalt(salt)
              )))
        }
      }
    }
  }

fun <A, B, C, D> Tuple4<A, B, C, D>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>
): Int = hashWithSalt(HA, HB, HC, HD, defaultSalt)

private class Tuple4Hash<A, B, C, D>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>
) : Hash<Tuple4<A, B, C, D>> {
  override fun Tuple4<A, B, C, D>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, salt)
}

fun <A, B, C, D> Hash.Companion.tuple4(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>
): Hash<Tuple4<A, B, C, D>> =
  Tuple4Hash(HA, HB, HC, HD)

fun <A, B, C, D> Tuple4<A, B, C, D>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  other: Tuple4<A, B, C, D>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) }
).fold(Monoid.ordering())

private class Tuple4Order<A, B, C, D>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>
) : Order<Tuple4<A, B, C, D>> {
  override fun Tuple4<A, B, C, D>.compare(other: Tuple4<A, B, C, D>): Ordering =
    compare(OA, OB, OC, OD, other)
}

fun <A, B, C, D> Order.Companion.tuple4(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>
): Order<Tuple4<A, B, C, D>> =
  Tuple4Order(OA, OB, OC, OD)
