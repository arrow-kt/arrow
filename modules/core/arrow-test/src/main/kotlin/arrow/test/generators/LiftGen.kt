package arrow.test.generators

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import io.kotlintest.properties.Gen

interface LiftGen<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A> liftGen(gen: Gen<A>): Gen<Kind<F, A>>
}

fun Option.Companion.liftGen() = object : LiftGen<ForOption> {
  override fun <A> liftGen(gen: Gen<A>): Gen<Kind<ForOption, A>> =
    Gen.option(gen) as Gen<Kind<ForOption, A>>
}
