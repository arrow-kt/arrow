package arrow.typeclasses

/**
 * ank_macro_hierarchy(arrow.typeclasses.Eq)
 *
 * A type class used to determine equality between 2 instances of the same type [F] in a type safe way.
 *
 * @see <a href="http://arrow-kt.io/docs/arrow/typeclasses/eq/">Eq documentation</a>
 */
interface Eq<in F> {

  /**
   * Compares two instances of [F] and returns true if they're considered equal for this instance.
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@eqv]
   * @returns true if [this@eqv] and [b] are equivalent, false otherwise.
   */
  fun F.eqv(b: F): Boolean

  /**
   * Compares two instances of [F] and returns true if they're considered not equal for this instance.
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@neqv]
   * @returns false if [this@neqv] and [b] are equivalent, true otherwise.
   */
  fun F.neqv(b: F): Boolean = !eqv(b)

  companion object {

    /**
     * Construct an [Eq] from a function `(F, F) -> Boolean`.
     *
     * @param feqv function that defines if two instances of type [F] are equal.
     * @returns an [Eq] instance that is defined by the [feqv] function.
     */
    inline operator fun <F> invoke(crossinline feqv: (F, F) -> Boolean): Eq<F> = object : Eq<F> {
      override fun F.eqv(b: F): Boolean =
        feqv(this, b)
    }

    /**
     * Retrieve an [Eq] that defines all instances as equal for type [F].
     *
     * @returns an [Eq] instance wherefore all instances of type [F] are equal.
     */
    fun any(): Eq<Any?> = EqAny

    private object EqAny : Eq<Any?> {
      override fun Any?.eqv(b: Any?): Boolean = this == b

      override fun Any?.neqv(b: Any?): Boolean = this != b
    }
  }
}
