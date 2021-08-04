package arrow.core

public fun <A : Comparable<A>> sort(a: A, b: A): Pair<A, A> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun <A : Comparable<A>> sort(a: A, b: A, c: A): Triple<A, A, A> =
  when {
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun <A : Comparable<A>> sort(a: A, vararg aas: A): List<A> =
  (listOf(a) + aas).sorted()

public fun <A> sort(a: A, b: A, comparator: Comparator<A>): Pair<A, A> =
  if (comparator.compare(a, b) <= 0) Pair(a, b) else Pair(b, a)

public fun <A> sort(a: A, b: A, c: A, comparator: Comparator<A>): Triple<A, A, A> =
  when {
    comparator.compare(a, b) <= 0 && comparator.compare(b, c) <= 0 -> Triple(a, b, c)
    comparator.compare(a, b) <= 0 -> if (comparator.compare(c, a) <= 0) Triple(c, a, b) else Triple(a, c, b)
    comparator.compare(b, a) <= 0 && comparator.compare(a, c) <= 0 -> Triple(b, a, c)
    else -> if (comparator.compare(c, b) <= 0) Triple(c, b, a) else Triple(b, c, a)
  }

public fun <A> sort(a: A, vararg aas: A, comparator: Comparator<A>): List<A> =
  (listOf(a) + aas).sortedWith(comparator)

public fun sort(a: Byte, b: Byte): Pair<Byte, Byte> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Byte, b: Byte, c: Byte): Triple<Byte, Byte, Byte> =
  when {
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Byte, vararg aas: Byte): List<Byte> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public fun sort(a: Short, b: Short): Pair<Short, Short> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Short, b: Short, c: Short): Triple<Short, Short, Short> =
  when {
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Short, vararg aas: Short): List<Short> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public fun sort(a: Int, b: Int): Pair<Int, Int> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Int, b: Int, c: Int): Triple<Int, Int, Int> =
  when {
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Int, vararg aas: Int): List<Int> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public fun sort(a: Long, b: Long): Pair<Long, Long> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Long, b: Long, c: Long): Triple<Long, Long, Long> =
  when {
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Long, vararg aas: Long): List<Long> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public const val FloatInstanceDeprecation: String =
  "Comparison operators for Float are deprecated. Due to how equality of floating-point numbers work, they're not lawful under equality."

@Deprecated(FloatInstanceDeprecation)
public fun sort(a: Float, b: Float): Pair<Float, Float> =
  when {
    a.isNaN() -> Pair(b, a)
    b.isNaN() -> Pair(a, b)
    else -> if (a <= b) Pair(a, b) else Pair(b, a)
  }

@Deprecated(FloatInstanceDeprecation)
public fun sort(a: Float, b: Float, c: Float): Triple<Float, Float, Float> =
  when {
    a.isNaN() -> when {
      b.isNaN() -> Triple(c, b, a)
      c.isNaN() -> Triple(b, c, a)
      else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
    }
    b.isNaN() -> when {
      c.isNaN() -> Triple(a, b, c)
      else -> if (a <= c) Triple(a, c, b) else Triple(c, a, b)
    }
    c.isNaN() -> if (a <= b) Triple(a, b, c) else Triple(b, a, c)
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

@Deprecated(FloatInstanceDeprecation)
public fun sort(a: Float, vararg aas: Float): List<Float> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public const val DoubleInstanceDeprecation: String =
  "Comparison operators for Double are deprecated. Due to how equality of floating-point numbers work, they're not lawful under equality."

@Deprecated(DoubleInstanceDeprecation)
public fun sort(a: Double, b: Double): Pair<Double, Double> =
  when {
    a.isNaN() -> Pair(b, a)
    b.isNaN() -> Pair(a, b)
    else -> if (a <= b) Pair(a, b) else Pair(b, a)
  }

@Deprecated(DoubleInstanceDeprecation)
public fun sort(a: Double, b: Double, c: Double): Triple<Double, Double, Double> =
  when {
    a.isNaN() -> when {
      b.isNaN() -> Triple(c, b, a)
      c.isNaN() -> Triple(b, c, a)
      else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
    }
    b.isNaN() -> when {
      c.isNaN() -> Triple(a, b, c)
      else -> if (a <= c) Triple(a, c, b) else Triple(c, a, b)
    }
    c.isNaN() -> if (a <= b) Triple(a, b, c) else Triple(b, a, c)
    a <= b && b <= c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    b <= a && a <= c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

@Deprecated(DoubleInstanceDeprecation)
public fun sort(a: Double, vararg aas: Double): List<Double> =
  (arrayOf(a) + aas.toTypedArray()).sorted()
