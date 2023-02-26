package arrow.typeclasses

import arrow.core.Either
import arrow.core.Endo
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.combine
import arrow.core.compose
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

public fun interface Semigroup<A> {
  
  // TODO: think of better name
  public fun append(a: A, b: A): A
  
  /**
   * Combine two [A] values.
   */
  public fun A.combine(b: A): A =
    append(this, b)

  public operator fun A.plus(b: A): A =
    append(this, b)

  public fun A.maybeCombine(b: A?): A =
    b?.let { append(this, it) } ?: this

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
    @JvmName("Integer")
    public fun int(): Semigroup<Int> = Monoid.int()

    @JvmStatic
    @JvmName("Long")
    public fun long(): Semigroup<Long> = Monoid.long()

    @JvmStatic
    @JvmName("Short")
    public fun short(): Semigroup<Short> = Monoid.short()

    @JvmStatic
    public fun <A, B> either(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Either<A, B>> =
      EitherSemigroup(SA, SB)

    @JvmStatic
    public fun <A, B> ior(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Ior<A, B>> =
      IorSemigroup(SA, SB)

    @JvmStatic
    public fun <A> endo(): Semigroup<Endo<A>> =
      Semigroup { f, g -> Endo(f.f.compose(g.f)) }

    @JvmStatic
    public fun <K, A> map(SG: Semigroup<A>): Semigroup<Map<K, A>> =
      MapSemigroup(SG)

    @JvmStatic
    public fun <A> option(SGA: Semigroup<A>): Semigroup<Option<A>> =
      OptionSemigroup(SGA)

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
      override fun append(a: Pair<A, B>, b: Pair<A, B>): Pair<A, B> = a.combine(SA, SB, b)
    }

    public object NonEmptyListSemigroup : Semigroup<NonEmptyList<Any?>> {
      override fun append(a: NonEmptyList<Any?>, b: NonEmptyList<Any?>): NonEmptyList<Any?> =
        NonEmptyList(a.head, a.tail.plus(b))
    }

    @PublishedApi
    internal class OptionSemigroup<A>(
      private val SGA: Semigroup<A>
    ) : Semigroup<Option<A>> {

      override fun append(a: Option<A>, b: Option<A>): Option<A> =
        a.combine(SGA as Semigroup<Any?>, b) as Option<A>

      override fun Option<A>.maybeCombine(b: Option<A>?): Option<A> =
        b?.let { combine(SGA as Semigroup<Any?>, it) as Option<A> } ?: this
    }

    private class MapSemigroup<K, A>(private val SG: Semigroup<A>) : Semigroup<Map<K, A>> {
      override fun append(a: Map<K, A>, b: Map<K, A>): Map<K, A> =
        a.combine(SG, b)
    }

    private open class EitherSemigroup<L, R>(
      private val SGL: Semigroup<L>,
      private val SGR: Semigroup<R>
    ) : Semigroup<Either<L, R>> {

      override fun append(a: Either<L, R>, b: Either<L, R>): Either<L, R> =
        a.combine(SGL, SGR, b)

      override fun Either<L, R>.maybeCombine(b: Either<L, R>?): Either<L, R> =
        b?.let { combine(SGL, SGR, it) } ?: this
    }

    private class IorSemigroup<A, B>(
      private val SGA: Semigroup<A>,
      private val SGB: Semigroup<B>
    ) : Semigroup<Ior<A, B>> {

      override fun append(a: Ior<A, B>, b: Ior<A, B>): Ior<A, B> =
        a.combine(SGA, SGB, b)

      override fun Ior<A, B>.maybeCombine(b: Ior<A, B>?): Ior<A, B> =
        b?.let { combine(SGA, SGB, it) } ?: this
    }
  }
}
