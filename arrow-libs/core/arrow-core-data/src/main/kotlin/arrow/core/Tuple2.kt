@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.defaultSalt

class ForTuple2 private constructor() {
  companion object
}
typealias Tuple2Of<A, B> = arrow.Kind2<ForTuple2, A, B>
typealias Tuple2PartialOf<A> = arrow.Kind<ForTuple2, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B> Tuple2Of<A, B>.fix(): Tuple2<A, B> =
  this as Tuple2<A, B>

@Deprecated("Deprecated in favor of Kotlin's Pair", ReplaceWith("Pair(a, b)"))
data class Tuple2<out A, out B>(val a: A, val b: B) : Tuple2Of<A, B> {

  @Deprecated("Functor hierarchy for Tuple2 is deprecated", ReplaceWith("Tuple2(this.a, f(this.b))", "arrow.core.Tuple2"))
  fun <C> map(f: (B) -> C) =
    a toT f(b)

  @Deprecated("BiFunctor hierarchy for Tuple2 is deprecated", ReplaceWith("Tuple2(fl(this.a), fr(this.b))", "arrow.core.Tuple2"))
  fun <C, D> bimap(fl: (A) -> C, fr: (B) -> D) =
    fl(a) toT fr(b)

  @Deprecated("Apply hierarchy for Tuple2 is deprecated", ReplaceWith("Tuple2(this.a, f.b(this.b))", "arrow.core.Tuple2"))
  fun <C> ap(f: Tuple2Of<*, (B) -> C>) =
    map(f.fix().b)

  @Deprecated("Monad hierarchy for Tuple2 is deprecated", ReplaceWith("f(this.b)"))
  fun <C> flatMap(f: (B) -> Tuple2Of<@UnsafeVariance A, C>) =
    f(b).fix()

  @Deprecated("Comonad hierarchy for Tuple2 is deprecated", ReplaceWith("a toT f(this)", "arrow.core.toT"))
  fun <C> coflatMap(f: (Tuple2Of<A, B>) -> C) =
    a toT f(this)

  @Deprecated("Comonad hierarchy for Tuple2 is deprecated", ReplaceWith("this.b"))
  fun extract() =
    b

  @Deprecated("Foldable hierarchy for Tuple2 is deprecated", ReplaceWith("f(b, this.b)"))
  fun <C> foldL(b: C, f: (C, B) -> C) =
    f(b, this.b)

  @Deprecated("Foldable hierarchy for Tuple2 is deprecated", ReplaceWith("f(this.b, lb)"))
  fun <C> foldR(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>) =
    f(b, lb)

  fun reverse(): Tuple2<B, A> = Tuple2(b, a)

  fun show(SA: Show<A>, SB: Show<B>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any())

  companion object
}

fun <A, B> Pair<A, B>.show(SA: Show<A>, SB: Show<B>): String =
  "(${SA.run { first.show() }}, ${SB.run { second.show() }})"

private class PairShow<A, B>(
  private val SA: Show<A>,
  private val SB: Show<B>
) : Show<Pair<A, B>> {
  override fun Pair<A, B>.show(): String =
    show(SA, SB)
}

fun <A, B> Show.Companion.pair(
  SA: Show<A>,
  SB: Show<B>
): Show<Pair<A, B>> =
  PairShow(SA, SB)

fun <A, B> Pair<A, B>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  other: Pair<A, B>
): Boolean =
  EQA.run { first.eqv(other.first) } &&
    EQB.run { this@eqv.second.eqv(other.second) }

fun <A, B> Pair<A, B>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  other: Pair<A, B>
): Boolean = !eqv(EQA, EQB, other)

private class PairEq<A, B>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>
) : Eq<Pair<A, B>> {
  override fun Pair<A, B>.eqv(other: Pair<A, B>): Boolean =
    eqv(EQA, EQB, other)
}

fun <A, B> Eq.Companion.pair(
  EQA: Eq<A>,
  EQB: Eq<B>
): Eq<Pair<A, B>> =
  PairEq(EQA, EQB)

fun <A, B> Pair<A, B>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      first.hashWithSalt(
        second.hashWithSalt(salt))
    }
  }

fun <A, B> Pair<A, B>.hash(
  HA: Hash<A>,
  HB: Hash<B>
): Int = hashWithSalt(HA, HB, defaultSalt)

private class PairHash<A, B>(
  private val HA: Hash<A>,
  private val HB: Hash<B>
) : Hash<Pair<A, B>> {
  override fun Pair<A, B>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, salt)
}

fun <A, B> Hash.Companion.pair(
  HA: Hash<A>,
  HB: Hash<B>
): Hash<Pair<A, B>> =
  PairHash(HA, HB)

fun <A, B> Pair<A, B>.compare(
  OA: Order<A>,
  OB: Order<B>,
  other: Pair<A, B>
): Ordering = listOf(
  OA.run { first.compare(other.first) },
  OB.run { second.compare(other.second) }
).fold(Monoid.ordering())

private class PairOrder<A, B>(
  private val OA: Order<A>,
  private val OB: Order<B>
) : Order<Pair<A, B>> {
  override fun Pair<A, B>.compare(other: Pair<A, B>): Ordering =
    compare(OA, OB, other)
}

fun <A, B> Order.Companion.pair(
  OA: Order<A>,
  OB: Order<B>
): Order<Pair<A, B>> =
  PairOrder(OA, OB)

fun <A, B> Semigroup.Companion.pair(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Pair<A, B>> =
  PairSemigroup(SA, SB)

fun <A, B> Pair<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, b: Pair<A, B>): Pair<A, B> {
  val (xa, xb) = this
  val (ya, yb) = b
  return Pair(SA.run { xa.combine(ya) }, SB.run { xb.combine(yb) })
}

private open class PairSemigroup<A, B>(
  private val SA: Semigroup<A>,
  private val SB: Semigroup<B>
) : Semigroup<Pair<A, B>> {
  override fun Pair<A, B>.combine(b: Pair<A, B>): Pair<A, B> =
    combine(SA, SB, b)
}

fun <A, B> Monoid.Companion.pair(MA: Monoid<A>, MB: Monoid<B>): Monoid<Pair<A, B>> =
  PairMonoid(MA, MB)

private class PairMonoid<A, B>(
  private val MA: Monoid<A>,
  private val MB: Monoid<B>
) : Monoid<Pair<A, B>>, PairSemigroup<A, B>(MA, MB) {
  override fun empty(): Pair<A, B> =
    Pair(MA.empty(), MB.empty())
}
