package arrow.typeclasses

import arrow.core.Const
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

public interface Semigroup<A> {
  /**
   * Combine two [A] values.
   */
  public fun A.combine(b: A): A

  public operator fun A.plus(b: A): A =
    this.combine(b)

  public fun A.maybeCombine(b: A?): A =
    b?.let { combine(it) } ?: this

  public companion object {
    @JvmStatic
    public fun <A> list(): Semigroup<List<A>> = Monoid.list()

    @JvmStatic
    public fun <A> sequence(): Semigroup<Sequence<A>> = Monoid.sequence()

    @JvmStatic
    public fun string(): Semigroup<String> = Monoid.string()

    @JvmStatic
    @JvmName("Boolean")
    public fun boolean(): Semigroup<Boolean> = Monoid.boolean()

    @JvmStatic
    @JvmName("Byte")
    public fun byte(): Semigroup<Byte> = Monoid.byte()

    @JvmStatic
    @JvmName("Double")
    @Deprecated(DoubleInstanceDeprecation)
    public fun double(): Semigroup<Double> = Monoid.double()

    @JvmStatic
    @JvmName("Integer")
    public fun int(): Semigroup<Int> = Monoid.int()

    @JvmStatic
    @JvmName("Long")
    public fun long(): Semigroup<Long> = Monoid.long()

    @JvmStatic
    @JvmName("Short")
    public fun short(): Semigroup<Short> = Monoid.short()

    @JvmStatic
    @JvmName("Float")
    @Deprecated(FloatInstanceDeprecation)
    public fun float(): Semigroup<Float> = Monoid.float()

    @JvmStatic
    public fun <A, B> either(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Either<A, B>> =
      EitherSemigroup(SA, SB)

    @JvmStatic
    public fun <A, B> ior(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Ior<A, B>> =
      IorSemigroup(SA, SB)

    @JvmStatic
    public fun <A> endo(): Semigroup<Endo<A>> =
      object : Semigroup<Endo<A>> {
        override fun Endo<A>.combine(g: Endo<A>): Endo<A> = Endo(f.compose(g.f))
      }

    @JvmStatic
    @JvmName("constant")
    public fun <A, T> const(SA: Semigroup<A>): Semigroup<Const<A, T>> =
      object : Semigroup<Const<A, T>> {
        override fun Const<A, T>.combine(b: Const<A, T>): Const<A, T> =
          this.combine(SA, b)
      }

    @JvmStatic
    public fun <K, A> map(SG: Semigroup<A>): Semigroup<Map<K, A>> =
      MapSemigroup(SG)

    @JvmStatic
    public fun <A> option(SGA: Semigroup<A>): Semigroup<Option<A>> =
      OptionSemigroup(SGA)

    @JvmStatic
    public fun <E, A> validated(SE: Semigroup<E>, SA: Semigroup<A>): Semigroup<Validated<E, A>> =
      ValidatedSemigroup(SE, SA)

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
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
