package arrow.fx.coroutines.stream

import arrow.fx.coroutines.IQueue
import arrow.fx.coroutines.isEmpty
import arrow.fx.coroutines.prependTo
import arrow.fx.coroutines.size
import kotlin.math.max
import kotlin.math.min

/**
 * Strict, finite sequence of values that allows index-based random access of elements.
 *
 * `Chunk`s can be created from a variety of collection types using methods on the `Chunk` companion
 *  i.e. [Chunk], [Chunk.iterable], [Chunk.array]. Additionally, the `Chunk` companion
 * defines a subtype of `Chunk` for each primitive type, using an unboxed primitive array.
 *
 * The operations on `Chunk` are all defined strictly. For example, `c.map(f).map(g).map(h)` results in
 * intermediate chunks being created (1 per call to `map`).
 */
abstract class Chunk<out O> {

  /** Returns the number of elements in this chunk. */
  abstract fun size(): Int

  /** Returns the element at the specified index. Throws if index is < 0 or >= size. */
  abstract operator fun get(i: Int): O

  /*
   * Protected `copyToArray_` implementation.
   * The Array is **guaranteed** to be `O`, which is type checked by the public extension function.
   */
  protected abstract fun copyToArray_(xs: Array<Any?>, start: Int): Unit

  // Trick to combine `protected` with `internal` to expose a typed extension `copyToArray` function.
  internal fun internalCopyToArray(xs: Array<*>, start: Int): Unit {
    copyToArray_(xs as Array<Any?>, start)
  }

  /** True if size is zero, false otherwise. */
  fun isEmpty(): Boolean =
    size() == 0

  /** False if size is zero, true otherwise. */
  fun isNotEmpty(): Boolean =
    size() > 0

  /** Gets the first element of this chunk. */
  fun firstOrNull(): O? =
    if (isEmpty()) null else get(0)

  /** Returns the first element for which the predicate returns true or `null` if no elements satisfy the predicate. */
  inline fun firstOrNull(p: (O) -> Boolean): O? {
    var result: O? = null
    var i = 0
    while (i < size() && result == null) {
      val o = get(i)
      if (p(o)) result = o
      i += 1
    }

    return result
  }

  /** kotlin-std exposes same alias **/
  fun find(p: (O) -> Boolean): O? =
    firstOrNull(p)

  /** Gets the last element of this chunk. */
  fun lastOrNull(): O? =
    if (isEmpty()) null else get(size() - 1)

  /** Returns the last element for which the predicate returns true or `null` if no elements satisfy the predicate. */
  inline fun lastOrNull(p: (O) -> Boolean): O? {
    var last: O? = null
    forEach { o ->
      if (p(o)) last = o
    }
    return last
  }

  /** kotlin-std exposes same alias **/
  inline fun findLast(p: (O) -> Boolean): O? =
    lastOrNull(p)

  /** Creates a new chunk by applying `f` to each element in this chunk. */
  inline fun <A> map(f: (O) -> A): Chunk<A> =
    when (val s = size()) {
      0 -> Empty
      1 -> Singleton(f(this[0]))
      else -> array(Array<Any?>(s) { i ->
        f(get(i))
      } as Array<A>)
    }

  /** Maps `f` over the elements of this chunk and concatenates the result. */
  inline fun <O2> flatMap(f: (O) -> Chunk<O2>): Chunk<O2> =
    if (isEmpty()) empty()
    else {
      val buff = ArrayList<Chunk<O2>>(size())
      forEach { o -> buff.add(f(o)) }

      val totalSize = buff.fold(0) { acc, c -> acc + c.size() }

      array(ArrayList<O2>(totalSize).apply {
        buff.forEach { c ->
          c.forEach { o2 -> add(o2) }
        }
      }.toArray() as Array<O2>)
    }

