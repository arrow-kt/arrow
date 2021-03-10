package arrow.syntax.function

import arrow.core.memoize as _memoize

/**
 * Memoizes the given **pure** function so that invocations with the same arguments will only execute the function once.
 *
 * ```kotlin:ank:playground
 * import arrow.syntax.function.memoize
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
@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <R> (() -> R).memoize(): () -> R =
  _memoize()

/**
 * @see memoize
 */
@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, R> ((P1) -> R).memoize(): (P1) -> R =
  _memoize()

/**
 * @see memoize
 */
@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, R> ((P1, P2) -> R).memoize(): (P1, P2) -> R =
  _memoize()

/**
 * @see memoize
 */
@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, R> ((P1, P2, P3) -> R).memoize(): (P1, P2, P3) -> R =
  _memoize()

/**
 * @see memoize
 */
@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, R> ((P1, P2, P3, P4) -> R).memoize(): (P1, P2, P3, P4) -> R =
  _memoize()

/**
 * @see memoize
 */
@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, R> ((P1, P2, P3, P4, P5) -> R).memoize(): (P1, P2, P3, P4, P5) -> R =
  _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, R> ((P1, P2, P3, P4, P5, P6) -> R).memoize(): (P1, P2, P3, P4, P5, P6) -> R =
  _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, R> ((P1, P2, P3, P4, P5, P6, P7) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7) -> R =
  _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, R> ((P1, P2, P3, P4, P5, P6, P7, P8) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8) -> R =
  _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R =
  _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10) -> R =
  _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21) -> R = _memoize()

@Deprecated(
  "arrow.syntax.function package is deprecated. Use arrow.core package instead.",
  ReplaceWith("memoize()", "arrow.core.memoize")
)
fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22, R> ((P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R).memoize(): (P1, P2, P3, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, P17, P18, P19, P20, P21, P22) -> R = _memoize()
