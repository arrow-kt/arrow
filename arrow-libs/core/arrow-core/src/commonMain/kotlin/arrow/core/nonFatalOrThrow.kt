package arrow.core

/**
 * Returns the Throwable if NonFatal and throws it otherwise.
 *
 * @throws Throwable the Throwable `this` if Fatal
 * @return the Throwable `this` if NonFatal
 *
 * ```kotlin
 * import arrow.*
 * import arrow.core.*
 *
 * fun unsafeFunction(i: Int): String =
 *    when (i) {
 *         1 -> throw IllegalArgumentException("Non-Fatal")
 *         2 -> throw OutOfMemoryError("Fatal")
 *         else -> "Hello"
 *    }
 *
 * fun main() {
 *   val nonFatal: Either<Throwable, String> =
 *   //sampleStart
 *   try {
 *      Either.Right(unsafeFunction(1))
 *   } catch (t: Throwable) {
 *       Either.Left(t.nonFatalOrThrow())
 *   }
 *   //sampleEnd
 *   println(nonFatal)
 * }
 * ```
 * <!--- KNIT example-nonfatalorthrow-01.kt -->
 *
 */
// https://youtrack.jetbrains.com/issue/KT-36036
public fun Throwable.nonFatalOrThrow(): Throwable =
  if (NonFatal(this)) this else throw this
