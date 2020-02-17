package arrow.core

import arrow.higherkind

operator fun <A, B> AndThenOf<A, B>.invoke(a: A): B = fix().invoke(a)

/**
 * [AndThen] wraps a function of shape `(A) -> B` and can be used to do function composition.
 * It's similar to [arrow.core.andThen] and [arrow.core.compose] and can be used to build stack safe
 * data structures that make use of lambdas. Usage is typically used for signature such as `A -> Kind<F, A>` where
 * `F` has a [arrow.typeclasses.Monad] instance i.e. [StateT.flatMap].
 *
 * As you can see the usage of [AndThen] is the same as `[arrow.core.andThen] except we start our computation by
 * wrapping our function in [AndThen].
 *
 * ```kotlin:ank:playground
 * import arrow.core.andThen
 * import arrow.core.AndThen
 * import arrow.core.extensions.list.foldable.foldLeft
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val f = (0..10000).toList()
 *     .fold({ x: Int -> x + 1 }) { acc, _ ->
 *       acc.andThen { it + 1 }
 *     }
 *
 *   val f2 = (0..10000).toList()
 *     .foldLeft(AndThen { x: Int -> x + 1 }) { acc, _ ->
 *       acc.andThen { it + 1 }
 *     }
 *   //sampleEnd
 *   println("f(0) = ${f(0)}, f2(0) = ${f2(0)}")
 * }
 * ```
 *
 */
@higherkind
sealed class AndThen<A, B> : (A) -> B, AndThenOf<A, B> {

  private data class Single<A, B>(val f: (A) -> B, val index: Int) : AndThen<A, B>()

  private data class Join<A, B>(val fa: AndThen<A, AndThen<A, B>>) : AndThen<A, B>() {
    override fun toString(): String = "AndThen.Join(...)"
  }

  private data class Concat<A, E, B>(val left: AndThen<A, E>, val right: AndThen<E, B>) : AndThen<A, B>() {
    override fun toString(): String = "AndThen.Concat(...)"
  }

  /**
   * Compose a function to be invoked after the current function is invoked.
   *
   * ```kotlin:ank:playground
   * import arrow.core.AndThen
   * import arrow.core.extensions.list.foldable.foldLeft
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val f = (0..10000).toList().foldLeft(AndThen { i: Int -> i + 1 }) { acc, _ ->
   *     acc.andThen { it + 1 }
   *   }
   *
   *   val result = f(0)
   *   //sampleEnd
   *   println("result = $result")
   * }
   * ```
   *
   * @param g function to apply on the result of this function.
   * @return a composed [AndThen] function that first applies this function to its input,
   * and then applies [g] to the result.
   */
  fun <X> andThen(g: (B) -> X): AndThen<A, X> =
    when (this) {
      // Fusing calls up to a certain threshold, using the fusion technique implemented for `IO#map`
      is Single -> if (index != maxStackDepthSize) Single(f andThen g, index + 1)
      else andThenF(AndThen(g))
      else -> andThenF(AndThen(g))
    }

  /**
   * Compose a function to be invoked before the current function is invoked.
   *
   * ```kotlin:ank:playground
   * import arrow.core.AndThen
   * import arrow.core.extensions.list.foldable.foldLeft
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val f = (0..10000).toList().foldLeft(AndThen { i: Int -> i + 1 }) { acc, _ ->
   *     acc.compose { it + 1 }
   *   }
   *
   *   val result = f(0)
   *   //sampleEnd
   *   println("result = $result")
   * }
   * ```
   *
   * @param g function to invoke before invoking this function with the result.
   * @return a composed [AndThen] function that first applies [g] to its input,
   * and then applies this function to the result.
   */
  infix fun <C> compose(g: (C) -> A): AndThen<C, B> =
    when (this) {
      // Fusing calls up to a certain threshold, using the fusion technique implemented for `IO#map`
      is Single -> if (index != maxStackDepthSize) Single(f compose g, index + 1)
      else composeF(AndThen(g))
      else -> composeF(AndThen(g))
    }

  /**
   * Alias for [andThen]
   *
   * @see andThen
   */
  fun <C> map(f: (B) -> C): AndThen<A, C> =
    andThen(f)

  /**
   * Alias for [compose]
   *
   * @see compose
   */
  fun <C> contramap(f: (C) -> A): AndThen<C, B> =
    this compose f

