package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Predicate
import arrow.core.toOption

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Array<out T>.firstOption(): Option<T> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun BooleanArray.firstOption(): Option<Boolean> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun ByteArray.firstOption(): Option<Byte> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun CharArray.firstOption(): Option<Char> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun DoubleArray.firstOption(): Option<Double> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun FloatArray.firstOption(): Option<Float> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun IntArray.firstOption(): Option<Int> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun LongArray.firstOption(): Option<Long> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun ShortArray.firstOption(): Option<Short> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Array<out T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun BooleanArray.firstOption(predicate: Predicate<Boolean>): Option<Boolean> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun ByteArray.firstOption(predicate: Predicate<Byte>): Option<Byte> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun CharArray.firstOption(predicate: Predicate<Char>): Option<Char> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun DoubleArray.firstOption(predicate: Predicate<Double>): Option<Double> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun FloatArray.firstOption(predicate: Predicate<Float>): Option<Float> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun IntArray.firstOption(predicate: Predicate<Int>): Option<Int> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun LongArray.firstOption(predicate: Predicate<Long>): Option<Long> = firstOrNull(predicate).toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun ShortArray.firstOption(predicate: Predicate<Short>): Option<Short> = firstOrNull(predicate).toOption()
