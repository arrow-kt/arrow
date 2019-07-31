package arrow.meta.typeclasses

import org.jetbrains.kotlin.types.KotlinType

data class TypeSubstitution(val source: KotlinType, val target: KotlinType)