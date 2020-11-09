package arrow.fx.coroutines

/**
 *  Port of `scala.collection.immutable.Queue`
 *
 * `IQueue` objects implement data structures that allow to
 *  insert and retrieve elements in a first-in-first-out (FIFO) manner.
 *
 *  `IQueue` is implemented as a pair of `List`s, one containing the ''in'' elements and the other the ''out'' elements.
 *  Elements are added to the ''in'' list and removed from the ''out'' list. When the ''out'' list runs dry, the
 *  queue is pivoted by replacing the ''out'' list by ''in.reverse'', and ''in'' by ''Nil''.
 *
 */
data class IQueue<A> internal constructor(
  private val listIn: List<A>,
  private val listOut: List<A>,
  /** an O(1) size method. */
  val size: Int
) : Iterable<A> {

  private fun <A> Iterable<A>.tail() = drop(1)

  private fun cons(a: A, l: List<A>): List<A> = listOf(a) + l

  fun isEmpty(): Boolean =
    size == 0

  fun isNotEmpty(): Boolean =
    !isEmpty()

  internal fun first(): A =
    firstOrNull() ?: throw NoSuchElementException("first on empty queue")

  internal fun tail(): IQueue<A> =
    tailOrNull() ?: throw NoSuchElementException("tail on empty queue")

  fun firstOrNull(): A? =
    when {
      listOut.isNotEmpty() -> listOut.first()
      listIn.isNotEmpty() -> listIn.last()
      else -> null
    }

  fun tailOrNull(): IQueue<A>? =
    when {
      listOut.isNotEmpty() -> IQueue(listIn, listOut.tail(), size - 1)
      listIn.isNotEmpty() -> IQueue(emptyList(), listIn.reversed().tail(), size - 1)
      else -> null
    }

  fun all(p: (A) -> Boolean): Boolean =
    listIn.all(p) && listOut.all(p)

  fun exists(p: (A) -> Boolean): Boolean =
    listIn.any(p) || listOut.any(p)

  fun enqueue(elem: A): IQueue<A> =
    IQueue(cons(elem, listIn), listOut, size + 1)

  fun enqueue(elems: Iterable<A>): IQueue<A> =
    IQueue(elems.reversed() + listIn, listOut, size + elems.size())

  fun prepend(elem: A): IQueue<A> =
    IQueue(listIn, cons(elem, listOut), size + 1)

  fun dequeue(): Pair<A, IQueue<A>> =
    when {
      listOut.isEmpty() && listIn.isNotEmpty() -> {
        val rev = listIn.reversed()
        Pair(rev.first(), IQueue(emptyList(), rev.tail(), size - 1))
      }
      listOut.isNotEmpty() -> Pair(listOut.first(), IQueue(listIn, listOut.tail(), size - 1))
      else -> throw NoSuchElementException("dequeue on empty queue")
    }

  fun drop(n: Int): IQueue<A> = when {
    listOut.isEmpty() && listIn.isNotEmpty() -> IQueue(emptyList(), listIn.reversed().drop(n), size - n.coerceAtLeast(0))
    listOut.isNotEmpty() -> IQueue(listIn, listOut.drop(n), size - n.coerceAtLeast(0))
    else -> empty()
  }

  fun dequeueOrNull(): Pair<A, IQueue<A>>? =
    if (isEmpty()) null
    else dequeue()

  fun filter(p: (A) -> Boolean): IQueue<A> {
    val newIn = listIn.filter(p)
    val newOut = listOut.filter(p)
    return IQueue(newIn, newOut, newIn.size + newOut.size)
  }

  fun filterNot(p: (A) -> Boolean): IQueue<A> {
    val newIn = listIn.filterNot(p)
    val newOut = listOut.filterNot(p)
    return IQueue(newIn, newOut, newIn.size + newOut.size)
  }

  override fun toString(): String =
    "IQueue(${listIn.joinToString(separator = ", ")}, ${listOut.joinToString(separator = ", ")})"

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun <A> empty(): IQueue<A> = EmptyQueue as IQueue<A>
    operator fun <A> invoke(vararg a: A): IQueue<A> = IQueue(emptyList(), a.toList(), a.size)
    operator fun <A> invoke(a: A): IQueue<A> = IQueue(emptyList(), listOf(a), 1)
    operator fun <A> invoke(a: List<A>): IQueue<A> = IQueue(emptyList(), a, a.size)
  }

  override fun iterator(): Iterator<A> = toList().iterator()

  fun toList(): List<A> = listOut + listIn.reversed()
}

internal infix fun <A> A.prependTo(q: IQueue<A>): IQueue<A> =
  q.prepend(this)

private val EmptyQueue: IQueue<Nothing> = IQueue(emptyList(), emptyList(), 0)
