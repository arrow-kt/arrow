@file:UseSerializers(
  EitherSerializer::class,
  IorSerializer::class,
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
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.decodeFromJsonElement
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
data class OptionInside<A>(val thing: Option<A>)

@Serializable
data class NonEmptyListInside<A>(val thing: NonEmptyList<A>)

@Serializable
data class NonEmptySetInside<A>(val thing: NonEmptySet<A>)

inline fun <reified T> backAgain(generator: Arb<T>, json: Json = Json) =
  runTest {
    checkAll(generator) { e ->
      val result = json.encodeToJsonElement<T>(e)
      val back = json.decodeFromJsonElement<T>(result)
      back shouldBe e
    }
  }

/**
 * Checks that the result of serializing a value into JSON,
 * and then deserializing it, gives back the original.
 */
class BackAgainTest {
  @Test fun backAgainEither() =
    backAgain(Arb.either(Arb.string(), Arb.int()).map(::EitherInside))
  @Test fun backAgainIor() =
    backAgain(Arb.ior(Arb.string(), Arb.int()).map(::IorInside))
  @Test fun backAgainOption() =
    backAgain(Arb.option(Arb.string()).map(::OptionInside))
  @Test fun backAgainNonEmptyList() =
    backAgain(Arb.nonEmptyList(Arb.int()).map(::NonEmptyListInside))
  @Test fun backAgainNonEmptySet() =
    backAgain(Arb.nonEmptySet(Arb.int()).map(::NonEmptySetInside))
}
