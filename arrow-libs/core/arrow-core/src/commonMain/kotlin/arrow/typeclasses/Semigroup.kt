package arrow.typeclasses

import arrow.core.Const
import arrow.core.ConstDeprecation
import arrow.core.Either
import arrow.core.Endo
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Validated
import arrow.core.combine
import arrow.core.compose
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

public const val SemigroupDeprecation: String =
  "Semigroup is being deprecated, use combine (A, A) -> A lambdas or method references instead."

@Deprecated(SemigroupDeprecation)
public fun interface Semigroup<A> {
  /**
   * Combine two [A] values.
   */
  public fun A.combine(b: A): A

  public fun append(a: A, b: A): A =
    a.combine(b)

  public operator fun A.plus(b: A): A =
    this.combine(b)

  public fun A.maybeCombine(b: A?): A =
    b?.let { combine(it) } ?: this

  public companion object {

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Lis<A>::plus directly instead.",
      ReplaceWith("List<A>::plus")
    )
    public fun <A> list(): Semigroup<List<A>> = Monoid.list()

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Sequence<A>::plus directly instead.",
      ReplaceWith("Sequence<A>::plus")
    )
    public fun <A> sequence(): Semigroup<Sequence<A>> = Monoid.sequence()

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use String::plus directly instead.",
      ReplaceWith("String::plus")
    )
    public fun string(): Semigroup<String> = Monoid.string()

    @JvmStatic
    @JvmName("Boolean")
    @Deprecated(
      "$SemigroupDeprecation. Use Boolean::and directly instead.",
      ReplaceWith("Boolean::and")
    )
    public fun boolean(): Semigroup<Boolean> = Monoid.boolean()

    @JvmStatic
    @JvmName("Byte")
    @Deprecated(
      "$SemigroupDeprecation. Use Int::plus and toByte directly instead.",
      ReplaceWith("{ a, b -> (a + b).toByte() }")
    )
    public fun byte(): Semigroup<Byte> = Monoid.byte()

    @JvmStatic
    @JvmName("Integer")
    @Deprecated(
      "$SemigroupDeprecation. Use Int::plus directly instead.",
      ReplaceWith("Int::plus")
    )
    public fun int(): Semigroup<Int> = Monoid.int()

    @JvmStatic
    @JvmName("Long")
    @Deprecated(
      "$SemigroupDeprecation. Use Long::plus directly instead.",
      ReplaceWith("Long::plus")
    )
    public fun long(): Semigroup<Long> = Monoid.long()

    @JvmStatic
    @JvmName("Short")
    @Deprecated(
      "$SemigroupDeprecation. Use Int::plus and toShort directly instead.",
      ReplaceWith("{ a, b -> (a + b).toShort() }")
    )
    public fun short(): Semigroup<Short> = Monoid.short()

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Either::combine directly instead.",
      ReplaceWith("{ a: Either<A, B>, b: Either<A, B> -> a.combine(b, SA::combine, SB::combine) }")
    )
    public fun <A, B> either(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Either<A, B>> =
      EitherSemigroup(SA, SB)

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Ior::combine directly instead.",
      ReplaceWith("{ a: Ior<A, B>, b: Ior<A, B> -> a.combine(b, SA::combine, SB::combine) }")
    )
    public fun <A, B> ior(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Ior<A, B>> =
      IorSemigroup(SA, SB)

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use arrow.core.compose directly instead.",
      ReplaceWith("{ f, g -> f.compose(g.f) }")
    )
    public fun <A> endo(): Semigroup<Endo<A>> =
      Semigroup { g -> Endo(f.compose(g.f)) }

    @JvmStatic
    @JvmName("constant")
    @Deprecated(ConstDeprecation)
    public fun <A, T> const(SA: Semigroup<A>): Semigroup<Const<A, T>> =
      Semigroup { b -> this.combine(SA, b) }

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Map::combine directly instead.",
      ReplaceWith(
        "{ a: Map<K, A>, b: Map<K, A> -> a.combine(b, SG::combine) }",
        "arrow.core.combine"
      )
    )
    public fun <K, A> map(SG: Semigroup<A>): Semigroup<Map<K, A>> =
      MapSemigroup(SG)

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Option::combine directly instead.",
      ReplaceWith(
        "{ a: Option<A>, b: Option<A> -> a.combine(b, SGA::combine) }",
        "arrow.core.combine"
      )
    )
    public fun <A> option(SGA: Semigroup<A>): Semigroup<Option<A>> =
      OptionSemigroup(SGA)

    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use Validated::combine directly instead.",
      ReplaceWith(
        "{ a: Validated<E, A>, b: Validated<E, A> -> a.combine(b, SE, SA) }",
        "arrow.core.combine"
      )
    )
    public fun <E, A> validated(SE: Semigroup<E>, SA: Semigroup<A>): Semigroup<Validated<E, A>> =
      ValidatedSemigroup(SE, SA)

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @Deprecated(
      "$SemigroupDeprecation. Use NonEmptyPlus::plus directly instead.",
      ReplaceWith("NonEmptyList::plus", "arrow.core.plus")
    )
    public fun <A> nonEmptyList(): Semigroup<NonEmptyList<A>> =
      NonEmptyListSemigroup as Semigroup<NonEmptyList<A>>

    @JvmStatic
    public fun <A, B> pair(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Pair<A, B>> =
      PairSemigroup(SA, SB)

    private open class PairSemigroup<A, B>(
      private val SA: Semigroup<A>,
      private val SB: Semigroup<B>
    ) : Semigroup<Pair<A, B>> {
      override fun Pair<A, B>.combine(b: Pair<A, B>): Pair<A, B> = combine(SA, SB, b)
    }

    public object NonEmptyListSemigroup : Semigroup<NonEmptyList<Any?>> {
      override fun NonEmptyList<Any?>.combine(b: NonEmptyList<Any?>): NonEmptyList<Any?> =
        NonEmptyList(this.head, this.tail.plus(b))
    }

    private open class ValidatedSemigroup<A, B>(
      private val SA: Semigroup<A>,
      private val SB: Semigroup<B>
    ) : Semigroup<Validated<A, B>> {
      override fun Validated<A, B>.combine(b: Validated<A, B>): Validated<A, B> =
        combine(SA, SB, b)
    }

    private class OptionSemigroup<A>(
      private val SGA: Semigroup<A>
    ) : Semigroup<Option<A>> {

      override fun Option<A>.combine(b: Option<A>): Option<A> =
        combine(SGA, b)

      override fun Option<A>.maybeCombine(b: Option<A>?): Option<A> =
        b?.let { combine(SGA, it) } ?: this
    }

    private class MapSemigroup<K, A>(private val SG: Semigroup<A>) : Semigroup<Map<K, A>> {
      override fun Map<K, A>.combine(b: Map<K, A>): Map<K, A> =
        combine(SG, b)
    }

    private open class EitherSemigroup<L, R>(
      private val SGL: Semigroup<L>,
      private val SGR: Semigroup<R>
    ) : Semigroup<Either<L, R>> {

      override fun Either<L, R>.combine(b: Either<L, R>): Either<L, R> =
        combine(SGL, SGR, b)

      override fun Either<L, R>.maybeCombine(b: Either<L, R>?): Either<L, R> =
        b?.let { combine(SGL, SGR, it) } ?: this
    }

    private class IorSemigroup<A, B>(
      private val SGA: Semigroup<A>,
      private val SGB: Semigroup<B>
    ) : Semigroup<Ior<A, B>> {

      override fun Ior<A, B>.combine(b: Ior<A, B>): Ior<A, B> =
        combine(SGA, SGB, b)

      override fun Ior<A, B>.maybeCombine(b: Ior<A, B>?): Ior<A, B> =
        b?.let { combine(SGA, SGB, it) } ?: this
    }
  }
}

@Deprecated(SemigroupDeprecation)
public fun <A> Semigroup<A>.combine(a: A, b: A): A =
  a.combine(b)
