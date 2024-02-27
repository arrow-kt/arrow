@file:JvmName("Composition")

package arrow.core

import kotlin.jvm.JvmName

public actual infix fun <P1, P2, IP, R> ((P1, P2) -> IP).andThen(f: (IP) -> R): (P1, P2) -> R =
  AndThen2(this).andThen(f)

public actual infix fun <IP, R> (() -> IP).andThen(f: (IP) -> R): () -> R =
  AndThen0(this).andThen(f)

public actual infix fun <P1, IP, R> ((P1) -> IP).andThen(f: (IP) -> R): (P1) -> R =
  AndThen1(this).andThen(f)

public actual infix fun <IP, R, P1> ((IP) -> R).compose(f: (P1) -> IP): (P1) -> R =
  AndThen1(this).compose(f)

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

private sealed class AndThen0<A> : () -> A {

  private data class Single<A>(val f: () -> A, val index: Int) : AndThen0<A>()

  private data class Concat<A, B>(val left: AndThen0<A>, val right: AndThen1<A, B>) : AndThen0<B>() {
    override fun toString(): String = "AndThen.Concat(...)"
  }

  fun <X> andThen(g: (A) -> X): AndThen0<X> =
    when (this) {
      // Fusing calls up to a certain threshold
      is Single ->
        if (index != maxStackDepthSize) Single({ g(f()) }, index + 1)
        else andThenF(AndThen1(g))
      else -> andThenF(AndThen1(g))
    }

  @Suppress("UNCHECKED_CAST")
  override fun invoke(): A =
    loop(this as AndThen0<Any?>, Unit, 0)

  override fun toString(): String =
    "AndThen0(...)"

  companion object {

    operator fun <A> invoke(f: () -> A): AndThen0<A> =
      when (f) {
        is AndThen0<A> -> f
        else -> Single(f, 0)
      }

    @Suppress("UNCHECKED_CAST")
    tailrec fun <A> loop(self: AndThen0<Any?>, current: Any?, joins: Int): A = when (self) {
      is Single -> if (joins == 0) self.f() as A else loop(self.f() as AndThen0<Any?>, null, joins - 1)
      is Concat<*, *> -> {
        when (val oldLeft = self.left) {
          is Single<*> -> {
            val left = oldLeft as Single<Any?>
            val newSelf = self.right as AndThen1<Any?, Any?>
            AndThen1.loop(newSelf, left.f(), joins)
          }
          is Concat<*, *> -> loop(
            rotateAccumulate(self.left as AndThen0<Any?>, self.right as AndThen1<Any?, Any?>),
            current,
            joins
          )
        }
      }
    }

    @Suppress("UNCHECKED_CAST")
    tailrec fun rotateAccumulate(
      left: AndThen0<Any?>,
      right: AndThen1<Any?, Any?>
    ): AndThen0<Any?> = when (left) {
      is Concat<*, *> -> rotateAccumulate(
        left.left as AndThen0<Any?>,
        (left.right as AndThen1<Any?, Any?>).andThenF(right)
      )
      is Single<*> -> left.andThenF(right)
    }
  }

  fun <X> andThenF(right: AndThen1<A, X>): AndThen0<X> =
    Concat(this, right)
}

private sealed class AndThen1<A, B> : (A) -> B {

  private data class Single<A, B>(val f: (A) -> B, val index: Int) : AndThen1<A, B>()

  private data class Concat<A, E, B>(val left: AndThen1<A, E>, val right: AndThen1<E, B>) : AndThen1<A, B>() {
    override fun toString(): String = "AndThen.Concat(...)"
  }

  fun <X> andThen(g: (B) -> X): AndThen1<A, X> =
    when (this) {
      // Fusing calls up to a certain threshold
      is Single ->
        if (index != maxStackDepthSize) Single({ a: A -> g(this(a)) }, index + 1)
        else andThenF(AndThen1(g))
      else -> andThenF(AndThen1(g))
    }

  infix fun <C> compose(g: (C) -> A): AndThen1<C, B> =
    when (this) {
      // Fusing calls up to a certain threshold
      is Single ->
        if (index != maxStackDepthSize) Single({ c: C -> this(g(c)) }, index + 1)
        else composeF(AndThen1(g))
      else -> composeF(AndThen1(g))
    }

  @Suppress("UNCHECKED_CAST")
  override fun invoke(a: A): B = loop(this as AndThen1<Any?, Any?>, a, 0)

  override fun toString(): String = "AndThen(...)"

