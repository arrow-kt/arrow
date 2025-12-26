package arrow.core

// It's important that leq is only used with in-order arguments to ensure sort stability
private inline fun <A> sort2(a: A, b: A, leq: (A, A) -> Boolean) = if (leq(a, b)) Pair(a, b) else Pair(b, a)

private inline fun <A> sort3(a: A, b: A, c: A, leq: (A, A) -> Boolean): Triple<A, A, A> = when {
  leq(a, b) -> when {
    leq(b, c) -> Triple(a, b, c)
    // so a <= b && c < b (i.e b is largest)
    leq(a, c) -> Triple(a, c, b)
    // so c < a < b
    else -> Triple(c, a, b)
  }
  // so b < a
  leq(a, c) -> Triple(b, a, c)
  // so b < a && c < a (i.e a is largest)
  leq(b, c) -> Triple(b, c, a)
  // so c < b < a
  else -> Triple(c, b, a)
}

public fun <A : Comparable<A>> sort(a: A, b: A): Pair<A, A> = sort2(a, b) { x, y -> x <= y }

public fun <A : Comparable<A>> sort(a: A, b: A, c: A): Triple<A, A, A> = sort3(a, b, c) { x, y -> x <= y }

public fun <A : Comparable<A>> sort(a: A, vararg aas: A): List<A> = buildList(aas.size + 1) {
  add(a)
  addAll(aas)
  sort()
}

context(c: Comparator<T>)
private operator fun <T> T.compareTo(other: T): Int = c.compare(this, other)

public fun <A> sort(a: A, b: A, comparator: Comparator<A>): Pair<A, A> = sort2(a, b) { x, y -> with(comparator) { x <= y } }

public fun <A> sort(a: A, b: A, c: A, comparator: Comparator<A>): Triple<A, A, A> = sort3(a, b, c) { x, y -> with(comparator) { x <= y } }

public fun <A> sort(a: A, vararg aas: A, comparator: Comparator<A>): List<A> = buildList(aas.size + 1) {
  add(a)
  addAll(aas)
  sortWith(comparator)
}

// Don't use xArrayOf(...).sorted() as it boxes values unnecessarily

public fun sort(a: Byte, b: Byte): Pair<Byte, Byte> = sort2(a, b) { x, y -> x <= y }

public fun sort(a: Byte, b: Byte, c: Byte): Triple<Byte, Byte, Byte> = sort3(a, b, c) { x, y -> x <= y }

public fun sort(a: Byte, vararg aas: Byte): List<Byte> = byteArrayOf(a, *aas).apply { sort() }.asList()

public fun sort(a: Short, b: Short): Pair<Short, Short> = sort2(a, b) { x, y -> x <= y }

public fun sort(a: Short, b: Short, c: Short): Triple<Short, Short, Short> = sort3(a, b, c) { x, y -> x <= y }

public fun sort(a: Short, vararg aas: Short): List<Short> = shortArrayOf(a, *aas).apply { sort() }.asList()

public fun sort(a: Int, b: Int): Pair<Int, Int> = sort2(a, b) { x, y -> x <= y }

public fun sort(a: Int, b: Int, c: Int): Triple<Int, Int, Int> = sort3(a, b, c) { x, y -> x <= y }

public fun sort(a: Int, vararg aas: Int): List<Int> = intArrayOf(a, *aas).apply { sort() }.asList()

public fun sort(a: Long, b: Long): Pair<Long, Long> = sort2(a, b) { x, y -> x <= y }

public fun sort(a: Long, b: Long, c: Long): Triple<Long, Long, Long> = sort3(a, b, c) { x, y -> x <= y }

public fun sort(a: Long, vararg aas: Long): List<Long> = longArrayOf(a, *aas).apply { sort() }.asList()
