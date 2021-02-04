@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple5 private constructor() {
  companion object
}
typealias Tuple5Of<A, B, C, D, E> = arrow.Kind5<ForTuple5, A, B, C, D, E>
typealias Tuple5PartialOf<A, B, C, D> = arrow.Kind4<ForTuple5, A, B, C, D>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E> Tuple5Of<A, B, C, D, E>.fix(): Tuple5<A, B, C, D, E> =
  this as Tuple5<A, B, C, D, E>

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) : Tuple5Of<A, B, C, D, E> {

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e)"

  companion object
}

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            a.hashWithSalt(
              b.hashWithSalt(
                c.hashWithSalt(
                  d.hashWithSalt(
                    e.hashWithSalt(salt)
                  ))))
          }
        }
      }
    }
  }

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>
): Int = hashWithSalt(HA, HB, HC, HD, HE, defaultSalt)

private class Tuple5Hash<A, B, C, D, E>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>
) : Hash<Tuple5<A, B, C, D, E>> {
  override fun Tuple5<A, B, C, D, E>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, salt)
}

fun <A, B, C, D, E> Hash.Companion.tuple5(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>
): Hash<Tuple5<A, B, C, D, E>> =
  Tuple5Hash(HA, HB, HC, HD, HE)

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  other: Tuple5<A, B, C, D, E>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) }
).fold(Monoid.ordering())

private class Tuple5Order<A, B, C, D, E>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>,
  private val OE: Order<E>
) : Order<Tuple5<A, B, C, D, E>> {
  override fun Tuple5<A, B, C, D, E>.compare(other: Tuple5<A, B, C, D, E>): Ordering =
    compare(OA, OB, OC, OD, OE, other)
}

fun <A, B, C, D, E> Order.Companion.tuple5(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>
): Order<Tuple5<A, B, C, D, E>> =
  Tuple5Order(OA, OB, OC, OD, OE)
