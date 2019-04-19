package arrow.data.fingertree.internal

internal sealed class Affix<A> {
  data class One<A>(val a: A) : Affix<A>()
  data class Two<A>(val a: A, val b: A) : Affix<A>()
  data class Three<A>(val a: A, val b: A, val c: A) : Affix<A>()
  data class Four<A>(val a: A, val b: A, val c: A, val d: A) : Affix<A>()

  fun append(item: A): Affix<A> = fromList(this.toList() + listOf(item))
  fun prepend(item: A): Affix<A> = fromList(listOf(item) + this.toList())

  fun toList(): List<A> = when (this) {
    is One -> listOf(this.a)
    is Two -> listOf(this.a, this.b)
    is Three -> listOf(this.a, this.b, this.c)
    is Four -> listOf(this.a, this.b, this.c, this.d)
  }

  fun toFingerTree(): FingerTreeInternal<A> = when (this) {
    is One -> FingerTreeInternal.Single(this.a)
    is Two -> FingerTreeInternal.Deep(One(this.a), FingerTreeInternal.Empty(), One(this.b))
    is Three -> FingerTreeInternal.Deep(Two(this.a, this.b), FingerTreeInternal.Empty(), One(this.c))
    is Four -> FingerTreeInternal.Deep(Three(this.a, this.b, this.c), FingerTreeInternal.Empty(), One(this.d))
  }

  fun head(): A =
    when (this) {
      is One -> this.a
      is Two -> this.a
      is Three -> this.a
      is Four -> this.a
    }

  fun dropHead(): Affix<A> =
    when (this) {
      is One -> throw RuntimeException("Cannot drop head")
      is Two -> One(this.b)
      is Three -> Two(this.b, this.c)
      is Four -> Three(this.b, this.c, this.d)
    }

  fun dropLast(): Affix<A> =
    when (this) {
      is One -> throw RuntimeException("Cannot drop head")
      is Two -> One(this.a)
      is Three -> Two(this.a, this.b)
      is Four -> Three(this.a, this.b, this.c)
    }

  companion object {
    fun <B> fromList(xs: List<B>): Affix<B> = when (xs.size) {
      1 -> One(xs[0])
      2 -> Two(xs[0], xs[1])
      3 -> Three(xs[0], xs[1], xs[2])
      4 -> Four(xs[0], xs[1], xs[2], xs[3])
      else -> throw IllegalArgumentException("TODO")
    }
  }
}
