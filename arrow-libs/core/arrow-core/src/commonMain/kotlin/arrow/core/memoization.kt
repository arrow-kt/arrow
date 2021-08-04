@file:JvmName("Memoization")

package arrow.core

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.jvm.JvmName

/**
 * Memoizes the given **pure** function so that invocations with the same arguments will only execute the function once.
 *
 * ```kotlin:ank:playground
 * import arrow.core.memoize
 * fun someWorkIntensiveFunction(someParam: Int): String = "$someParam"
 *
 * fun main() {
 *   //sampleStart
 *   val memoizedF = ::someWorkIntensiveFunction.memoize()
 *
 *   // The first invocation will store the argument and result in a cache inside the `memoizedF` reference.
 *   val value1 = memoizedF(42)
 *   // This second invocation won't really call the `someWorkIntensiveFunction` function
 *   //but retrieve the result from the previous invocation instead.
 *   val value2 = memoizedF(42)
 *
 *   //sampleEnd
 *   println("$value1 $value2")
 * }
 * ```
 *
 * Note that calling this function with the same parameters in parallel might cause the function to be executed twice.
 */
public fun <R> (() -> R).memoize(): () -> R {
  val m = MemoizedHandler<() -> R, MemoizeKey0<R>, R>(this@memoize)
  return { m(MemoizeKey0(0)) }
}

/**
 * @see memoize
 */
public fun <P1, R> ((P1) -> R).memoize(): (P1) -> R {
  val m = MemoizedHandler<((P1) -> R), MemoizeKey1<P1, R>, R>(this@memoize)
  return { p1 -> m(MemoizeKey1(p1)) }
}

/**
 * @see memoize
 */
public fun <P1, P2, R> ((P1, P2) -> R).memoize(): (P1, P2) -> R {
  val m = MemoizedHandler<((P1, P2) -> R), MemoizeKey2<P1, P2, R>, R>(this@memoize)
  return { p1: P1, p2: P2 -> m(MemoizeKey2(p1, p2)) }
}

/**
 * @see memoize
 */
public fun <P1, P2, P3, R> ((P1, P2, P3) -> R).memoize(): (P1, P2, P3) -> R {
  val m = MemoizedHandler<((P1, P2, P3) -> R), MemoizeKey3<P1, P2, P3, R>, R>(this@memoize)
  return { p1: P1, p2: P2, p3: P3 -> m(MemoizeKey3(p1, p2, p3)) }
}

/**
 * @see memoize
 */
public fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).memoize(): (P1, P2, P3, P4) -> R {
  val m = MemoizedHandler<((P1, P2, P3, P4) -> R), MemoizeKey4<P1, P2, P3, P4, R>, R>(this@memoize)
  return { p1: P1, p2: P2, p3: P3, p4: P4 -> m(MemoizeKey4(p1, p2, p3, p4)) }
}

/**
 * @see memoize
 */
public fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).memoize(): (P1, P2, P3, P4, P5) -> R {
  val m = MemoizedHandler<((P1, P2, P3, P4, P5) -> R), MemoizeKey5<P1, P2, P3, P4, P5, R>, R>(this@memoize)
  return { p1: P1, p2: P2, p3: P3, p4: P4, p5: P5 -> m(MemoizeKey5(p1, p2, p3, p4, p5)) }
}

private interface MemoizedCall<in F, out R> {
  operator fun invoke(f: F): R
}

private data class MemoizeKey0<R>(val p1: Byte) : MemoizedCall<() -> R, R> {
  override fun invoke(f: () -> R): R = f()
}

private data class MemoizeKey1<out P1, R>(val p1: P1) : MemoizedCall<(P1) -> R, R> {
  override fun invoke(f: (P1) -> R) = f(p1)
}

private data class MemoizeKey2<out P1, out P2, R>(val p1: P1, val p2: P2) : MemoizedCall<(P1, P2) -> R, R> {
  override fun invoke(f: (P1, P2) -> R) = f(p1, p2)
}

private data class MemoizeKey3<out P1, out P2, out P3, R>(val p1: P1, val p2: P2, val p3: P3) :
  MemoizedCall<(P1, P2, P3) -> R, R> {
  override fun invoke(f: (P1, P2, P3) -> R) = f(p1, p2, p3)
}

private data class MemoizeKey4<out P1, out P2, out P3, out P4, R>(val p1: P1, val p2: P2, val p3: P3, val p4: P4) :
  MemoizedCall<(P1, P2, P3, P4) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4) -> R) = f(p1, p2, p3, p4)
}

private data class MemoizeKey5<out P1, out P2, out P3, out P4, out P5, R>(
  val p1: P1,
  val p2: P2,
  val p3: P3,
  val p4: P4,
  val p5: P5
) : MemoizedCall<(P1, P2, P3, P4, P5) -> R, R> {
  override fun invoke(f: (P1, P2, P3, P4, P5) -> R) = f(p1, p2, p3, p4, p5)
}

private class MemoizedHandler<F, in K : MemoizedCall<F, R>, out R>(val f: F) {
  private val cache = atomic(emptyMap<K, R>())
  operator fun invoke(k: K): R {
    val cached = cache.value[k]
    // No cached value found, compute one
    return if (cached == null) {
      val b = k(f)
      cache.loop { old ->
        val bb = old[k]
        // No value is set yet (race condition)
        if (bb == null) {
          // Value was set, return the value
          if (cache.compareAndSet(old, old + Pair(k, b))) return b
          // keep looping failed to set
          else Unit
          // A value was already set first, return it
        } else return bb
      }
      // Cached value found, return it
    } else cached
  }
}
