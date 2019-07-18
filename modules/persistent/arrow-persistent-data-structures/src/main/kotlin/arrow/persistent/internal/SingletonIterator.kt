package arrow.persistent.internal

class SingletonIterator<T>(private val element: T) : Iterator<T> {

  private var hasNext = true

  override fun hasNext() = hasNext

  override fun next(): T {
    if (!hasNext()) {
      throw NoSuchElementException()
    }
    hasNext = false
    return element
  }
}
