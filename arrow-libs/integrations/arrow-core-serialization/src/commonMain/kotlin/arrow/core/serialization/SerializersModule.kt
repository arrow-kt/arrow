package arrow.core.serialization

import arrow.core.*
import kotlinx.serialization.modules.SerializersModule

public val ArrowModule: SerializersModule = SerializersModule {
  contextual(Either::class) { (a, b) -> EitherSerializer(a, b) }
  contextual(Ior::class) { (a, b) -> IorSerializer(a, b) }
  contextual(NonEmptyList::class) { (t) -> NonEmptyListSerializer(t) }
  contextual(NonEmptySet::class) { (t) -> NonEmptySetSerializer(t) }
  contextual(Option::class) { (t) -> OptionSerializer(t) }
}

