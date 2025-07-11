package arrow.core

public fun <A : Comparable<A>> sort(a: A, b: A): Pair<A, A> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun <A : Comparable<A>> sort(a: A, b: A, c: A): Triple<A, A, A> =
  when {
    b in a..c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    a in b..c -> Triple(b, a, c)
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
    b in a..c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    a in b..c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Byte, vararg aas: Byte): List<Byte> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public fun sort(a: Short, b: Short): Pair<Short, Short> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Short, b: Short, c: Short): Triple<Short, Short, Short> =
  when {
    b in a..c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    a in b..c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Short, vararg aas: Short): List<Short> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public fun sort(a: Int, b: Int): Pair<Int, Int> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Int, b: Int, c: Int): Triple<Int, Int, Int> =
  when {
    b in a..c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    a in b..c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Int, vararg aas: Int): List<Int> =
  (arrayOf(a) + aas.toTypedArray()).sorted()

public fun sort(a: Long, b: Long): Pair<Long, Long> =
  if (a <= b) Pair(a, b) else Pair(b, a)

public fun sort(a: Long, b: Long, c: Long): Triple<Long, Long, Long> =
  when {
    b in a..c -> Triple(a, b, c)
    a <= b -> if (c <= a) Triple(c, a, b) else Triple(a, c, b)
    a in b..c -> Triple(b, a, c)
    else -> if (c <= b) Triple(c, b, a) else Triple(b, c, a)
  }

public fun sort(a: Long, vararg aas: Long): List<Long> =
  (arrayOf(a) + aas.toTypedArray()).sorted()
