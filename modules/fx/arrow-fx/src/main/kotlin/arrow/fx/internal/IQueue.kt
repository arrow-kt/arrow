package arrow.fx.internal

import arrow.core.Tuple2
import arrow.core.Option
import arrow.core.None
import arrow.core.Some
import arrow.core.toT
import arrow.core.extensions.list.foldable.exists
import arrow.core.extensions.list.foldable.nonEmpty

/**
 *  Port of `scala.collection.immutable.Queue`
 * `Queue` objects implement data structures that allow to
 *  insert and retrieve elements in a first-in-first-out (FIFO) manner.
 *
 *  `Queue` is implemented as a pair of `List`s, one containing the ''in'' elements and the other the ''out'' elements.
 *  Elements are added to the ''in'' list and removed from the ''out'' list. When the ''out'' list runs dry, the
 *  queue is pivoted by replacing the ''out'' list by ''in.reverse'', and ''in'' by ''Nil''.
 *
 */

internal class IQueue<A> private constructor(val lIn: List<A>, val lOut: List<A>) {

  private fun <A> Iterable<A>.head() = first()
  private fun <A> Iterable<A>.tail() = drop(1)
  private fun cons(a: A, l: List<A>): List<A> = listOf(a) + l

  fun isEmpty(): Boolean = lIn.isEmpty() && lOut.isEmpty()
  fun nonEmpty(): Boolean = lIn.nonEmpty() || lOut.nonEmpty()

  fun head(): A =
    when {
      lOut.nonEmpty() -> lOut.head()
      lIn.nonEmpty() -> lIn.last()
      else -> throw NoSuchElementException("head on empty queue")
    }

  fun tail(): IQueue<A> =
    when {
      lOut.nonEmpty() -> IQueue(lIn, lOut.tail())
      lIn.nonEmpty() -> IQueue(emptyList(), lIn.reversed().tail())
      else -> throw NoSuchElementException("tail on empty queue")
    }

  fun forAll(p: (A) -> Boolean): Boolean =
    lIn.all(p) && lOut.all(p)

  fun exists(p: (A) -> Boolean): Boolean =
    lIn.exists(p) || lOut.exists(p)

  fun length(): Int = lIn.size + lOut.size

  fun enqueue(elem: A): IQueue<A> = IQueue(cons(elem, lIn), lOut)

  fun enqueue(elems: Iterable<A>): IQueue<A> = IQueue(elems.reversed() + lIn, lOut)

  fun dequeue(): Tuple2<A, IQueue<A>> =
    when {
      lOut.isEmpty() && !lIn.isEmpty() -> {
        val rev = lIn.reversed()
        rev.head() toT IQueue(emptyList(), rev.tail())
      }
      lOut.nonEmpty() -> lOut.head() toT IQueue(lIn, lOut.tail())
      else -> throw NoSuchElementException("dequeue on empty queue")
    }

  fun dequeueOption(): Option<Tuple2<A, IQueue<A>>> =
    if (isEmpty()) None
    else Some(dequeue())

  fun front(): A = head()

  fun filter(p: (A) -> Boolean): IQueue<A> =
    IQueue(lIn.filter(p), lOut.filter(p))

  fun filterNot(p: (A) -> Boolean): IQueue<A> =
    IQueue(lIn.filterNot(p), lOut.filterNot(p))

  override fun toString(): String = "Queue(${lIn.joinToString(separator = ", ")}, ${lOut.joinToString(separator = ", ")})"

  companion object {
    fun <A> empty(): IQueue<A> = IQueue(emptyList(), emptyList())
    fun <A> invoke(vararg a: A): IQueue<A> = IQueue(emptyList(), a.toList())
  }

  fun toList(): List<A> = lOut + lIn.reversed()
}
