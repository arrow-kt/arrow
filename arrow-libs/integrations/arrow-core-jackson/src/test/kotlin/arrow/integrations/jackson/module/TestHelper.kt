package arrow.integrations.jackson.module

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import arrow.core.PotentiallyUnsafeNonEmptyOperation
import arrow.core.bothIor
import arrow.core.leftIor
import arrow.core.rightIor
import arrow.core.toOption
import arrow.core.wrapAsNonEmptyListOrThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonTypeRef
import tools.jackson.module.kotlin.kotlinModule

internal fun basicKotlinArrowMapper(
  eitherModuleConfig: EitherModuleConfig = EitherModuleConfig("left", "right"),
  iorModuleConfig: IorModuleConfig = IorModuleConfig("left", "right"),
): JsonMapper = JsonMapper.builder().addModule(kotlinModule()).addArrowModule(eitherModuleConfig, iorModuleConfig).build()

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

fun <A, B> Arb.Companion.either(left: Arb<A>, right: Arb<B>): Arb<Either<A, B>> = choice(left.map { Either.Left(it) }, right.map { Either.Right(it) })

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
fun <A> Arb.Companion.nonEmptyList(a: Arb<A>): Arb<NonEmptyList<A>> = list(a, 1..100).map { it.wrapAsNonEmptyListOrThrow() }

fun <A> Arb.Companion.nonEmptySet(a: Arb<A>): Arb<NonEmptySet<A>> = nonEmptyList(a).map { it.toNonEmptySet() }

fun <L, R> Arb.Companion.ior(arbL: Arb<L>, arbR: Arb<R>): Arb<Ior<L, R>> = Arb.choice(
  arbitrary { arbL.bind().leftIor() },
  arbitrary { arbR.bind().rightIor() },
  arbitrary { (arbL.bind() to arbR.bind()).bothIor() },
)
