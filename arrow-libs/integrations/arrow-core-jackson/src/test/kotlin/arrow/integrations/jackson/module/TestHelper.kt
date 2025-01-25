package arrow.integrations.jackson.module

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import arrow.core.toNonEmptyListOrNull
import arrow.core.toNonEmptySetOrNull
import arrow.core.toOption
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull

inline fun <reified T> T.shouldRoundTrip(mapper: ObjectMapper) {
  val encoded = mapper.writeValueAsString(this)
  val decoded = mapper.readValue(encoded, jacksonTypeRef<T>())
  decoded shouldBe this
}

inline fun <reified T> String.shouldRoundTripOtherWay(mapper: ObjectMapper) {
  val decoded = mapper.readValue(this, jacksonTypeRef<T>())
  val encoded = mapper.writeValueAsString(decoded)
  mapper.readTree(encoded) shouldBe mapper.readTree(this)
}

fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> = arb.orNull().map { it.toOption() }

fun <A, B> Arb.Companion.either(left: Arb<A>, right: Arb<B>): Arb<Either<A, B>> =
  choice(left.map { Either.Left(it) }, right.map { Either.Right(it) })

fun <A> Arb.Companion.nonEmptyList(a: Arb<A>): Arb<NonEmptyList<A>> =
  list(a).filter(List<A>::isNotEmpty).map { it.toNonEmptyListOrNull()!! }

fun <A> Arb.Companion.nonEmptySet(a: Arb<A>): Arb<NonEmptySet<A>> =
  list(a).filter(List<A>::isNotEmpty).map { it.toNonEmptySetOrNull()!! }