  companion object {

    operator fun <A, B> invoke(f: (A) -> B): AndThen1<A, B> =
      when (f) {
        is AndThen1<A, B> -> f
        else -> Single(f, 0)
      }

    @Suppress("UNCHECKED_CAST")
    tailrec fun <B> loop(self: AndThen1<Any?, Any?>, current: Any?, joins: Int): B = when (self) {
      is Single -> if (joins == 0) self.f(current) as B else loop(
        self.f(current) as AndThen1<Any?, Any?>,
        null,
        joins - 1
      )
      is Concat<*, *, *> -> {
        when (val oldLeft = self.left) {
          is Single<*, *> -> {
            val left = oldLeft as Single<Any?, Any?>
            val newSelf = self.right as AndThen1<Any?, Any?>
            loop(newSelf, left.f(current), joins)
          }
          is Concat<*, *, *> -> loop(
            rotateAccumulate(self.left as AndThen1<Any?, Any?>, self.right as AndThen1<Any?, Any?>),
            current,
            joins
          )
        }
      }
    }

    @Suppress("UNCHECKED_CAST")
    tailrec fun rotateAccumulate(
      left: AndThen1<Any?, Any?>,
      right: AndThen1<Any?, Any?>
    ): AndThen1<Any?, Any?> = when (left) {
      is Concat<*, *, *> -> rotateAccumulate(
        left.left as AndThen1<Any?, Any?>,
        (left.right as AndThen1<Any?, Any?>).andThenF(right)
      )
      is Single<*, *> -> left.andThenF(right)
    }
  }

  fun <X> andThenF(right: AndThen1<B, X>): AndThen1<A, X> =
    Concat(this, right)

  fun <X> composeF(right: AndThen1<X, A>): AndThen1<X, B> =
    Concat(right, this)
}

private sealed class AndThen2<A, B, C> : (A, B) -> C {

  private data class Single<A, B, C>(val f: (A, B) -> C, val index: Int) : AndThen2<A, B, C>()

  private data class Concat<A, E, B, C>(val left: AndThen2<A, E, B>, val right: AndThen1<B, C>) : AndThen2<A, E, C>() {
    override fun toString(): String = "AndThen.Concat(...)"
  }

  fun <X> andThen(g: (C) -> X): AndThen2<A, B, X> =
    when (this) {
      // Fusing calls up to a certain threshold
      is Single ->
        if (index != maxStackDepthSize) Single({ a: A, b: B -> g(this(a, b)) }, index + 1)
        else andThenF(AndThen1(g))
      else -> andThenF(AndThen1(g))
    }

  @Suppress("UNCHECKED_CAST")
  override fun invoke(a: A, b: B): C =
    loop(this as AndThen2<Any?, Any?, Any?>, a, b, 0)

  override fun toString(): String = "AndThen(...)"

  companion object {

    operator fun <A, B, C> invoke(f: (A, B) -> C): AndThen2<A, B, C> =
      when (f) {
        is AndThen2<A, B, C> -> f
        else -> Single(f, 0)
      }

    @Suppress("UNCHECKED_CAST")
    tailrec fun <C> loop(self: AndThen2<Any?, Any?, Any?>, currentA: Any?, currentB: Any?, joins: Int): C =
      when (self) {
        is Single<*, *, *> -> {
          val f = self.f as ((Any?, Any?) -> Any?)
          if (joins == 0) f(currentA, currentB) as C
          else loop(f(currentA, currentB) as AndThen2<Any?, Any?, Any?>, null, null, joins - 1)
        }
        is Concat<*, *, *, *> -> {
          when (val oldLeft = self.left) {
            is Single<*, *, *> -> {
              val left = oldLeft as Single<Any?, Any?, Any?>
              val newSelf = self.right as AndThen1<Any?, Any?>
              AndThen1.loop(newSelf, left.f(currentA, currentB), joins)
            }
            is Concat<*, *, *, *> -> loop(
              rotateAccumulate(self.left as AndThen2<Any?, Any?, Any?>, self.right as AndThen1<Any?, Any?>),
              currentA,
              currentB,
              joins
            )
          }
        }
      }

    @Suppress("UNCHECKED_CAST")
    tailrec fun rotateAccumulate(
      left: AndThen2<Any?, Any?, Any?>,
      right: AndThen1<Any?, Any?>
    ): AndThen2<Any?, Any?, Any?> = when (left) {
      is Concat<*, *, *, *> -> rotateAccumulate(
        left.left as AndThen2<Any?, Any?, Any?>,
        (left.right as AndThen1<Any?, Any?>).andThenF(right)
      )
      is Single<*, *, *> -> left.andThenF(right)
    }
  }

  fun <X> andThenF(right: AndThen1<C, X>): AndThen2<A, B, X> =
    Concat(this, right)
}
