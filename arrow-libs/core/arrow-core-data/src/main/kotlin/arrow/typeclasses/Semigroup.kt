package arrow.typeclasses

interface Semigroup<A> {
  /**
   * Combine two [A] values.
   */
  fun A.combine(b: A): A

  operator fun A.plus(b: A): A =
    this.combine(b)

  fun A.maybeCombine(b: A?): A =
    b?.let { combine(it) } ?: this

  companion object {
    fun <A> list(): Semigroup<List<A>> =
      Monoid.list()
  }
}
