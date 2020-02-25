package arrow.test.generators

import arrow.Kind2
import arrow.core.Either
import arrow.core.ForEither
import arrow.core.ForIor
import arrow.core.ForValidated
import arrow.core.Ior
import arrow.core.Validated
import io.kotlintest.properties.Gen

interface GenK2<F> {
  /**
   * lifts Gen<A> and Gen<B> to the context F. the resulting Gen can be used to create types Kind2<F, A, B>
   */
  fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<F, A, B>>
}

fun Either.Companion.genK2() =
  object : GenK2<ForEither> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForEither, A, B>> =
      Gen.either(genA, genB) as Gen<Kind2<ForEither, A, B>>
  }

fun Ior.Companion.genK2() =
  object : GenK2<ForIor> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForIor, A, B>> =
      Gen.ior(genA, genB) as Gen<Kind2<ForIor, A, B>>
  }

fun Validated.Companion.genK2() =
  object : GenK2<ForValidated> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForValidated, A, B>> =
      Gen.validated(genA, genB) as Gen<Kind2<ForValidated, A, B>>
  }
