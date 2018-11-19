package arrow.typeclasses

/**
 * A type class used to represent hashing for objects of type [F]
 *
 * @see // Fancy link to docs
 */
interface Hash<in F> : Eq<F> {

  /**
   * Produces a hash for an object of type [F].
   *
   * @receiver The object to hash
   * @returns an int representing the object hash
   */
  fun F.hash(): Int

  companion object {

    /**
     * Construct an instance of [Hash] from a function `(F) -> Int`.
     *
     * @param hashF function that computes a hash for any object of type [F]
     * @returns an instance of [Hash] that is defined by the hashF function
     */
    inline operator fun <F> invoke(crossinline hashF: (F) -> Int): Hash<F> = object : Hash<F> {
      override fun F.hash(): Int = hashF(this)

      override fun F.eqv(b: F): Boolean = this == b
    }

    /**
     * Retrieve an instance of [Hash] where the hash function delegates to kotlin's `Any?.hashCode()` function
     *
     * @returns an instance of [Hash] that always delegates to kotlin's native hashCode functionality
     */
    fun any(): Hash<Any?> = HashAny

    private object HashAny : Hash<Any?> {
      override fun Any?.hash(): Int = hashCode()

      override fun Any?.eqv(b: Any?): Boolean = this == b
    }
  }
}