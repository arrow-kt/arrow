package arrow.fx.coroutines.stream

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
