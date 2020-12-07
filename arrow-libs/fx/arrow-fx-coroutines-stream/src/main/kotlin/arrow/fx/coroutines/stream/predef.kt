package arrow.fx.coroutines.stream

import arrow.fx.coroutines.IQueue

internal inline infix fun <A, B, C> ((A) -> B).andThen(crossinline f: (B) -> C): (A) -> C =
  { a -> f(this(a)) }

internal inline infix fun <A, B, C> (suspend (A) -> B).andThen(crossinline f: suspend (B) -> C): suspend (A) -> C =
  { a: A -> f(this(a)) }

internal fun checkBounds(arraySize: Int, offset: Int, length: Int) {
  require(offset in 0..arraySize)
  require(length in 0..arraySize)
  val end = offset + length
  require(end in 0..arraySize)
}

/** The iterator which produces no values. */
internal val empty: Iterator<Nothing> = object : Iterator<Nothing> {
  override fun hasNext(): Boolean =
    false

  override fun next(): Nothing =
    throw NoSuchElementException("next on empty iterator")
}

internal fun <A, B> Iterator<A>.flatMap(f: (A) -> Iterator<B>): Iterator<B> =
  object : Iterator<B> {
    private var cur: Iterator<B> = empty

    private fun nextCur() {
      cur = f(this@flatMap.next())
    }

    override fun hasNext(): Boolean {
      while (!cur.hasNext()) {
        if (!this@flatMap.hasNext()) return false
        nextCur()
      }
      return true
    }

    override fun next(): B =
      (if (hasNext()) cur else empty).next()
  }

internal const val ArrowExceptionMessage =
  "Arrow-kt internal error. Please let us know and create a ticket at https://github.com/arrow-kt/arrow/issues/new/choose"

internal class ArrowInternalException(override val message: String = ArrowExceptionMessage) : RuntimeException(message)

internal fun <A> IQueue<A>.tail(): IQueue<A> =
  tailOrNull() ?: throw ArrowInternalException("$ArrowExceptionMessage\nTail on empty queue.")

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}

infix fun <A> A.prependTo(fa: Iterable<A>): List<A> =
  listOf(this) + fa

internal fun <A> Iterable<A>.deleteFirst(f: (A) -> Boolean): Pair<A, List<A>>? {
  tailrec fun go(rem: Iterable<A>, acc: List<A>): Pair<A, List<A>>? =
    when {
      rem.isEmpty() -> null
      else -> {
        val a = rem.first()
        val tail = rem.drop(1)
        if (!f(a)) go(tail, acc + a)
        else Pair(a, acc + tail)
      }
    }

  return go(this, emptyList())
}

internal fun <A> Iterable<A>.uncons(): Pair<A, List<A>>? =
  firstOrNull()?.let { Pair(it, drop(1)) }

internal fun Iterable<*>.isEmpty(): Boolean =
  size() == 0

internal fun Iterable<*>.size(): Int =
  when (this) {
    is Collection -> size
    else -> fold(0) { acc, _ -> acc + 1 }
  }
