@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple7 private constructor() {
  companion object
}
typealias Tuple7Of<A, B, C, D, E, F, G> = arrow.Kind7<ForTuple7, A, B, C, D, E, F, G>
typealias Tuple7PartialOf<A, B, C, D, E, F> = arrow.Kind6<ForTuple7, A, B, C, D, E, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G> Tuple7Of<A, B, C, D, E, F, G>.fix(): Tuple7<A, B, C, D, E, F, G> =
  this as Tuple7<A, B, C, D, E, F, G>

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F,
  @Deprecated("Use seventh instead", ReplaceWith("seventh"))
  val g: G
) : Tuple7Of<A, B, C, D, E, F, G> {

  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f
  val seventh: G = g

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f, $g)"

  companion object
}

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

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>, F : Comparable<F>, G : Comparable<G>>
  Tuple7<A, B, C, D, E, F, G>.compareTo(other: Tuple7<A, B, C, D, E, F, G>): Int {
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
            if (sixth == 0) g.compareTo(other.g)
            else sixth
          } else fifth
        } else fourth
      } else third
    } else second
  } else first
}
