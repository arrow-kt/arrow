package arrow.meta.plugin.idea;

import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.ResolutionScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.synthetic.JavaSyntheticPropertiesScope
import org.jetbrains.kotlin.synthetic.SyntheticScopeProviderExtension
import org.jetbrains.kotlin.types.KotlinType

class MetaSyntheticScopeProviderExtension : SyntheticScopeProviderExtension {
    override fun getScopes(
      moduleDescriptor: ModuleDescriptor,
      javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope
    ): List<SyntheticScope> {
        println("MetaSyntheticScopeProviderExtension.getScopes: $moduleDescriptor, $javaSyntheticPropertiesScope")
        return listOf(MetaScope)
    }
}

object MetaScope : SyntheticScope.Default() {
    override fun getSyntheticConstructor(constructor: ConstructorDescriptor): ConstructorDescriptor? {
        println("MetaScope.getSyntheticConstructor: $constructor")
        return super.getSyntheticConstructor(constructor)
    }

    override fun getSyntheticConstructors(scope: ResolutionScope): Collection<FunctionDescriptor> {
        println("MetaScope.getSyntheticConstructors: $scope")
        return super.getSyntheticConstructors(scope)
    }

    override fun getSyntheticConstructors(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> {
        println("MetaScope.getSyntheticConstructors: $scope, $name")
        return super.getSyntheticConstructors(scope, name, location)
    }

    override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, location: LookupLocation): Collection<PropertyDescriptor> {
        println("MetaScope.getSyntheticExtensionProperties: $receiverTypes")
        return super.getSyntheticExtensionProperties(receiverTypes, location)
    }

    override fun getSyntheticExtensionProperties(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<PropertyDescriptor> {
        println("MetaScope.getSyntheticExtensionProperties: $receiverTypes")
        return super.getSyntheticExtensionProperties(receiverTypes, name, location)
    }

    override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>): Collection<FunctionDescriptor> {
        println("MetaScope.getSyntheticMemberFunctions: $receiverTypes")
        return super.getSyntheticMemberFunctions(receiverTypes)
    }

    override fun getSyntheticMemberFunctions(receiverTypes: Collection<KotlinType>, name: Name, location: LookupLocation): Collection<FunctionDescriptor> {
        println("MetaScope.getSyntheticMemberFunctions: $receiverTypes, $name")
        return super.getSyntheticMemberFunctions(receiverTypes, name, location)
    }

    override fun getSyntheticStaticFunctions(scope: ResolutionScope): Collection<FunctionDescriptor> {
        println("MetaScope.getSyntheticStaticFunctions: $scope")
        return super.getSyntheticStaticFunctions(scope)
    }

    override fun getSyntheticStaticFunctions(scope: ResolutionScope, name: Name, location: LookupLocation): Collection<FunctionDescriptor> {
        println("MetaScope.getSyntheticStaticFunctions: $scope, $name")
        return super.getSyntheticStaticFunctions(scope, name, location)
    }
}