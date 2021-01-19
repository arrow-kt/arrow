@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.defaultSalt

class ForTuple7 private constructor() {
  companion object
}
typealias Tuple7Of<A, B, C, D, E, F, G> = arrow.Kind7<ForTuple7, A, B, C, D, E, F, G>
typealias Tuple7PartialOf<A, B, C, D, E, F> = arrow.Kind6<ForTuple7, A, B, C, D, E, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G> Tuple7Of<A, B, C, D, E, F, G>.fix(): Tuple7<A, B, C, D, E, F, G> =
  this as Tuple7<A, B, C, D, E, F, G>

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G) : Tuple7Of<A, B, C, D, E, F, G> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any())

  companion object
}

private class Tuple7Show<A, B, C, D, E, F, G>(
  private val SA: Show<A>,
  private val SB: Show<B>,
  private val SC: Show<C>,
  private val SD: Show<D>,
  private val SE: Show<E>,
  private val SF: Show<F>,
  private val SG: Show<G>
) : Show<Tuple7<A, B, C, D, E, F, G>> {
  override fun Tuple7<A, B, C, D, E, F, G>.show(): String =
    show(SA, SB, SC, SD, SE, SF, SG)
}

fun <A, B, C, D, E, F, G> Show.Companion.tuple7(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>,
  SE: Show<E>,
  SF: Show<F>,
  SG: Show<G>
): Show<Tuple7<A, B, C, D, E, F, G>> =
  Tuple7Show(SA, SB, SC, SD, SE, SF, SG)

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  other: Tuple7<A, B, C, D, E, F, G>
): Boolean =
  EQA.run { a.eqv(other.a) } &&
    EQB.run { this@eqv.b.eqv(other.b) } &&
    EQC.run { c.eqv(other.c) } &&
    EQD.run { d.eqv(other.d) } &&
    EQE.run { e.eqv(other.e) } &&
    EQF.run { f.eqv(other.f) } &&
    EQG.run { g.eqv(other.g) }

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  other: Tuple7<A, B, C, D, E, F, G>
): Boolean = !eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, other)

private class Tuple7Eq<A, B, C, D, E, F, G>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>,
  private val EQC: Eq<C>,
  private val EQD: Eq<D>,
  private val EQE: Eq<E>,
  private val EQF: Eq<F>,
  private val EQG: Eq<G>
) : Eq<Tuple7<A, B, C, D, E, F, G>> {
  override fun Tuple7<A, B, C, D, E, F, G>.eqv(other: Tuple7<A, B, C, D, E, F, G>): Boolean =
    eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, other)
}

fun <A, B, C, D, E, F, G> Eq.Companion.tuple7(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>
): Eq<Tuple7<A, B, C, D, E, F, G>> =
  Tuple7Eq(EQA, EQB, EQC, EQD, EQE, EQF, EQG)

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            HF.run {
              HG.run {
                a.hashWithSalt(
                  b.hashWithSalt(
                    c.hashWithSalt(
                      d.hashWithSalt(
                        e.hashWithSalt(
                          f.hashWithSalt(
                            g.hashWithSalt(salt)
                          ))))))
              }
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  salt: Int
): Int = hashWithSalt(HA, HB, HC, HD, HE, HF, HG, defaultSalt)

private class Tuple7Hash<A, B, C, D, E, F, G>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>,
  private val HF: Hash<F>,
  private val HG: Hash<G>
) : Hash<Tuple7<A, B, C, D, E, F, G>> {
  override fun Tuple7<A, B, C, D, E, F, G>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, HF, HG, salt)
}

fun <A, B, C, D, E, F, G> Hash.Companion.tuple7(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>
): Hash<Tuple7<A, B, C, D, E, F, G>> =
  Tuple7Hash(HA, HB, HC, HD, HE, HF, HG)

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  other: Tuple7<A, B, C, D, E, F, G>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) },
  OG.run { g.compare(other.g) }
).fold(Monoid.ordering())

private class Tuple7Order<A, B, C, D, E, F, G>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>,
  private val OE: Order<E>,
  private val OF: Order<F>,
  private val OG: Order<G>
) : Order<Tuple7<A, B, C, D, E, F, G>> {
  override fun Tuple7<A, B, C, D, E, F, G>.compare(other: Tuple7<A, B, C, D, E, F, G>): Ordering =
    compare(OA, OB, OC, OD, OE, OF, OG, other)
}

fun <A, B, C, D, E, F, G> Order.Companion.tuple7(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>
): Order<Tuple7<A, B, C, D, E, F, G>> =
  Tuple7Order(OA, OB, OC, OD, OE, OF, OG)