  /** Returns a chunk that has only the elements that satisfy the supplied predicate. */
  inline fun filter(p: (O) -> Boolean): Chunk<O> {
    val b = ArrayList<O>(size())
    forEach { o -> if (p(o)) b.add(o) }
    return array(b.toArray() as Array<out O>)
  }

  /** Returns a chunk that has only the elements that satisfy the supplied predicate. */
  inline fun <B> mapNotNull(p: (O) -> B?): Chunk<B> {
    val b = ArrayList<B>(size())
    forEach { o ->
      p(o)?.let(b::add)
    }

    return array(b.toArray() as Array<out B>)
  }

  /**
   * Splits this chunk in to two chunks at the specified index `n`,
   * which is guaranteed to be in-bounds.
   */
  protected abstract fun splitAtChunk_(n: Int): Pair<Chunk<O>, Chunk<O>>

  /** Splits this chunk in to two chunks at the specified index. */
  fun splitAt(n: Int): Pair<Chunk<O>, Chunk<O>> =
    when {
      n <= 0 -> Pair(empty(), this)
      n >= size() -> Pair(this, empty())
      else -> splitAtChunk_(n)
    }

  /** Takes the first `n` elements of this chunk. */
  open fun take(n: Int): Chunk<O> =
    when {
      n <= 0 -> empty()
      n == 1 -> fromNullable(firstOrNull())
      n >= size() -> this
      else -> array(Array<Any?>(n) { i ->
        get(i)
      } as Array<out O>)
    }

  /** Takes the last `n` elements of this chunk. */
  fun takeLast(n: Int): Chunk<O> =
    when {
      n <= 0 -> empty()
      n == 1 -> fromNullable(lastOrNull())
      else -> {
        val size = size()
        if (n >= size) this
        else array(Array<Any?>(n) { i ->
          get(size - i)
        } as Array<out O>)
      }
    }

  /** Drops the first `n` elements of this chunk. */
  open fun drop(n: Int): Chunk<O> =
    when {
      n <= 0 -> this
      n >= size() -> empty()
      else -> array(Array<Any?>(size() - n) { i ->
        get(i + n)
      } as Array<O>)
    }

  fun tail(): Chunk<O> =
    drop(1)

  /**
   * Returns a list containing all elements except last [n] elements.
   */
  fun dropLast(n: Int): Chunk<O> =
    take(max((size() - n), 0))

  /** Left-folds the elements of this chunk. */
  inline fun <A> fold(init: A, f: (A, O) -> A): A {
    var acc = init
    forEach { o ->
      acc = f(acc, o)
    }
    return acc
  }

  /**
   * Zips this chunk the the supplied chunk, returning a chunk of tuples.
   */
  fun <O2> zip(that: Chunk<O2>): Chunk<Pair<O, O2>> =
    zipWith(that, ::Pair)

  /**
   * Zips this chunk with the supplied chunk, passing each pair to `f`, resulting in
   * an output chunk.
   */
  inline fun <O2, O3> zipWith(that: Chunk<O2>, f: (O, O2) -> O3): Chunk<O3> {
    val sz = min(size(), that.size())
    return array(Array<Any?>(sz) { i ->
      f(get(i), that[i])
    } as Array<O3>)
  }

  /** alias for `fold` **/
  inline fun <A> foldLeft(init: A, f: (A, O) -> A): A =
    fold(init, f)

  /** Returns true if the predicate passes for all elements. */
  inline fun all(p: (O) -> Boolean): Boolean {
    var i = 0
    var result = true
    while (i < size() && result) {
      result = p(get(i))
      i += 1
    }
    return result
  }

  inline fun any(p: (O) -> Boolean): Boolean {
    if (isEmpty()) return false
    for (element in this) if (p(element)) return true
    return false
  }

  /** Invokes the supplied function for each element of this chunk. */
  inline fun forEach(f: (O) -> Unit): Unit {
    var i = 0
    while (i < size()) {
      f(get(i))
      i += 1
    }
  }

