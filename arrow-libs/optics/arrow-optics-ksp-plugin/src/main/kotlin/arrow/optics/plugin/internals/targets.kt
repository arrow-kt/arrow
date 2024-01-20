package arrow.optics.plugin.internals

internal val ALL_TARGETS = OpticsTarget.entries
internal val SEALED_TARGETS = setOf(OpticsTarget.PRISM, OpticsTarget.DSL)
internal val VALUE_TARGETS = setOf(OpticsTarget.ISO, OpticsTarget.DSL)
internal val OTHER_TARGETS = setOf(OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL)
