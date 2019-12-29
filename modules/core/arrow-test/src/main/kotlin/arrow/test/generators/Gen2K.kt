package arrow.test.generators

import arrow.Kind2
import arrow.core.Either
import arrow.core.ForEither
import arrow.core.ForIor
import arrow.core.Ior
import io.kotlintest.properties.Gen

interface Gen2K<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<F, A, B>>
}

fun Either.Companion.gen2K() =
  object : Gen2K<ForEither> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForEither, A, B>> =
      Gen.either(genA, genB) as Gen<Kind2<ForEither, A, B>>
  }

fun Ior.Companion.gen2K() =
  object : Gen2K<ForIor> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForIor, A, B>> =
      Gen.ior(genA, genB) as Gen<Kind2<ForIor, A, B>>
  }
