package arrow.fx.coroutines

internal inline infix fun <A, B, C> ((A) -> B).andThen(crossinline f: (B) -> C): (A) -> C =
  { a -> f(this(a)) }

internal inline infix fun <A, B, C> (suspend (A) -> B).andThen(crossinline f: suspend (B) -> C): suspend (A) -> C =
  { a: A -> f(this(a)) }

@PublishedApi
internal infix fun <A> A.prependTo(fa: Iterable<A>): List<A> =
  listOf(this) + fa

internal fun <A> Iterable<A>.deleteFirst(f: (A) -> Boolean): Pair<A, List<A>>? {
  tailrec fun go(rem: Iterable<A>, acc: List<A>): Pair<A, List<A>>? =
    when {
      rem.isEmpty() -> null
      else -> {
        val a = rem.first()
        val tail = rem.drop(1)
        if (!f(a)) go(tail, acc + a)
        else Pair(a, acc + tail)
      }
    }

  return go(this, emptyList())
}

internal fun <A> Iterable<A>.uncons(): Pair<A, List<A>>? =
  firstOrNull()?.let { Pair(it, drop(1)) }

internal fun Iterable<*>.isEmpty(): Boolean =
  size() == 0

internal fun Iterable<*>.size(): Int =
  when (this) {
    is Collection -> size
    else -> fold(0) { acc, _ -> acc + 1 }
  }

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}
