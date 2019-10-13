package arrow.typeclasses

/**
 * A type class used to represent hashing for objects of type [F]
 *
 * A hash function is a mapping of arbitrary data ([F]) to an output set of fixed size ([Int]). The result, a hash value, is
 *  most commonly used in collections like HashTable as a lookup value
 *
 * @see <a href="http://arrow-kt.io/docs/arrow/typeclasses/hash/">Hash documentation</a>
 */
interface Hash<in F> : Eq<F> {

  /**
   * Produces a hash for an object of type [F].
   *
   * @receiver The object to hash
   * @returns an int representing the object hash
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.extensions.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = String.hash().run { "MyString".hashed() }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun F.hashed(): Int // TODO This can't be named the same as the TypeClass because in the current processor there are collisions, once we have meta then yes

  companion object {

    /**
     * Construct an instance of [Hash] from a function `(F) -> Int`.
     *
     * @param hashF function that computes a hash for any object of type [F]
     * @returns an instance of [Hash] that is defined by the hashF function
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.typeclasses.Hash
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Hash<String> { s: String -> s.hashCode() }.run { "MyString".hash() }
     *   //sampleEnd
     *   println(result)
     * }
     *  ```
     */
    inline operator fun <F> invoke(crossinline hashF: (F) -> Int): Hash<F> = object : Hash<F> {
      override fun F.hashed(): Int = hashF(this)

      override fun F.eqv(b: F): Boolean = this == b
    }

    /**
     * Retrieve an instance of [Hash] where the hash function delegates to kotlin's `Any?.hashCode()` function
     *
     * @returns an instance of [Hash] that always delegates to kotlin's native hashCode functionality
     *
     * ```kotlin:ank
     * import arrow.typeclasses.Hash
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Hash.any().run { 1.5.hashed() }
     *   //sampleEnd
     *   println(result)
     * }
     *  ```
     */
    fun any(): Hash<Any?> = HashAny

    private object HashAny : Hash<Any?> {
      override fun Any?.hashed(): Int = hashCode()

      override fun Any?.eqv(b: Any?): Boolean = this == b
    }
  }
}
