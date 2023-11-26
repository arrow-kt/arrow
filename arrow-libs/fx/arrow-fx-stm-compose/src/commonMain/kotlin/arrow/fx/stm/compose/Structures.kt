package arrow.fx.stm.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import kotlin.jvm.JvmInline

public object TVar {
  public fun <A> new(value: A): MutableState<A> = mutableStateOf(value)
}

public fun <A> MutableState<A>.read(): A = value
public fun <A> MutableState<A>.unsafeRead(): A = value
public fun <A> MutableState<A>.write(newValue: A) { value = newValue }
public fun <A> MutableState<A>.modify(transform: (A) -> A) { value = transform(value) }

public object TMap {
  public fun <K, V> new(): SnapshotStateMap<K, V> = mutableStateMapOf()
}

public fun <K, V> SnapshotStateMap<K, V>.insert(key: K, value: V) { this[key] = value }
public fun <K, V> SnapshotStateMap<K, V>.update(key: K, transform: (V) -> V): Unit =
  when (val v = this[key]) {
    null -> { }
    else -> { this[key] = transform(v) }
  }

public object TList {
  public fun <A> new(): SnapshotStateList<A> = mutableStateListOf()
}

@JvmInline
public value class TSet<A>(internal val map: SnapshotStateMap<A, Boolean>): MutableSet<A> {
  public companion object {
    public fun <A> new(): TSet<A> = TSet(mutableStateMapOf())
  }

  override fun clear() { map.clear() }
  override fun add(element: A): Boolean = map.put(element, true) ?: false
  override fun addAll(elements: Collection<A>): Boolean {
    var added = false
    for (element in elements) {
      added = added || add(element)
    }
    return added
  }

  override fun remove(element: A): Boolean =
    map.remove(element) ?: false
  override fun retainAll(elements: Collection<A>): Boolean =
    remove { it !in elements }
  override fun removeAll(elements: Collection<A>): Boolean =
    remove { it in elements }
  private fun remove(predicate: (A) -> Boolean): Boolean {
    var modified = false
    for (k in map.keys.filter(predicate)) {
        modified = true
        map.remove(k)
    }
    return modified
  }

  override val size: Int get() = map.size
  override fun isEmpty(): Boolean = map.isEmpty()

  override fun contains(element: A): Boolean = element in map
  override fun containsAll(elements: Collection<A>): Boolean = elements.all { it in map }

  override fun iterator(): MutableIterator<A> = map.keys.iterator()
}

@JvmInline
public value class TQueue<A>(internal val list: SnapshotStateList<A>) {
  public companion object {
    public fun <A> new(): TQueue<A> = TQueue(mutableStateListOf())
  }

  public fun isEmpty(): Boolean = list.isEmpty()
  public fun isNotEmpty(): Boolean = !isEmpty()
  public fun size(): Int = list.size

  public fun tryPeek(): A? = list.firstOrNull()
  public fun tryRead(): A? = if (list.isEmpty()) null else list.removeFirst()

  public operator fun plusAssign(a: A) { list.add(a) }
  public fun write(a: A) { this += a }
  public fun writeFront(a: A) { list.add(0, a) }

  public fun removeAll(predicate: (A) -> Boolean) { list.removeAll { !predicate(it) } }

  public fun flush(): List<A> {
    val current = mutableListOf<A>().also { it.addAll(list) }
    list.clear()
    return current
  }
}
