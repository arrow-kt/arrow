package arrow.persistent.internal

import arrow.core.Option

/**
 * Maps the elements of this Iterator lazily using the given `mapper`.
 */
fun <T, U> Iterator<T>.map(mapper: (T) -> U): Iterator<U> {
  if (!hasNext()) {
    return EmptyIterator
  } else {
    val that = this
    return object : Iterator<U> {

      override fun hasNext(): Boolean {
        return that.hasNext()
      }

      override fun next(): U {
        if (!hasNext()) {
          throw NoSuchElementException()
        }
        return mapper(that.next())
      }
    }
  }
}

fun <T> Iterator<T>.find(predicate: (T) -> Boolean): Option<T> {
  for (a in this) {
    if (predicate(a)) {
      return Option.just(a)
    }
  }
  return Option.empty()
}
