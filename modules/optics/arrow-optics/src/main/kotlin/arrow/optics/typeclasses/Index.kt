package arrow.optics.typeclasses

import arrow.optics.Iso
import arrow.optics.Optional

/**
 * [Index] provides an [Optional] for a structure [S] to focus in an optional [A] at a given index [I].
 *
 * @param S source of [Optional]
 * @param I index
 * @param A focus of [Optional], [A] is supposed to be unique for a given pair [S] and [I].
 */
interface Index<S, I, A> {

  /**
   * Get [Optional] focus [A] for a structure [S] at index [i].
   */
  fun index(i: I): Optional<S, A>

  companion object {

    /**
     * Lift an instance of [Index] using an [Iso].
     */
    fun <S, A, I, B> fromIso(ID: Index<A, I, B>, iso: Iso<S, A>): Index<S, I, B> = object : Index<S, I, B> {
      override fun index(i: I): Optional<S, B> = iso compose ID.index(i)
    }

    /**
     * Get an [Optional] for an index [i] given an [Index].
     */
    fun <S, I, A> index(ID: Index<S, I, A>, i: I): Optional<S, A> = ID.index(i)

  }

}
