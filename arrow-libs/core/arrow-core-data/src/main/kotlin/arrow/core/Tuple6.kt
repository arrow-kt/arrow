@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple6 private constructor() {
  companion object
}
typealias Tuple6Of<A, B, C, D, E, F> = arrow.Kind6<ForTuple6, A, B, C, D, E, F>
typealias Tuple6PartialOf<A, B, C, D, E> = arrow.Kind5<ForTuple6, A, B, C, D, E>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F> Tuple6Of<A, B, C, D, E, F>.fix(): Tuple6<A, B, C, D, E, F> =
  this as Tuple6<A, B, C, D, E, F>

data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) : Tuple6Of<A, B, C, D, E, F> {
  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f)"

  companion object
}

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  other: Tuple6<A, B, C, D, E, F>
): Boolean =
  EQA.run { a.eqv(other.a) } &&
    EQB.run { this@eqv.b.eqv(other.b) } &&
    EQC.run { c.eqv(other.c) } &&
    EQD.run { d.eqv(other.d) } &&
    EQE.run { e.eqv(other.e) } &&
    EQF.run { f.eqv(other.f) }

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  other: Tuple6<A, B, C, D, E, F>
): Boolean = !eqv(EQA, EQB, EQC, EQD, EQE, EQF, other)

private class Tuple6Eq<A, B, C, D, E, F>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>,
  private val EQC: Eq<C>,
  private val EQD: Eq<D>,
  private val EQE: Eq<E>,
  private val EQF: Eq<F>
) : Eq<Tuple6<A, B, C, D, E, F>> {
  override fun Tuple6<A, B, C, D, E, F>.eqv(other: Tuple6<A, B, C, D, E, F>): Boolean =
    eqv(EQA, EQB, EQC, EQD, EQE, EQF, other)
}

fun <A, B, C, D, E, F> Eq.Companion.tuple6(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>
): Eq<Tuple6<A, B, C, D, E, F>> =
  Tuple6Eq(EQA, EQB, EQC, EQD, EQE, EQF)

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            HF.run {
              a.hashWithSalt(
                b.hashWithSalt(
                  c.hashWithSalt(
                    d.hashWithSalt(
                      e.hashWithSalt(
                        f.hashWithSalt(salt)
                      )))))
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>
): Int = hashWithSalt(HA, HB, HC, HD, HE, HF, defaultSalt)

private class Tuple6Hash<A, B, C, D, E, F>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>,
  private val HF: Hash<F>
) : Hash<Tuple6<A, B, C, D, E, F>> {
  override fun Tuple6<A, B, C, D, E, F>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, HF, salt)
}

fun <A, B, C, D, E, F> Hash.Companion.tuple6(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>
): Hash<Tuple6<A, B, C, D, E, F>> =
  Tuple6Hash(HA, HB, HC, HD, HE, HF)

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  other: Tuple6<A, B, C, D, E, F>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) }
).fold(Monoid.ordering())

private class Tuple6Order<A, B, C, D, E, F>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>,
  private val OE: Order<E>,
  private val OF: Order<F>
) : Order<Tuple6<A, B, C, D, E, F>> {
  override fun Tuple6<A, B, C, D, E, F>.compare(other: Tuple6<A, B, C, D, E, F>): Ordering =
    compare(OA, OB, OC, OD, OE, OF, other)
}

fun <A, B, C, D, E, F> Order.Companion.tuple6(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>
): Order<Tuple6<A, B, C, D, E, F>> =
  Tuple6Order(OA, OB, OC, OD, OE, OF)
