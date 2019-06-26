package arrow.meta.typeclasses

import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.calls.model.ResolvedValueArgument

class ExtensionValueArgument(private val delegate: ValueParameterDescriptor) : ValueParameterDescriptor by delegate, ResolvedValueArgument {
  override fun getArguments(): MutableList<ValueArgument> =
    mutableListOf()
}