  /** Invokes the supplied function for each element of this chunk. */
  inline fun forEachIndexed(f: (Int, O) -> Unit): Unit {
    var i = 0
    while (i < size()) {
      f(i, get(i))
      i += 1
    }
  }

  /**
   * Creates an iterator that iterates the elements of this chunk.
   * The returned iterator is not thread safe.
   */
  operator fun iterator(): Iterator<O> = object : Iterator<O> {
    var i = 0
    override fun hasNext(): Boolean =
      (i) < size()

    override fun next(): O {
      val result = get(i)
      i += 1
      return result
    }
  }

  /**
   * Creates an iterator that iterates the elements of this chunk starting at index [n].
   * The returned iterator is not thread safe.
   */
  fun iterator(n: Int): Iterator<O> = object : Iterator<O> {
    val offset = max(n, 0)
    var i = 0
    override fun hasNext(): Boolean =
      (i + offset) < size()

    override fun next(): O {
      val result = get(i + offset)
      i += 1
      return result
    }
  }

  /** Creates an iterator that iterates the elements of this chunk in reverse order. The returned iterator is not thread safe. */
  fun reverseIterator(): Iterator<O> = object : Iterator<O> {
    var i = size() - 1
    override fun hasNext(): Boolean = i >= 0
    override fun next(): O {
      val result = get(i)
      i -= 1
      return result
    }
  }

  /**
   * Returns the index of the first element which passes the specified predicate (i.e., `p(i) == true`)
   * or `null` if no elements pass the predicate.
   */
  inline fun indexOfFirst(p: (O) -> Boolean): Int? {
    var i = 0
    var result = -1
    while (result < 0 && i < size()) {
      if (p(get(i))) result = i
      i += 1
    }

    return if (result == -1) null else result
  }

  /**
   * Maps the supplied stateful function over each element, outputting the final state and the accumulated outputs.
   * The first invocation of `f` uses `init` as the input state value. Each successive invocation uses
   * the output state of the previous invocation.
   */
  inline fun <S, O2> mapAccumulate(init: S, f: (S, O) -> Pair<S, O2>): Pair<S, Chunk<O2>> {
    var s = init
    val ch = array(Array<Any?>(size()) { i ->
      val (s2, o2) = f(s, get(i))
      s = s2
      o2
    } as Array<O2>)

    return Pair(s, ch)
  }

  /** alias for `foldLeft` **/
  inline fun <O2> scan(z: O2, f: (O2, O) -> O2): Chunk<O2> =
    scanLeft(z, f)

  /** Like `foldLeft` but emits each intermediate result of `f`. */
  inline fun <O2> scanLeft(z: O2, f: (O2, O) -> O2): Chunk<O2> =
    scanLeft_(z, true, f).first

  /** Like `scanLeft` except the final element is emitted as a standalone value instead of as
   * the last element of the accumulated chunk.
   *
   * Equivalent to `val b = a.scanLeft(z)(f); val (c, carry) = b.splitAt(b.size - 1)`.
   */
  inline fun <O2> scanLeftCarry(z: O2, f: (O2, O) -> O2): Pair<Chunk<O2>, O2> =
    scanLeft_(z, false, f)

  @PublishedApi internal inline fun <O2> scanLeft_(z: O2, emitFinal: Boolean, f: (O2, O) -> O2): Pair<Chunk<O2>, O2> {
    val size = if (emitFinal) size() + 1 else size()
    var acc = z

    val ch = array(Array<Any?>(size) { i ->
      if (emitFinal && size == i) acc
      else {
        acc = f(acc, get(i))
        acc
      }
    } as Array<O2>)

    return Pair(ch, acc)
  }

  protected open fun toArray(): Array<Any?> =
    Array(size()) { i -> get(i) }

  internal fun internalToArray(): Array<Any?> =
    Array(size()) { i -> get(i) }

  /** Converts this chunk to a list. */
  fun toList(): List<O> =
    when (size()) {
      0 -> emptyList()
      1 -> listOf(get(0))
      else -> List(size(), ::get)
    }

