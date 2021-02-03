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

class ForTuple9 private constructor() {
  companion object
}
typealias Tuple9Of<A, B, C, D, E, F, G, H, I> = arrow.Kind9<ForTuple9, A, B, C, D, E, F, G, H, I>
typealias Tuple9PartialOf<A, B, C, D, E, F, G, H> = arrow.Kind8<ForTuple9, A, B, C, D, E, F, G, H>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I> Tuple9Of<A, B, C, D, E, F, G, H, I>.fix(): Tuple9<A, B, C, D, E, F, G, H, I> =
  this as Tuple9<A, B, C, D, E, F, G, H, I>

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I) : Tuple9Of<A, B, C, D, E, F, G, H, I> {
  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>, SH: Show<H>, SI: Show<I>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }, SH.run { h.show() }, SI.run { i.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f, $g, $h, $i)"

  companion object
}

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  other: Tuple9<A, B, C, D, E, F, G, H, I>
): Boolean =
  EQA.run { a.eqv(other.a) } &&
    EQB.run { this@eqv.b.eqv(other.b) } &&
    EQC.run { c.eqv(other.c) } &&
    EQD.run { d.eqv(other.d) } &&
    EQE.run { e.eqv(other.e) } &&
    EQF.run { f.eqv(other.f) } &&
    EQG.run { g.eqv(other.g) } &&
    EQH.run { h.eqv(other.h) } &&
    EQI.run { i.eqv(other.i) }

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  other: Tuple9<A, B, C, D, E, F, G, H, I>
): Boolean = !eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, other)

private class Tuple9Eq<A, B, C, D, E, F, G, H, I>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>,
  private val EQC: Eq<C>,
  private val EQD: Eq<D>,
  private val EQE: Eq<E>,
  private val EQF: Eq<F>,
  private val EQG: Eq<G>,
  private val EQH: Eq<H>,
  private val EQI: Eq<I>
) : Eq<Tuple9<A, B, C, D, E, F, G, H, I>> {
  override fun Tuple9<A, B, C, D, E, F, G, H, I>.eqv(other: Tuple9<A, B, C, D, E, F, G, H, I>): Boolean =
    eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI, other)
}

fun <A, B, C, D, E, F, G, H, I> Eq.Companion.tuple9(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>
): Eq<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Tuple9Eq(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, EQI)

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
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
                    a.hashWithSalt(
                      b.hashWithSalt(
                        c.hashWithSalt(
                          d.hashWithSalt(
                            e.hashWithSalt(
                              f.hashWithSalt(
                                g.hashWithSalt(
                                  h.hashWithSalt(
                                    i.hashWithSalt(salt)
                                  ))))))))
                  }
                }
              }
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>
): Int = hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, HI, defaultSalt)

private class Tuple9Hash<A, B, C, D, E, F, G, H, I>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>,
  private val HF: Hash<F>,
  private val HG: Hash<G>,
  private val HH: Hash<H>,
  private val HI: Hash<I>
) : Hash<Tuple9<A, B, C, D, E, F, G, H, I>> {
  override fun Tuple9<A, B, C, D, E, F, G, H, I>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, HI, salt)
}

fun <A, B, C, D, E, F, G, H, I> Hash.Companion.tuple9(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>
): Hash<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Tuple9Hash(HA, HB, HC, HD, HE, HF, HG, HH, HI)

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  other: Tuple9<A, B, C, D, E, F, G, H, I>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) },
  OG.run { g.compare(other.g) },
  OH.run { h.compare(other.h) },
  OI.run { i.compare(other.i) }
).fold(Monoid.ordering())

private class Tuple9Order<A, B, C, D, E, F, G, H, I>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>,
  private val OE: Order<E>,
  private val OF: Order<F>,
  private val OG: Order<G>,
  private val OH: Order<H>,
  private val OI: Order<I>
) : Order<Tuple9<A, B, C, D, E, F, G, H, I>> {
  override fun Tuple9<A, B, C, D, E, F, G, H, I>.compare(other: Tuple9<A, B, C, D, E, F, G, H, I>): Ordering =
    compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, other)
}

fun <A, B, C, D, E, F, G, H, I> Order.Companion.tuple9(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
): Order<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Tuple9Order(OA, OB, OC, OD, OE, OF, OG, OH, OI)
