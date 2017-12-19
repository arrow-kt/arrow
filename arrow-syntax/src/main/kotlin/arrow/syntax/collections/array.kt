package arrow.syntax.collections

import arrow.syntax.function.toOption
import kategory.Option
import kategory.Predicate

fun <T> Array<out T>.firstOption(): Option<T> = firstOrNull().toOption()

fun BooleanArray.firstOption(): Option<Boolean> = firstOrNull().toOption()

fun ByteArray.firstOption(): Option<Byte> = firstOrNull().toOption()

fun CharArray.firstOption(): Option<Char> = firstOrNull().toOption()

fun DoubleArray.firstOption(): Option<Double> = firstOrNull().toOption()

fun FloatArray.firstOption(): Option<Float> = firstOrNull().toOption()

fun IntArray.firstOption(): Option<Int> = firstOrNull().toOption()

fun LongArray.firstOption(): Option<Long> = firstOrNull().toOption()

fun ShortArray.firstOption(): Option<Short> = firstOrNull().toOption()

fun <T> Array<out T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()

inline fun BooleanArray.firstOption(predicate: (Boolean) -> Boolean): Option<Boolean> = firstOrNull(predicate).toOption()

inline fun ByteArray.firstOption(predicate: (Byte) -> Boolean): Option<Byte> = firstOrNull(predicate).toOption()

inline fun CharArray.firstOption(predicate: (Char) -> Boolean): Option<Char> = firstOrNull(predicate).toOption()

inline fun DoubleArray.firstOption(predicate: (Double) -> Boolean): Option<Double> = firstOrNull(predicate).toOption()

inline fun FloatArray.firstOption(predicate: (Float) -> Boolean): Option<Float> = firstOrNull(predicate).toOption()

inline fun IntArray.firstOption(predicate: (Int) -> Boolean): Option<Int> = firstOrNull(predicate).toOption()

inline fun LongArray.firstOption(predicate: (Long) -> Boolean): Option<Long> = firstOrNull(predicate).toOption()

inline fun ShortArray.firstOption(predicate: (Short) -> Boolean): Option<Short> = firstOrNull(predicate).toOption()
