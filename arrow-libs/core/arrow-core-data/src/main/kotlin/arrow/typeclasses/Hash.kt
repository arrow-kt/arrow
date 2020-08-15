package arrow.typeclasses

/**
 * A type class used to represent non-cryptographic hashing for objects of type [F]
 *
 * A hash function is a mapping of arbitrary data ([F]) to an output set of fixed size ([Int]). The result, a hash value, is
 *  most commonly used in collections like HashTable as a lookup value.
 *
 * > Note: To implement this typeclass for your datatype you have to either implement [hash] or [hashWithSalt] otherwise they will call their
 *  default implementations recursively and crash.
 *
 * > Much of the internal structure is based on [hashable](https://hackage.haskell.org/package/hashable-1.3.0.0/)
 *
 * @see <a href="http://arrow-kt.io/docs/arrow/typeclasses/hash/">Hash documentation</a>
 *
 */
interface Hash<in F> {

  /**
   * Produces a hash for an object of type [F].
   *
   * @receiver The object to hash
   * @returns an int representing the object hash
   *
   * ```kotlin:ank:playground
   * import arrow.core.extensions.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = String.hash().run { "MyString".hash() }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun F.hash(): Int = hashWithSalt(defaultSalt)

  /**
   * Produces a hash for an object of type [F] with a given salt.
   *
   * @receiver The object to hash
   * @param salt The salt to apply
   * @returns an int representing the objects hash
   *
   * ```kotlin:ank:playground
   * import arrow.core.extensions.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val result = String.hash().run { "MyString".hashWithSalt(10) }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   */
  fun F.hashWithSalt(salt: Int): Int = salt.combineHashes(hash())

  companion object {

    /**
     * Construct an instance of [Hash] from a function `(F) -> Int`.
     *
     * @param hashF function that computes a hash for any object of type [F]
     * @returns an instance of [Hash] that is defined by the hashF function
     *
     * ```kotlin:ank:playground
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
      override fun F.hash(): Int = hashF(this)
    }

    /**
     * Construct an instance of [Hash] from a function `(F, Int) -> Int`.
     *
     * @param hashF function that computes a hash for any object of type [F]
     * @returns an instance of [Hash] that is defined by the hashF function
     *
     * ```kotlin:ank:playground
     * import arrow.typeclasses.Hash
     * import arrow.typeclasses.combineHashes
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Hash<String> { s: String, salt: Int -> salt.combineHashes(s.hashCode()) }.run { "MyString".hash() }
     *   //sampleEnd
     *   println(result)
     * }
     *  ```
     */
    inline operator fun <F> invoke(crossinline hashF: (F, Int) -> Int): Hash<F> = object : Hash<F> {
      override fun F.hashWithSalt(salt: Int): Int = hashF(this, salt)
    }

    /**
     * Retrieve an instance of [Hash] where the hash function delegates to kotlin's `Any?.hashCode()` function
     *
     * @returns an instance of [Hash] that always delegates to kotlin's native hashCode functionality
     *
     * ```kotlin:ank:playground
     * import arrow.typeclasses.Hash
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val result = Hash.any().run { 1.5.hash() }
     *   //sampleEnd
     *   println(result)
     * }
     *  ```
     */
    fun any(): Hash<Any?> = HashAny

    private object HashAny : Hash<Any?> {
      override fun Any?.hash(): Int = hashCode()
    }
  }
}

/**
 * Combine two hashes
 */
private fun Int.combineHashes(h: Int): Int = (this * 16777619) xor h

/**
 * Convenience because a lot of hash instances use this
 */
fun Int.hashWithSalt(salt: Int): Int = salt.combineHashes(this.hashCode())

private const val defaultSalt: Int = 0x087fc72c
