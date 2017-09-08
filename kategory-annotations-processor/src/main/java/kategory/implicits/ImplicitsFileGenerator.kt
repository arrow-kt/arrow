package kategory.implicits

import kategory.common.Type
import kategory.common.Package
import kategory.implicits.AnnotatedImplicits.Consumer
import kategory.implicits.AnnotatedImplicits.Consumer.ValueParameter
import kategory.implicits.AnnotatedImplicits.Provider
import kategory.implicits.AnnotatedImplicits.Provider.Function
import kategory.implicits.AnnotatedImplicits.Provider.Property
import kategory.common.utils.ClassOrPackageDataWrapper
import kategory.common.utils.escapedClassName
import kategory.common.utils.extractFullName
import kategory.common.utils.knownError
import kategory.common.utils.plusIfNotBlank
import me.eugeniomarletti.kotlin.metadata.getJvmMethodSignature
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.NameResolver
import java.io.File

typealias ProviderInvocation = String
typealias FunctionToGenerate = String

class ImplicitsFileGenerator(
        private val generatedDir: File,
        private val annotatedList: List<AnnotatedImplicits>,
        private val useTypeAlias: Boolean
) {

    /**
     * Main entry point for consumer extension generation
     */
    fun generate() {
        val consumers: List<Consumer> = annotatedList.filterIsInstance<Consumer>()
        val providers: List<Provider> = annotatedList.filterIsInstance<Provider>()
        if (providers.isEmpty() && consumers.isEmpty()) return

        val providersByType: Map<Type, Provider> = getProvidersByTypes(providers)
        checkMissingProvidedTypes(consumers, providersByType)

        val providerInvocationsByType: Map<Type, ProviderInvocation> = getProviderInvocationsByType(providersByType)
        val consumerFunctionGroupsByPackage: Map<Package, List<List<Consumer>>> =
                getConsumerFunctionGroupsByPackage(consumers)
        val functionsToGenerateByPackage: Map<Package, List<FunctionToGenerate>> =
                getFunctionsToGenerateByPackage(consumerFunctionGroupsByPackage, providerInvocationsByType)

        val sources: List<Pair<Package, String>> = functionsToGenerateByPackage.entries.mapIndexed { counter, (`package`, functionsToGenerate) ->
            val source: String = functionsToGenerate.joinToString(prefix = "package $`package`\n\n", separator = "\n")
            `package` to source
        }
        val grouped = sources.groupBy({ (pack, _) -> pack })
        grouped.forEach { entry ->
            val pack = entry.key
            val file = File(generatedDir, implicitAnnotationClass.simpleName + ".$pack.kt")
            file.printWriter().use { w ->
                entry.value.forEach { (_, source) ->
                    w.println(source)
                }
            }
        }
    }

    /**
     * Returns a map containing mappings from the types provided to the functions satisfying the consumer constrains
     * Currently does not support higher kinds or generic arguments.
     */
    private fun getProvidersByTypes(providers: List<Provider>): Map<Type, Provider> {
        val providersByType: Map<String, List<Provider>> = providers.groupBy { provider ->
            when (provider) {
                is Provider.Function -> provider.functionProto.returnType
                is Provider.Property -> provider.propertyProto.returnType
            }.extractFullName(provider.classOrPackageProto, useTypeAlias)
        }

        val duplicatedProviders: Map<String, List<Provider>> = providersByType.filter { (_, providers) -> providers.size > 1 }
        if (duplicatedProviders.isNotEmpty())
            knownError("These $implicitAnnotationName types are provided more than once: $duplicatedProviders")

        return providersByType.mapValues { (_, providers) -> providers[0] }
    }

    /**
     * Bails processing when a consumer declares a dependency on a provider that is not provided.
     * Ideally this should ever bail with an error since the implicit provider may be provided in an external library
     * that contributes to the list of providers available.
     */
    private fun checkMissingProvidedTypes(
        consumers: List<Consumer>,
        providersByType: Map<Type, Provider>
    ) {
        val consumersByType: Map<String, List<Consumer>> = consumers.groupBy { consumer ->
            when (consumer) {
                is Consumer.ValueParameter -> consumer.valueParameterProto.type
            }.extractFullName(consumer.classOrPackageProto, useTypeAlias)
        }

        val missingProvidedTypes: Set<Type> = consumersByType.keys - providersByType.keys
        if (missingProvidedTypes.isNotEmpty())
            knownError("These $implicitAnnotationName types are requested but not provided: $missingProvidedTypes")
    }

    /**
     * Resolves names for the provider invocations
     */
    private fun getProviderInvocationsByType(providersByType: Map<Type, Provider>): Map<Type, ProviderInvocation> =
        providersByType.mapValues { (_, provider) ->
            val proto: ClassOrPackageDataWrapper = provider.classOrPackageProto
            val nameResolver: NameResolver = proto.nameResolver
            val prefix: String = when (proto) {
                is ClassOrPackageDataWrapper.Package -> proto.`package`
                is ClassOrPackageDataWrapper.Class -> nameResolver.getString(proto.classProto.fqName)
            }.escapedClassName.plusIfNotBlank(".")

            when (provider) {
                is Function -> {
                    val name: String = nameResolver.getString(provider.functionProto.name)
                    "$prefix`$name`()"
                }
                is Property -> {
                    val name: String = nameResolver.getString(provider.propertyProto.name)
                    "$prefix`$name`"
                }
            }
        }

    private fun getConsumerFunctionGroupsByPackage(consumers: List<Consumer>): Map<Package, List<List<Consumer>>> =
        consumers
            .groupBy { consumer ->
                when (consumer) {
                    is ValueParameter -> {
                        val proto: ClassOrPackageDataWrapper = consumer.classOrPackageProto
                        val nameResolver: NameResolver = proto.nameResolver
                        val function: ProtoBuf.Function = consumer.functionProto
                        val signature: String? = function.getJvmMethodSignature(nameResolver)
                        val fqFunctionSignature: String = when (proto) {
                            is ClassOrPackageDataWrapper.Package -> proto.`package`
                            is ClassOrPackageDataWrapper.Class -> nameResolver.getString(proto.classProto.fqName).replace('/', '.')
                        }.plusIfNotBlank(".") + signature
                        fqFunctionSignature
                    }
                }
            }
            .values
            .groupBy { consumersInFunction -> consumersInFunction[0].classOrPackageProto.`package` }

    private fun getFunctionsToGenerateByPackage(
            consumerFunctionGroupsByPackage: Map<Package, List<List<Consumer>>>,
            providerInvocationsByType: Map<Type, ProviderInvocation>
    ): Map<Package, List<FunctionToGenerate>> =
        consumerFunctionGroupsByPackage.mapValues { (`package`, consumerFunctionGroup) ->
            consumerFunctionGroup.map { consumersInFunction ->
                val first: Consumer = consumersInFunction[0]
                val function: ProtoBuf.Function = when (first) {
                    is Consumer.ValueParameter -> first.functionProto
                }
                val proto: ClassOrPackageDataWrapper = first.classOrPackageProto
                val nameResolver: NameResolver = proto.nameResolver
                val escapedPackage: String = `package`.escapedClassName
                val prefix: String = when (proto) {
                    is ClassOrPackageDataWrapper.Package -> ""
                    is ClassOrPackageDataWrapper.Class ->
                        nameResolver.getString(proto.classProto.fqName).escapedClassName.removePrefix(escapedPackage + ".") + "."
                }
                val escapedFunctionName: String = "`" + nameResolver.getString(function.name) + "`"

                val argsIn: String = function.valueParameterList.mapNotNull { valueParameter ->
                    extractConsumerValueParameter(valueParameter, consumersInFunction) { parameterName, consumer ->
                        valueParameter.takeIf { consumer == null }?.let {
                            "$parameterName: " + it.type.extractFullName(proto, failOnGeneric = false)
                        }
                    }
                }.joinToString()

                val argsOut: String = function.valueParameterList.map { valueParameter ->
                    extractConsumerValueParameter(valueParameter, consumersInFunction) { parameterName, consumer ->
                        if (consumer == null) parameterName
                        else {
                            val type: String = when (consumer) {
                                is ValueParameter -> consumer.valueParameterProto.type
                            }.extractFullName(consumer.classOrPackageProto, useTypeAlias, failOnGeneric = false)
                            providerInvocationsByType[type]!!
                        }
                    }
                }.joinToString()

                "fun $prefix$escapedFunctionName($argsIn) = $escapedFunctionName($argsOut)"
            }
        }

    private inline fun <T> extractConsumerValueParameter(
        valueParameter: ProtoBuf.ValueParameter,
        consumersInFunction: List<Consumer>,
        action: (parameterName: String, consumer: Consumer?) -> T
    ): T {
        val nameResolver: NameResolver = consumersInFunction[0].classOrPackageProto.nameResolver
        val paramName: String = nameResolver.getString(valueParameter.name)
        val consumer: Consumer? = consumersInFunction.firstOrNull {
            val consumerParameterName: String = when (it) {
                is Consumer.ValueParameter -> nameResolver.getString(it.valueParameterProto.name)
            }
            paramName == consumerParameterName
        }
        return action(paramName, consumer)
    }
}
