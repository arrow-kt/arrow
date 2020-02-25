package arrow.test.generators

import arrow.Kind
import io.kotlintest.properties.Gen

interface GenK<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A> genK(gen: Gen<A>): Gen<Kind<F, A>>
}
