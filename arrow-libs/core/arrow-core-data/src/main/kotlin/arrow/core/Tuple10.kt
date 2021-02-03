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

class ForTuple10 private constructor() {
  companion object
}
typealias Tuple10Of<A, B, C, D, E, F, G, H, I, J> = arrow.Kind10<ForTuple10, A, B, C, D, E, F, G, H, I, J>
typealias Tuple10PartialOf<A, B, C, D, E, F, G, H, I> = arrow.Kind9<ForTuple10, A, B, C, D, E, F, G, H, I>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I, J> Tuple10Of<A, B, C, D, E, F, G, H, I, J>.fix(): Tuple10<A, B, C, D, E, F, G, H, I, J> =
  this as Tuple10<A, B, C, D, E, F, G, H, I, J>

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I, val j: J) : Tuple10Of<A, B, C, D, E, F, G, H, I, J> {

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>, SH: Show<H>, SI: Show<I>, SJ: Show<J>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }, SH.run { h.show() }, SI.run { i.show() }, SJ.run { j.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f, $g, $h, $i, $j)"

  companion object
}

fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  EQJ: Eq<J>,
  other: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean =
  EQA.run { a.eqv(other.a) } &&
    EQB.run { this@eqv.b.eqv(other.b) } &&
    EQC.run { c.eqv(other.c) } &&
    EQD.run { d.eqv(other.d) } &&
    EQE.run { e.eqv(other.e) } &&
    EQF.run { f.eqv(other.f) } &&
    EQG.run { g.eqv(other.g) } &&
    EQH.run { h.eqv(other.h) } &&
    EQI.run { i.eqv(other.i) } &&
    EQJ.run { j.eqv(other.j) }

fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  EQJ: Eq<J>,
  other: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = !eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, EQJ, other)

private class Tuple10Eq<A, B, C, D, E, F, G, H, I, J>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>,
  private val EQC: Eq<C>,
  private val EQD: Eq<D>,
  private val EQE: Eq<E>,
  private val EQF: Eq<F>,
  private val EQG: Eq<G>,
  private val EQH: Eq<H>,
  private val EQI: Eq<I>,
  private val EQJ: Eq<J>,
) : Eq<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.eqv(other: Tuple10<A, B, C, D, E, F, G, H, I, J>): Boolean =
    eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, EQJ, other)
}

fun <A, B, C, D, E, F, G, H, I, J> Eq.Companion.tuple10(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  EQJ: Eq<J>
): Eq<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Tuple10Eq(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, EQJ)

fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
  HJ: Hash<J>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            HF.run {
              HG.run {
                HH.run {
                  HI.run {
                    HJ.run {
                      a.hashWithSalt(
                        b.hashWithSalt(
                          c.hashWithSalt(
                            d.hashWithSalt(
                              e.hashWithSalt(
                                f.hashWithSalt(
                                  g.hashWithSalt(
                                    h.hashWithSalt(
                                      i.hashWithSalt(
                                        j.hashWithSalt(salt)
                                      )))))))))
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

fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
  HJ: Hash<J>
): Int = hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, HI, HJ, defaultSalt)

private class Tuple10Hash<A, B, C, D, E, F, G, H, I, J>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>,
  private val HF: Hash<F>,
  private val HG: Hash<G>,
  private val HH: Hash<H>,
  private val HI: Hash<I>,
  private val HJ: Hash<J>
) : Hash<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, HI, HJ, salt)
}

fun <A, B, C, D, E, F, G, H, I, J> Hash.Companion.tuple10(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
  HJ: Hash<J>
): Hash<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Tuple10Hash(HA, HB, HC, HD, HE, HF, HG, HH, HI, HJ)

fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  other: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) },
  OG.run { g.compare(other.g) },
  OH.run { h.compare(other.h) },
  OI.run { i.compare(other.i) },
  OJ.run { j.compare(other.j) }
).fold(Monoid.ordering())

private class Tuple10Order<A, B, C, D, E, F, G, H, I, J>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>,
  private val OE: Order<E>,
  private val OF: Order<F>,
  private val OG: Order<G>,
  private val OH: Order<H>,
  private val OI: Order<I>,
  private val OJ: Order<J>,
) : Order<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.compare(other: Tuple10<A, B, C, D, E, F, G, H, I, J>): Ordering =
    compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, other)
}

fun <A, B, C, D, E, F, G, H, I, J> Order.Companion.tuple10(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>
): Order<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Tuple10Order(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ)
