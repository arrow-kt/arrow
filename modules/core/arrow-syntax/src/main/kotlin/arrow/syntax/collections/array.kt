package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Predicate
import arrow.core.toOption

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

inline fun BooleanArray.firstOption(predicate: Predicate<Boolean>): Option<Boolean> = firstOrNull(predicate).toOption()

inline fun ByteArray.firstOption(predicate: Predicate<Byte>): Option<Byte> = firstOrNull(predicate).toOption()

inline fun CharArray.firstOption(predicate: Predicate<Char>): Option<Char> = firstOrNull(predicate).toOption()

inline fun DoubleArray.firstOption(predicate: Predicate<Double>): Option<Double> = firstOrNull(predicate).toOption()

inline fun FloatArray.firstOption(predicate: Predicate<Float>): Option<Float> = firstOrNull(predicate).toOption()

inline fun IntArray.firstOption(predicate: Predicate<Int>): Option<Int> = firstOrNull(predicate).toOption()

inline fun LongArray.firstOption(predicate: Predicate<Long>): Option<Long> = firstOrNull(predicate).toOption()

inline fun ShortArray.firstOption(predicate: Predicate<Short>): Option<Short> = firstOrNull(predicate).toOption()
