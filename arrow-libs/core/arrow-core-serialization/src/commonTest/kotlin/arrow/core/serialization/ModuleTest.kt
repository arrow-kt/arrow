package arrow.core.serialization

import arrow.core.*
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test


@Serializable
data class ContextualEitherInside<A, B>(@Contextual val thing: Either<A, B>)

@Serializable
data class ContextualIorInside<A, B>(@Contextual val thing: Ior<A, B>)

@Serializable
data class ContextualNonEmptyListInside<A>(@Contextual val thing: NonEmptyList<A>)

@Serializable
data class ContextualNonEmptySetInside<A>(@Contextual val thing: NonEmptySet<A>)

class ModuleTest {
  private val jsonWithModule = Json {
    serializersModule = SerializersModule {
      include(ArrowModule)
    }
  }

  @Test
  fun backAgainEither() =
    backAgain(Arb.either(Arb.string(), Arb.int()), jsonWithModule)

  @Test
  fun backAgainIor() =
    backAgain(Arb.ior(Arb.string(), Arb.int()), jsonWithModule)

  @Test
  fun backAgainNonEmptyList() =
    backAgain(Arb.nonEmptyList(Arb.int()), jsonWithModule)

  @Test
  fun backAgainNonEmptySet() =
    backAgain(Arb.nonEmptySet(Arb.int()), jsonWithModule)

  @Test
  fun backAgainContextualEither() =
    backAgain(Arb.either(Arb.string(), Arb.int()).map(::ContextualEitherInside), jsonWithModule)

  @Test
  fun backAgainContextualIor() =
    backAgain(Arb.ior(Arb.string(), Arb.int()).map(::ContextualIorInside), jsonWithModule)

  @Test
  fun backAgainContextualNonEmptyList() =
    backAgain(Arb.nonEmptyList(Arb.int()).map(::ContextualNonEmptyListInside), jsonWithModule)

  @Test
  fun backAgainContextualNonEmptySet() =
    backAgain(Arb.nonEmptySet(Arb.int()).map(::ContextualNonEmptySetInside), jsonWithModule)
}
