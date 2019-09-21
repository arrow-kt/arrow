package arrow.typeclasses

/**
 * ank_macro_hierarchy(arrow.typeclasses.Show)
 *
 * A type class used to get a textual representation for an instance of type [A] in a type safe way.
 *
 */
interface Show<in A> {

  /**
   * Given an object [this@show] of type [A] it returns its textual representation.
   *
   * @receiver object of type [A].
   * @returns a [String] representing [this@show].
   */
  fun A.showed(): String //TODO until meta this can't be named the same as the class

  companion object {

    /**
     * Construct a [Show] instance from a function `A.() -> String`
     *
     * @param fshow function that defines a textual representation for type [A].
     * @returns a [Show] instance that is defined by the [fshow] function.
     */
    inline operator fun <A> invoke(crossinline fshow: A.() -> String): Show<A> = object : Show<A> {
      override fun A.showed(): String =
        fshow(this)
    }

    /**
     * Construct a [Show] instance using object `toString`.
     *
     * @returns a [Show] instance that is defined by the [A] `toString` method.
     */
    fun <A> fromToString(): Show<A> = object : Show<A> {
      override fun A.showed(): String =
        toString()
    }

    /**
     * Retrieve a [Show] that defines the textual representation as toString() for type [A].
     */
    fun any(): Show<Any?> = ShowAny

    private object ShowAny : Show<Any?> {
      override fun Any?.showed(): String =
        toString()
    }
  }
}
