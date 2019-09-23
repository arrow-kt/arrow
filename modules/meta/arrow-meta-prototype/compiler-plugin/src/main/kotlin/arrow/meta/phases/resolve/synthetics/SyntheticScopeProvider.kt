package arrow.meta.phases.resolve.synthetics

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.types.KotlinType

interface SyntheticScopeProvider : ExtensionPhase {
  fun CompilerContext.syntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor?
  fun CompilerContext.syntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor>
  fun CompilerContext.syntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor>
  fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor>
  fun CompilerContext.syntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor>
}