@file:UseSerializers(
  EitherSerializer::class,
  IorSerializer::class,
  ValidatedSerializer::class,
  OptionSerializer::class,
  NonEmptyListSerializer::class,
  NonEmptySetSerializer::class
)

package arrow.core.serialization

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import arrow.core.Validated
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlinx.serialization.Serializable

/*
 These types are needed to "trick" the kotlinx.serialization plug-in
 to use the corresponding (de)serializers for those types.
 */

@Serializable
data class EitherInside<A, B>(val thing: Either<A, B>)

@Serializable
data class IorInside<A, B>(val thing: Ior<A, B>)

@Serializable
data class ValidatedInside<A, B>(val thing: Validated<A, B>)

@Serializable
data class OptionInside<A>(val thing: Option<A>)

@Serializable
data class NonEmptyListInside<A>(val thing: NonEmptyList<A>)

@Serializable
data class NonEmptySetInside<A>(val thing: NonEmptySet<A>)

inline fun <reified T> StringSpec.backAgain(generator: Arb<T>) {
  "there and back again, ${T::class.simpleName}" {
    checkAll(generator) { e ->
      val result = Json.encodeToJsonElement<T>(e)
      val back = Json.decodeFromJsonElement<T>(result)
      back shouldBe e
    }
  }
}

/**
 * Checks that the result of serializing a value into JSON,
 * and then deserializing it, gives back the original.
 */
class BackAgainTest : StringSpec({
  backAgain(Arb.either(Arb.string(), Arb.int()).map(::EitherInside))
  backAgain(Arb.ior(Arb.string(), Arb.int()).map(::IorInside))
  backAgain(Arb.validated(Arb.string(), Arb.int()).map(::ValidatedInside))
  backAgain(Arb.option(Arb.string()).map(::OptionInside))
  backAgain(Arb.nonEmptyList(Arb.int()).map(::NonEmptyListInside))
  backAgain(Arb.nonEmptySet(Arb.int()).map(::NonEmptySetInside))
})
