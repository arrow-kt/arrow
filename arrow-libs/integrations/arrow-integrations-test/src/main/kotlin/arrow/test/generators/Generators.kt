package arrow.test.generators

import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.toOption
import io.kotlintest.properties.Gen

fun <B> Gen.Companion.option(gen: Gen<B>): Gen<Option<B>> =
  gen.orNull().map { it.toOption() }

fun <A> Gen.Companion.nonEmptyList(gen: Gen<A>): Gen<NonEmptyList<A>> =
  gen.flatMap { head -> Gen.list(gen).map { NonEmptyList(head, it) } }
