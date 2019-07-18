package arrow.persistent.internal

object EmptyIterator : Iterator<Nothing> {

  override fun hasNext(): Boolean {
    return false
  }

  override fun next(): Nothing {
    throw NoSuchElementException()
  }
}
