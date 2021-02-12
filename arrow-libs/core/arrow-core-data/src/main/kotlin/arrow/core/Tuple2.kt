@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.KindDeprecation
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForTuple2 private constructor() {
  companion object
}
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple2Of<A, B> = arrow.Kind2<ForTuple2, A, B>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple2PartialOf<A> = arrow.Kind<ForTuple2, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
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

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b)"

  companion object
}

operator fun <A : Comparable<A>, B : Comparable<B>> Pair<A, B>.compareTo(other: Pair<A, B>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) second.compareTo(other.second)
  else first
}

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