  fun <C> flatMap(f: (B) -> AndThenOf<A, C>): AndThen<A, C> = Join(this.map(AndThen(f).andThen { it.fix() }))

  fun <C> ap(ff: AndThenOf<A, (B) -> C>): AndThen<A, C> =
    ff.fix().flatMap { f ->
      map(f)
    }

  /**
   * Invoke the `[AndThen]` function
   *
   * ```kotlin:ank:playground
   * import arrow.core.AndThen
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val f: AndThen<Int, String> = AndThen(Int::toString)
   *   val result = f.invoke(0)
   *   //sampleEnd
   *   println("result = $result")
   * }
   * ```
   *
   * @param a value to invoke function with
   * @return result of type [B].
   *
   **/
  @Suppress("UNCHECKED_CAST")
  override fun invoke(a: A): B = loop(this as AndThen<Any?, Any?>, a, 0)

  override fun toString(): String = "AndThen(...)"

  companion object {

    fun <A, B> just(b: B): AndThen<A, B> =
      AndThen { b }

    fun <A> id(): AndThen<A, A> =
      AndThen(::identity)

    /**
     * Wraps a function in [AndThen].
     *
     * ```kotlin:ank:playground
     * import arrow.core.AndThen
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = AndThen { x: Int -> x + 1 }
     *   val result = f(0)
     *   //sampleEnd
     *   println("result = $result")
     * }
     * ```
     *
     * @param f the function to wrap
     * @return wrapped function [f].
     *
     */
    operator fun <A, B> invoke(f: (A) -> B): AndThen<A, B> = when (f) {
      is AndThen<A, B> -> f
      else -> Single(f, 0)
    }

    fun <I, A, B> tailRecM(a: A, f: (A) -> AndThenOf<I, Either<A, B>>): AndThen<I, B> =
      AndThen { t: I -> step(a, t, f) }

    private tailrec fun <I, A, B> step(a: A, t: I, fn: (A) -> AndThenOf<I, Either<A, B>>): B {
      val af = fn(a)(t)
      return when (af) {
        is Either.Right -> af.b
        is Either.Left -> step(af.a, t, fn)
      }
    }

    /**
     * Establishes the maximum stack depth when fusing `andThen` or `compose` calls.
     *
     * The default is `128`, from which we substract one as an
     * optimization. This default has been reached like this:
     *
     *  - according to official docs, the default stack size on 32-bits
     *    Windows and Linux was 320 KB, whereas for 64-bits it is 1024 KB
     *  - according to measurements chaining `Function1` references uses
     *    approximately 32 bytes of stack space on a 64 bits system;
     *    this could be lower if "compressed oops" is activated
     *  - therefore a "map fusion" that goes 128 in stack depth can use
     *    about 4 KB of stack space
     */
    private const val maxStackDepthSize = 127
  }

  fun <X> andThenF(right: AndThen<B, X>): AndThen<A, X> = Concat(this, right)
  fun <X> composeF(right: AndThen<X, A>): AndThen<X, B> = Concat(right, this)

  @Suppress("UNCHECKED_CAST")
  private tailrec fun loop(self: AndThen<Any?, Any?>, current: Any?, joins: Int): B = when (self) {
    is Single -> if (joins == 0) self.f(current) as B else loop(self.f(current) as AndThen<Any?, Any?>, null, joins - 1)
    is Join -> loop(
      self.fa.andThen { Concat(AndThen<Any?, Any?> { current }, it) },
      current,
      joins + 1
    )
    is Concat<*, *, *> -> {
      when (val oldLeft = self.left) {
        is Single<*, *> -> {
          val left = oldLeft as Single<Any?, Any?>
          val newSelf = self.right as AndThen<Any?, Any?>
          loop(newSelf, left.f(current), joins)
        }
        is Join<*, *>,
        is Concat<*, *, *> -> loop(
          rotateAccumulate(self.left as AndThen<Any?, Any?>, self.right as AndThen<Any?, Any?>),
          current,
          joins
        )
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  private tailrec fun rotateAccumulate(
    left: AndThen<Any?, Any?>,
    right: AndThen<Any?, Any?>
  ): AndThen<Any?, Any?> = when (left) {
    is Concat<*, *, *> -> rotateAccumulate(
      left.left as AndThen<Any?, Any?>,
      (left.right as AndThen<Any?, Any?>).andThenF(right)
    )
    is Join -> Join(left.fa.andThen { it.andThenF(right) })
    is Single<*, *> -> left.andThenF(right)
  }
}
