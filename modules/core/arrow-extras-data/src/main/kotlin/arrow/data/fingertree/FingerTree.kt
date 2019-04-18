package arrow.data.fingertree

import arrow.core.Option
import arrow.data.fingertree.internal.FingerTreeInternal

class FingerTree<T> internal constructor(internal: FingerTreeInternal<T> = FingerTreeInternal.Empty()) {

  private var fingerTreeInternal: FingerTreeInternal<T> = internal

  fun append(item: T) {
    this.fingerTreeInternal = fingerTreeInternal.append(item)
  }

  fun prepend(item: T) {
    this.fingerTreeInternal = fingerTreeInternal.prepend(item)
  }

  fun head(): Option<T> = this.fingerTreeInternal.viewL().flatMap {
    this.fingerTreeInternal = it.b
    Option.just(it.a)
  }

  fun tail(): Option<FingerTree<T>> = this.fingerTreeInternal.viewR().flatMap {
    this.fingerTreeInternal = it.b
    Option.just(FingerTree(it.b))
  }

  fun last(): Option<T> = this.fingerTreeInternal.viewR().flatMap {
    this.fingerTreeInternal = it.b
    Option.just(it.a)
  }

  fun init(): Option<FingerTree<T>> = this.fingerTreeInternal.viewR().flatMap {
    this.fingerTreeInternal = it.b
    Option.just(FingerTree(it.b))
  }

  fun isEmpty(): Boolean = this.fingerTreeInternal.viewL().fold({ true }, { false })

  fun asList(): List<T> = this.fingerTreeInternal.asList()

  companion object {

    fun <A> empty(): FingerTree<A> = FingerTree()

    fun <A> single(item: A): FingerTree<A> = FingerTree<A>().apply { append(item) }

    fun <A> fromList(list: List<A>): FingerTree<A> = FingerTree<A>().apply {
      list.forEach(::append)
    }

    fun <A> fromArgs(vararg items: A): FingerTree<A> = FingerTree.fromList(items.toList())
  }

}