  override fun hashCode(): Int =
    fold(1) { acc, o ->
      31 * acc + o.hashCode()
    }

  override fun equals(other: Any?): Boolean =
    if (other is Chunk<*>) size() == other.size() && (0 until size()).all { i -> get(i) == other[i] }
    else false

  override fun toString(): String =
    toList().joinToString(prefix = "Chunk(", separator = ", ", postfix = ")")

  companion object {

    operator fun <O> invoke(size: Int, init: (index: Int) -> O): Chunk<O> =
      array(Array(max(size, 0), init as (Int) -> Any?) as Array<O>)

    /** Chunk with no elements. */
    fun <O> empty(): Chunk<O> = Empty

    /** Creates a chunk consisting of a single element. */
    fun <O> just(o: O): Chunk<O> = Singleton(o)

    fun <O> fromNullable(o: O?): Chunk<O> =
      if (o == null) empty() else just(o)

    fun <O> iterable(l: Iterable<O>): Chunk<O> =
      when (val size = l.size()) {
        0 -> empty()
        1 -> just(l.first())
        else -> {
          val array = arrayOfNulls<Any?>(size)
          l.forEachIndexed { i, o ->
            array[i] = o
          }
          array(array as Array<out O>)
        }
      }

    /** Creates a chunk backed by an array. If `O` is a primitive type, elements will be boxed. */
    fun <O> boxed(values: Array<O>): Chunk<O> =
      Boxed(values, 0, values.size)

    /**
     * Creates a chunk backed by a subsequence of an array.
     * If `A` is a primitive type, elements will be boxed.
     */
    fun <O> boxed(values: Array<O>, offset: Int, length: Int): Chunk<O> =
      Boxed(values, offset, length)

    /** Creates a chunk backed by an array of booleans. */
    fun booleans(values: BooleanArray): Chunk<Boolean> =
      Booleans(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of booleans. */
    fun booleans(values: BooleanArray, offset: Int, length: Int): Chunk<Boolean> =
      Booleans(values, offset, length)

    /** Creates a chunk backed by an array of bytes. */
    fun bytes(values: ByteArray): Chunk<Byte> =
      Bytes(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of bytes. */
    fun bytes(values: ByteArray, offset: Int, length: Int): Chunk<Byte> =
      Bytes(values, offset, length)

    /** Creates a chunk backed by an array of shorts. */
    fun shorts(values: ShortArray): Chunk<Short> =
      Shorts(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of shorts. */
    fun shorts(values: ShortArray, offset: Int, length: Int): Chunk<Short> =
      Shorts(values, offset, length)

    /** Creates a chunk backed by an array of doubles. */
    fun doubles(values: DoubleArray): Chunk<Double> =
      Doubles(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of doubles. */
    fun doubles(values: DoubleArray, offset: Int, length: Int): Chunk<Double> =
      Doubles(values, offset, length)

    /** Creates a chunk backed by an array of ints. */
    fun ints(values: IntArray): Chunk<Int> =
      Ints(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of ints. */
    fun ints(values: IntArray, offset: Int, length: Int): Chunk<Int> =
      Ints(values, offset, length)

    /** Creates a chunk backed by an array of longs. */
    fun longs(values: LongArray): Chunk<Long> =
      Longs(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of ints. */
    fun longs(values: LongArray, offset: Int, length: Int): Chunk<Long> =
      Longs(values, offset, length)

    /** Creates a chunk backed by an array of floats. */
    fun floats(values: FloatArray): Chunk<Float> =
      Floats(values, 0, values.size)

    /** Creates a chunk backed by a subsequence of an array of floats. */
    fun floats(values: FloatArray, offset: Int, length: Int): Chunk<Float> =
      Floats(values, offset, length)

    /** Creates a chunk backed by an array. */
    fun <O> array(values: Array<out O>): Chunk<O> =
      when (values.size) {
        0 -> empty()
        1 -> just(values[0])
        else -> boxed(values)
      }

    operator fun <O> invoke(vararg oos: O): Chunk<O> =
      array(oos)

    /** Concatenates the specified sequence of chunks in to a single chunk, avoiding boxing. */
    fun <A> concat(chunks: Iterable<Chunk<A>>): Chunk<A> =
      when {
        chunks.isEmpty() -> empty()
        chunks.any { ch -> ch.any { it is Boolean } } -> concatBooleans(chunks as Iterable<Chunk<Boolean>>) as Chunk<A>
        chunks.any { ch -> ch.any { it is Byte } } -> concatBytes(chunks as Iterable<Chunk<Byte>>) as Chunk<A>
        chunks.any { ch -> ch.any { it is Float } } -> concatFloats(chunks as Iterable<Chunk<Float>>) as Chunk<A>
        chunks.any { ch -> ch.any { it is Double } } -> concatDoubles(chunks as Iterable<Chunk<Double>>) as Chunk<A>
        chunks.any { ch -> ch.any { it is Short } } -> concatShorts(chunks as Iterable<Chunk<Short>>) as Chunk<A>
        chunks.any { ch -> ch.any { it is Int } } -> concatInts(chunks as Iterable<Chunk<Int>>) as Chunk<A>
        chunks.any { ch -> ch.any { it is Long } } -> concatLongs(chunks as Iterable<Chunk<Long>>) as Chunk<A>
        else -> {
          val size = chunks.fold(0) { acc, chunk -> acc + chunk.size() }
          val buffer = arrayOfNulls<Any?>(size)
          var idx = 0
          chunks.forEach { chunk ->
            chunk.forEach { a ->
              buffer[idx++] = a
            }
          }
          array(buffer as Array<A>)
        }
      }
  }

  object Empty : Chunk<Nothing>() {
    override fun size(): Int = 0
    override fun get(i: Int): Nothing = throw RuntimeException("Chunk.empty[$i]")
    override fun splitAtChunk_(n: Int): Pair<Chunk<Nothing>, Chunk<Nothing>> = TODO("INTERNAL DEV ERROR NUB")
    override fun copyToArray_(xs: Array<Any?>, start: Int) = Unit
  }

  class Singleton<O>(val value: O) : Chunk<O>() {
    override fun size(): Int = 1
    override operator fun get(i: Int): O = if (i == 0) value else throw IndexOutOfBoundsException()
    protected override fun splitAtChunk_(n: Int): Pair<Chunk<O>, Chunk<O>> = TODO("INTERNAL DEV ERROR NUB")
    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      xs[start] = value
    }
  }

  class Boxed<O>(val values: Array<O>, val offset: Int, val length: Int) : Chunk<O>() {

    init {
      checkBounds(values.size, offset, length)
    }

    override fun size() = length

    override operator fun get(i: Int): O =
      values[offset + i]

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun splitAtChunk_(n: Int): Pair<Chunk<O>, Chunk<O>> =
      Pair(Boxed(values, offset, n), Boxed(values, offset + n, length - n))

    override fun drop(n: Int): Chunk<O> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Boxed(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<O> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Boxed(values, offset, n)
      }

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Booleans(val values: BooleanArray, val offset: Int, val length: Int) : Chunk<Boolean>() {

    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Boolean = values[offset + i]

    override fun splitAtChunk_(n: Int): Pair<Chunk<Boolean>, Chunk<Boolean>> =
      Pair(Booleans(values, offset, n), Booleans(values, offset + n, length - n))

    fun copyToArray(xs: BooleanArray, start: Int) {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Boolean> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Booleans(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Boolean> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Booleans(values, offset, n)
      }

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Bytes(val values: ByteArray, val offset: Int, val length: Int) : Chunk<Byte>() {

    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Byte = values[offset + i]

    fun copyToArray(xs: ByteArray, start: Int): Unit {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Byte> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Bytes(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Byte> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Bytes(values, offset, n)
      }

    override fun splitAtChunk_(n: Int): Pair<Chunk<Byte>, Chunk<Byte>> =
      Pair(Bytes(values, offset, n), Bytes(values, offset + n, length - n))

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Shorts(val values: ShortArray, val offset: Int, val length: Int) : Chunk<Short>() {
    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Short = values[offset + i]

    fun copyToArray(xs: ShortArray, start: Int) {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Short> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Shorts(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Short> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Shorts(values, offset, n)
      }

    override fun splitAtChunk_(n: Int): Pair<Chunk<Short>, Chunk<Short>> =
      Pair(Shorts(values, offset, n), Shorts(values, offset + n, length - n))

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Doubles(val values: DoubleArray, val offset: Int, val length: Int) : Chunk<Double>() {
    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Double = values[offset + i]

    fun copyToArray(xs: DoubleArray, start: Int) {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Double> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Doubles(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Double> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Doubles(values, offset, n)
      }

    override fun splitAtChunk_(n: Int): Pair<Chunk<Double>, Chunk<Double>> =
      Pair(Doubles(values, offset, n), Doubles(values, offset + n, length - n))

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Ints(val values: IntArray, val offset: Int, val length: Int) : Chunk<Int>() {
    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Int =
      values[offset + i]

    fun copyToArray(xs: IntArray, start: Int) {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Int> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Ints(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Int> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Ints(values, offset, n)
      }

    override fun splitAtChunk_(n: Int): Pair<Chunk<Int>, Chunk<Int>> =
      Pair(Ints(values, offset, n), Ints(values, offset + n, length - n))

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Longs(val values: LongArray, val offset: Int, val length: Int) : Chunk<Long>() {
    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Long = values[offset + i]

    fun copyToArray(xs: LongArray, start: Int): Unit {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Long> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Longs(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Long> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Longs(values, offset, n)
      }

    override fun splitAtChunk_(n: Int): Pair<Chunk<Long>, Chunk<Long>> =
      Pair(Longs(values, offset, n), Longs(values, offset + n, length - n))

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  class Floats(val values: FloatArray, val offset: Int, val length: Int) : Chunk<Float>() {

    init {
      checkBounds(values.size, offset, length)
    }

    override fun size(): Int = length

    override fun get(i: Int): Float =
      values[offset + i]

    fun copyToArray(xs: FloatArray, start: Int) {
      values.copyInto(
        startIndex = offset,
        destination = xs,
        destinationOffset = start,
        endIndex = offset + length
      )
    }

    override fun copyToArray_(xs: Array<Any?>, start: Int) {
      values.toTypedArray()
        .copyInto(
          startIndex = offset,
          destination = xs,
          destinationOffset = start,
          endIndex = offset + length
        )
    }

    override fun drop(n: Int): Chunk<Float> =
      when {
        n <= 0 -> this
        n >= size() -> empty()
        else -> Floats(values, offset + n, length - n)
      }

    override fun take(n: Int): Chunk<Float> =
      when {
        n <= 0 -> empty()
        n >= size() -> this
        else -> Floats(values, offset, n)
      }

    override fun splitAtChunk_(n: Int): Pair<Chunk<Float>, Chunk<Float>> =
      Pair(Floats(values, offset, n), Floats(values, offset + n, length - n))

    override fun toArray(): Array<Any?> =
      values.sliceArray(offset..(offset + length)) as Array<Any?>
  }

  /**
   * A FIFO queue of chunks that provides an O(1) size method and provides the ability to
   * take and drop individual elements while preserving the chunk structure as much as possible.
   *
   * This is similar to a queue of individual elements but chunk structure is maintained.
   */
  class Queue<A> internal constructor(
    val chunks: IQueue<Chunk<A>>,
    val size: Int
  ) : Iterable<A> {
    override fun iterator(): Iterator<A> =
      chunks.iterator().flatMap { it.iterator() }

    /** Enqueue a chunk to the end of this chunk queue. */
    fun enqueue(c: Chunk<A>): Queue<A> =
      Queue(chunks.enqueue(c), size + c.size())

    fun isEmpty(): Boolean =
      size == 0

    fun isNotEmpty(): Boolean =
      !isEmpty()

    /** Takes the first `n` elements of this chunk queue in a way that preserves chunk structure. */
    fun take(n: Int): Queue<A> =
      when {
        n <= 0 -> empty()
        n >= size -> this
        else -> {
          fun loop(acc: IQueue<Chunk<A>>, rem: IQueue<Chunk<A>>, toTake: Int): Queue<A> =
            if (toTake <= 0) Queue(acc, n)
            else {
              val (next, tail) = rem.dequeue()
              val nextSize = next.size()
              if (nextSize < toTake) loop(acc.enqueue(next), tail, toTake - nextSize)
              else if (nextSize == toTake) Queue(acc.enqueue(next), n)
              else Queue(acc.enqueue(next.take(toTake)), n)
            }
          loop(IQueue.empty(), chunks, n)
        }
      }

    /** Takes the last `n` elements of this chunk queue in a way that preserves chunk structure. */
    fun takeLast(n: Int): Queue<A> =
      if (n <= 0) empty() else drop(size - n)

    /** Drops the first `n` elements of this chunk queue in a way that preserves chunk structure. */
    fun drop(n: Int): Queue<A> =
      when {
        n <= 0 -> this
        n >= size -> empty()
        else -> {
          fun loop(rem: IQueue<Chunk<A>>, toDrop: Int): Queue<A> =
            if (toDrop <= 0) Queue(rem, size - n)
            else {
              val next = rem.first()
              val nextSize = next.size()
              when {
                nextSize < toDrop -> loop(rem.tail(), toDrop - nextSize)
                nextSize == toDrop -> Queue(rem.tail(), size - n)
                else -> Queue(next.drop(toDrop) prependTo rem.tail(), size - n)
              }
            }
          loop(chunks, n)
        }
      }

    /** Drops the last `n` elements of this chunk queue in a way that preserves chunk structure. */
    fun dropLast(n: Int): Queue<A> =
      if (n <= 0) this else take(size - n)

    /** Converts this chunk queue to a single chunk, copying all chunks to a single chunk. */
    fun toChunk(): Chunk<A> =
      concat(chunks)

    override fun equals(other: Any?): Boolean =
      if (other is Queue<*>) size == other.size && chunks == other.chunks
      else false

    override fun hashCode(): Int =
      chunks.hashCode()

    override fun toString(): String =
      chunks.joinToString(prefix = "Queue(", separator = ", ", postfix = ")")

    companion object {
      private val empty_: Queue<Nothing> = Queue(IQueue.empty(), 0)

      fun <A> empty(): Queue<A> = empty_ as Queue<A>

      operator fun <A> invoke(vararg chunks: Chunk<A>): Queue<A> =
        chunks.fold(empty()) { acc, a ->
          acc.enqueue(a)
        }

      operator fun <A> invoke(chunks: Iterable<Chunk<A>>): Queue<A> =
        chunks.fold(empty()) { acc, a ->
          acc.enqueue(a)
        }

      /**
       * Creates a chunk consisting of the first `n` elements of `queue` and returns the remainder.
       */
      fun <A> queueFirstN(n: Int, q: IQueue<A>): Pair<Chunk<A>, IQueue<A>> =
        when {
          n <= 0 -> Pair(Chunk.empty(), q)
          n == 1 -> {
            val (hd, tl) = q.dequeue()
            Pair(just(hd), tl)
          }
          else -> {
            val bldr = ArrayList<A>()
            var cur = q
            var rem = n
            while (rem > 0 && cur.isNotEmpty()) {
              val (hd, tl) = cur.dequeue()
              bldr += hd
              cur = tl
              rem -= 1
            }

            Pair(array(bldr.toArray() as Array<out A>), cur)
          }
        }
    }
  }
}

fun <A> Chunk<A>.intersperse(separator: A): Chunk<A> {
  val bldr = ArrayList<A>(size() * 2)
  forEach { o ->
    bldr.add(separator)
    bldr.add(o)
  }

  return Chunk.array(bldr.toArray() as Array<out A>)
}

/** Prepends a chunk to the start of this chunk queue. */
infix fun <A> Chunk<A>.prependTo(q: Chunk.Queue<A>): Chunk.Queue<A> =
  Chunk.Queue(this prependTo q.chunks, size() + q.size)

fun <O> Chunk<O>.copyToArray(xs: Array<O>, start: Int = 0): Unit {
  internalCopyToArray(xs, start)
}

fun <O> Chunk<O>.toArray(): Array<O> =
  internalToArray() as Array<O>

fun Chunk<Byte>.copyToArray(xs: ByteArray, start: Int = 0): Unit =
  if (this is Chunk.Bytes) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

fun Chunk<Long>.copyToArray(xs: LongArray, start: Int = 0): Unit =
  if (this is Chunk.Longs) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

fun Chunk<Boolean>.copyToArray(xs: BooleanArray, start: Int = 0): Unit =
  if (this is Chunk.Booleans) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

fun Chunk<Short>.copyToArray(xs: ShortArray, start: Int = 0): Unit =
  if (this is Chunk.Shorts) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

fun Chunk<Int>.copyToArray(xs: IntArray, start: Int = 0): Unit =
  if (this is Chunk.Ints) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

fun Chunk<Float>.copyToArray(xs: FloatArray, start: Int = 0): Unit =
  if (this is Chunk.Floats) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

fun Chunk<Double>.copyToArray(xs: DoubleArray, start: Int = 0): Unit =
  if (this is Chunk.Doubles) copyToArray(xs, start)
  else {
    var idx = 0
    while (idx < xs.size && idx < size()) {
      xs[start + idx] = get(idx)
      idx++
    }
  }

/** Concatenates the specified sequence of boolean chunks in to a single chunk. */
internal fun concatBooleans(
  chunks: Iterable<Chunk<Boolean>>
): Chunk<Boolean> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = BooleanArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.booleans(array)
  }

/** Concatenates the specified sequence of bytes chunks in to a single chunk. */
internal fun concatBytes(
  chunks: Iterable<Chunk<Byte>>
): Chunk<Byte> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = ByteArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.bytes(array)
  }

/** Concatenates the specified sequence of long chunks in to a single chunk. */
internal fun concatLongs(
  chunks: Iterable<Chunk<Long>>
): Chunk<Long> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = LongArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.longs(array)
  }

/** Concatenates the specified sequence of float chunks in to a single chunk. */
internal fun concatFloats(
  chunks: Iterable<Chunk<Float>>
): Chunk<Float> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = FloatArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.floats(array)
  }

/** Concatenates the specified sequence of double chunks in to a single chunk. */
internal fun concatDoubles(
  chunks: Iterable<Chunk<Double>>
): Chunk<Double> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = DoubleArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.doubles(array)
  }

/** Concatenates the specified sequence of short chunks in to a single chunk. */
internal fun concatShorts(
  chunks: Iterable<Chunk<Short>>
): Chunk<Short> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = ShortArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.shorts(array)
  }

/** Concatenates the specified sequence of int chunks in to a single chunk. */
internal fun concatInts(
  chunks: Iterable<Chunk<Int>>
): Chunk<Int> =
  if (chunks.isEmpty()) Chunk.empty()
  else {
    val size = chunks.fold(0) { acc, ch -> acc + ch.size() }
    val array = IntArray(size)
    var offset = 0
    chunks.forEach { c ->
      if (c.isNotEmpty()) {
        c.copyToArray(array, offset)
        offset += c.size()
      }
    }
    Chunk.ints(array)
  }
