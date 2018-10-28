package arrow.data

interface IterableHolder<out A> {
  fun getWrappedIterable() : Iterable<A>
}