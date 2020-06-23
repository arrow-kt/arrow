package arrow.fx.coroutines

inline infix fun <A, B, C> ((A) -> B).andThen(crossinline f: (B) -> C): (A) -> C =
  { a -> f(this(a)) }

infix fun <A> A.prependTo(fa: Iterable<A>): List<A> =
  listOf(this) + fa

fun <A> Iterable<A>.deleteFirst(f: (A) -> Boolean): Pair<A, List<A>>? {
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

fun <A> Iterable<A>.uncons(): Pair<A, List<A>>? =
  firstOrNull()?.let { Pair(it, drop(1)) }

fun Iterable<*>.isEmpty(): Boolean =
  size() == 0

fun Iterable<*>.size(): Int =
  when (this) {
    is Collection -> size
    else -> fold(0) { acc, _ -> acc + 1 }
  }

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}
