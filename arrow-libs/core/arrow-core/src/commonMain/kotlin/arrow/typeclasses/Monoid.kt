package arrow.typeclasses

import arrow.core.Either
import arrow.core.Endo
import arrow.core.None
import arrow.core.Option
import arrow.core.combine
import arrow.core.compose
import arrow.core.flatten
import arrow.core.fold
import arrow.core.identity
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.collections.plus as _plus

public interface Monoid<A> : Semigroup<A> {
  /**
   * A zero value for this A
   */
  public fun empty(): A

  /**
   * Combine an [Collection] of [A] values.
   */
  @Deprecated("use fold instead", ReplaceWith("fold()"))
  public fun Collection<A>.combineAll(): A =
    fold()

  /**
   * Combine an array of [A] values.
   */
  @Deprecated("use fold instead", ReplaceWith("fold(elems)"))
  public fun combineAll(elems: List<A>): A = fold(elems)

  /**
   * Fold an [Collection] of [A] values.
   */
  public fun Collection<A>.fold(): A =
    if (isEmpty()) empty() else reduce { a, b -> a.combine(b) }

  /**
   * Fold an array of [A] values.
   */
  public fun fold(elems: List<A>): A = elems.fold()

  public companion object {
    @JvmStatic
    @JvmName("Boolean")
    public fun boolean(): Monoid<Boolean> = AndMonoid

    @JvmStatic
    @JvmName("Byte")
    public fun byte(): Monoid<Byte> = ByteMonoid

    @JvmStatic
    @JvmName("Integer")
    public fun int(): Monoid<Int> = IntMonoid

    @JvmStatic
    @JvmName("Long")
    public fun long(): Monoid<Long> = LongMonoid

    @JvmStatic
    @JvmName("Short")
    public fun short(): Monoid<Short> = ShortMonoid

    @JvmStatic
    public fun <A> list(): Monoid<List<A>> = ListMonoid as Monoid<List<A>>

    @JvmStatic
    public fun <A> sequence(): Monoid<Sequence<A>> = SequenceMonoid as Monoid<Sequence<A>>

    @JvmStatic
    public fun string(): Monoid<String> = StringMonoid

    @JvmStatic
    public fun <A, B> either(SGA: Semigroup<A>, MB: Monoid<B>): Monoid<Either<A, B>> =
      EitherMonoid(SGA, MB)

    @Deprecated("For binary compat", level = DeprecationLevel.HIDDEN)
    public fun <A, B> either(MA: Monoid<A>, MB: Monoid<B>): Monoid<Either<A, B>> =
      EitherMonoid(MA, MB)

    @JvmStatic
    public fun <A> endo(): Monoid<Endo<A>> =
      object : Monoid<Endo<A>> {
        override fun empty(): Endo<A> = Endo(::identity)
        override fun append(a: Endo<A>, b: Endo<A>): Endo<A> = Endo(a.f.compose(b.f))
      }

    @JvmStatic
    public fun <K, A> map(SG: Semigroup<A>): Monoid<Map<K, A>> =
      MapMonoid(SG)

    @JvmStatic
    public fun <A> option(MA: Semigroup<A>): Monoid<Option<A>> =
      OptionMonoid(MA)

    @JvmStatic
    public fun <A, B> pair(MA: Monoid<A>, MB: Monoid<B>): Monoid<Pair<A, B>> =
      PairMonoid(MA, MB)

    @PublishedApi
    internal class OptionMonoid<A>(
      private val MA: Semigroup<A>
    ) : Monoid<Option<A>> {
  
      override fun append(a: Option<A>, b: Option<A>): Option<A> =
        a.combine(MA as Semigroup<Any?>, b) as Option<A>
      
      override fun Option<A>.maybeCombine(b: Option<A>?): Option<A> =
        b?.let { combine(MA as Semigroup<Any?>, it) as Option<A> } ?: this

      override fun empty(): Option<A> = None
    }

    private class MapMonoid<K, A>(private val SG: Semigroup<A>) : Monoid<Map<K, A>> {
      override fun empty(): Map<K, A> = emptyMap()
  
      override fun append(a: Map<K, A>, b: Map<K, A>): Map<K, A> =
        a.combine(SG, b)
    }

    private object AndMonoid : Monoid<Boolean> {
      override fun append(a: Boolean, b: Boolean): Boolean = a && b
      override fun empty(): Boolean = true
    }

    private object ByteMonoid : Monoid<Byte> {
      override fun empty(): Byte = 0
      override fun append(a: Byte, b: Byte): Byte = (a + b).toByte()
    }

    private object DoubleMonoid : Monoid<Double> {
      override fun empty(): Double = .0
      override fun append(a: Double, b: Double): Double = a + b
    }

    private object IntMonoid : Monoid<Int> {
      override fun empty(): Int = 0
      override fun append(a: Int, b: Int): Int = a + b
    }

    private object LongMonoid : Monoid<Long> {
      override fun empty(): Long = 0L
      override fun append(a: Long, b: Long): Long = a + b
    }

    private object ShortMonoid : Monoid<Short> {
      override fun empty(): Short = 0
      override fun append(a: Short, b: Short): Short = (a + b).toShort()
    }

    private object FloatMonoid : Monoid<Float> {
      override fun empty(): Float = 0f
      override fun append(a: Float, b: Float): Float = a + b
    }

    private object StringMonoid : Monoid<String> {
      override fun append(a: String, b: String): String = "${a}$b"
      override fun empty(): String = ""
    }

    private object ListMonoid : Monoid<List<Any?>> {
      override fun empty(): List<Any?> = emptyList()
      override fun append(a: List<Any?>, b: List<Any?>): List<Any?> = a._plus(b)
    }

    private object SequenceMonoid : Monoid<Sequence<Any?>> {
      override fun empty(): Sequence<Any?> = emptySequence()
      override fun append(a: Sequence<Any?>, b: Sequence<Any?>): Sequence<Any?> = sequenceOf(a, b).flatten()
    }

    private class EitherMonoid<L, R>(
      private val SGOL: Semigroup<L>,
      private val MOR: Monoid<R>
    ) : Monoid<Either<L, R>> {
      override fun empty(): Either<L, R> = Either.Right(MOR.empty())
  
      override fun append(a: Either<L, R>, b: Either<L, R>): Either<L, R> =
        a.combine(SGOL, MOR, b)

      override fun Collection<Either<L, R>>.fold(): Either<L, R> =
        fold(either(SGOL, MOR))

      override fun fold(elems: List<Either<L, R>>): Either<L, R> =
        elems.fold(either(SGOL, MOR))

      override fun Either<L, R>.maybeCombine(b: Either<L, R>?): Either<L, R> =
        b?.let { combine(SGOL, MOR, it) } ?: this
    }

    private class PairMonoid<A, B>(
      private val MA: Monoid<A>,
      private val MB: Monoid<B>
    ) : Monoid<Pair<A, B>> {
      override fun empty(): Pair<A, B> = Pair(MA.empty(), MB.empty())
      override fun append(a: Pair<A, B>, b: Pair<A, B>): Pair<A, B> =
        a.combine(MA, MB, b)
    }
  }
}
