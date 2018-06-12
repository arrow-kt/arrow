package arrow.optics

import arrow.data.SetK
import arrow.data.k

object SetOptics {

  /**
   * [PIso] that defines the equality between a [Set] and a [SetK]
   */
  fun <A, B> toPSetK(): PIso<Set<A>, Set<B>, SetK<A>, SetK<B>> = PIso(
    get = { it.k() },
    reverseGet = SetK<B>::set
  )

  /**
   * [Iso] that defines the equality between a [Set] and a [SetK]
   */
  fun <A> toSetK(): Iso<Set<A>, SetK<A>> = toPSetK()

}