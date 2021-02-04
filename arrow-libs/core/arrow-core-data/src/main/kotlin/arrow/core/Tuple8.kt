@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple8 private constructor() {
  companion object
}
typealias Tuple8Of<A, B, C, D, E, F, G, H> = arrow.Kind8<ForTuple8, A, B, C, D, E, F, G, H>
typealias Tuple8PartialOf<A, B, C, D, E, F, G> = arrow.Kind7<ForTuple8, A, B, C, D, E, F, G>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H> Tuple8Of<A, B, C, D, E, F, G, H>.fix(): Tuple8<A, B, C, D, E, F, G, H> =
  this as Tuple8<A, B, C, D, E, F, G, H>

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H) : Tuple8Of<A, B, C, D, E, F, G, H> {
  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>, SH: Show<H>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }, SH.run { h.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f, $g, $h)"

  companion object
}

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
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
                  a.hashWithSalt(
                    b.hashWithSalt(
                      c.hashWithSalt(
                        d.hashWithSalt(
                          e.hashWithSalt(
                            f.hashWithSalt(
                              g.hashWithSalt(
                                h.hashWithSalt(salt)
                              )))))))
                }
              }
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>
): Int = hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, defaultSalt)

private class Tuple8Hash<A, B, C, D, E, F, G, H>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>,
  private val HF: Hash<F>,
  private val HG: Hash<G>,
  private val HH: Hash<H>
) : Hash<Tuple8<A, B, C, D, E, F, G, H>> {
  override fun Tuple8<A, B, C, D, E, F, G, H>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, salt)
}

fun <A, B, C, D, E, F, G, H> Hash.Companion.tuple8(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>
): Hash<Tuple8<A, B, C, D, E, F, G, H>> =
  Tuple8Hash(HA, HB, HC, HD, HE, HF, HG, HH)

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  other: Tuple8<A, B, C, D, E, F, G, H>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) },
  OG.run { g.compare(other.g) },
  OH.run { h.compare(other.h) }
).fold(Monoid.ordering())

private class Tuple8Order<A, B, C, D, E, F, G, H>(
  private val OA: Order<A>,
  private val OB: Order<B>,
  private val OC: Order<C>,
  private val OD: Order<D>,
  private val OE: Order<E>,
  private val OF: Order<F>,
  private val OG: Order<G>,
  private val OH: Order<H>
) : Order<Tuple8<A, B, C, D, E, F, G, H>> {
  override fun Tuple8<A, B, C, D, E, F, G, H>.compare(other: Tuple8<A, B, C, D, E, F, G, H>): Ordering =
    compare(OA, OB, OC, OD, OE, OF, OG, OH, other)
}

fun <A, B, C, D, E, F, G, H> Order.Companion.tuple8(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>
): Order<Tuple8<A, B, C, D, E, F, G, H>> =
  Tuple8Order(OA, OB, OC, OD, OE, OF, OG, OH)
