package arrow.optics.plugin.internals

internal val DEFAULT_TARGETS = OpticsTarget.entries - OpticsTarget.COPY
internal val SEALED_TARGETS = setOf(OpticsTarget.PRISM, OpticsTarget.LENS, OpticsTarget.DSL)
internal val VALUE_TARGETS = setOf(OpticsTarget.ISO, OpticsTarget.DSL)
internal val OTHER_TARGETS = setOf(OpticsTarget.LENS, OpticsTarget.DSL)
