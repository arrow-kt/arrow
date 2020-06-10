package arrow.fx.coroutines

fun <A> identity(a: A): A = a

inline infix fun <A, B, C> ((A) -> B).andThen(crossinline f: (B) -> C): (A) -> C =
  { a -> f(this(a)) }

infix fun <A> A.prependTo(fa: List<A>): List<A> =
  listOf(this) + fa

fun <A> List<A>.deleteFirst(f: (A) -> Boolean): Pair<A, List<A>>? {
  tailrec fun go(rem: List<A>, acc: List<A>): Pair<A, List<A>>? =
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

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}
