@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Hash
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

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>, F : Comparable<F>, G : Comparable<G>, H : Comparable<H>, I : Comparable<I>, J : Comparable<J>>
  Tuple10<A, B, C, D, E, F, G, H, I, J>.compareTo(other: Tuple10<A, B, C, D, E, F, G, H, I, J>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) {
        val fourth = d.compareTo(other.d)
        if (fourth == 0) {
          val fifth = e.compareTo(other.e)
          if (fifth == 0) {
            val sixth = f.compareTo(other.f)
            if (sixth == 0) {
              val seventh = g.compareTo(other.g)
              if (seventh == 0) {
                val eigth = h.compareTo(other.h)
                if (eigth == 0) {
                  val ninth = i.compareTo(other.i)
                  if (ninth == 0) j.compareTo(other.j)
                  else ninth
                } else eigth
              } else seventh
            } else sixth
          } else fifth
        } else fourth
      } else third
    } else second
  } else first
}
