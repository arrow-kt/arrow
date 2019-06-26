package arrow.meta.typeclasses

import org.jetbrains.kotlin.descriptors.ClassDescriptor

internal data class ExtensionCompatibilityResult(val candidates: List<ClassDescriptor>,
                                                 val substitutions: List<TypeSubstitution>)