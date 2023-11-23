package arrow.fx.stm.compose

public inline fun <A> stm(noinline f: STM.() -> A): STM.() -> A = f

public interface STM {
  public fun retry(): Nothing
  public infix fun <A> (STM.() -> A).orElse(other: STM.() -> A): A
  public fun <A> catch(f: STM.() -> A, onError: STM.(Throwable) -> A): A

  public fun check(condition: Boolean) {
    if (!condition) retry()
  }

  public fun <A> TVar<A>.read(): A = variable.value
  public fun <A> TVar<A>.write(value: A) { variable.value = value }
  public fun <A> TVar<A>.modify(f: (A) -> A) { variable.value = f(variable.value) }

  public operator fun <K, V> TMap<K, V>.get(k: K): V? = map[k]
  public fun <K, V> TMap<K, V>.lookup(k: K): V? = this[k]

  public operator fun <K, V> TMap<K, V>.set(k: K, v: V) { map[k] = v }
  public operator fun <K, V> TMap<K, V>.plusAssign(kv: Pair<K, V>) { this[kv.first] = kv.second }
  public fun <K, V> TMap<K, V>.insert(k: K, v: V) { this[k] = v }
  public fun <K, V> TMap<K, V>.remove(k: K) { map.remove(k) }
  public operator fun <K, V> TMap<K, V>.contains(k: K): Boolean = k in map
  public fun <K, V> TMap<K, V>.member(k: K): Boolean = k in this
  public fun <K, V> TMap<K, V>.update(k: K, f: (V) -> V) {
    when (val v = map[k]) {
      null -> {}
      else -> map[k] = f(v)
    }
  }

  public operator fun <A> TSet<A>.plusAssign(a: A) { map[a] = true }
  public fun <A> TSet<A>.insert(a: A) { this += a }
  public fun <A> TSet<A>.remove(a: A) { map.remove(a) }
  public operator fun <A> TSet<A>.contains(a: A): Boolean = a in map
  public fun <A> TSet<A>.member(a: A): Boolean = a in this

  public fun <A> TQueue<A>.isEmpty(): Boolean = list.isEmpty()
  public fun <A> TQueue<A>.isNotEmpty(): Boolean = !isEmpty()
  public fun <A> TQueue<A>.size(): Int = list.size

  public fun <A> TQueue<A>.peek(): A = if (list.isEmpty()) retry() else list.first()
  public fun <A> TQueue<A>.tryPeek(): A? = list.firstOrNull()
  public fun <A> TQueue<A>.read(): A = if (list.isEmpty()) retry() else list.removeFirst()
  public fun <A> TQueue<A>.tryRead(): A? = if (list.isEmpty()) null else list.removeFirst()

  public operator fun <A> TQueue<A>.plusAssign(a: A) { list.add(a) }
  public fun <A> TQueue<A>.write(a: A) { this += a }
  public fun <A> TQueue<A>.writeFront(a: A) { list.add(0, a) }

  public fun <A> TQueue<A>.removeAll(predicate: (A) -> Boolean) { list.removeAll { !predicate(it) } }

  public fun <A> TQueue<A>.flush(): List<A> {
    val current = mutableListOf<A>().also { it.addAll(list) }
    list.clear()
    return current
  }
}

public class BlockedIndefinitely : Throwable("Transaction blocked indefinitely")

