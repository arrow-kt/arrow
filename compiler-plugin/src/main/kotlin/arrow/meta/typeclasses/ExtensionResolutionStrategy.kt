package arrow.meta.typeclasses


import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import arrow.meta.typeclasses.ExtensionCandidateResolution.*
import arrow.meta.typeclasses.ExtensionResolution.*
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe


val ValueParameterDescriptor.isWithAnnotated: Boolean
  get() {
    println("isWithAnnotated"); TODO()
  }

object ExtensionResolutionStrategy {

  tailrec fun resolve(
    lookingFor: ValueParameterDescriptor,
    parameters: List<ValueParameterDescriptor>,
    argumentParameterDescriptor: ValueParameterDescriptor,
    substitutions: List<TypeSubstitution>,
    lookInSupertypes: Boolean = false
  ): ExtensionCandidateResolution {

    val functionOrder = listOf(
      FindInLocalFunction,
      FindInPackage,
      FindInTypeCompanion,
      FindInContractInterfaceCompanion,
      FindInTypeSubpackages,
      FindInContractInterfaceSubpackages
    )
    val candidates = functionOrder.fold(emptyList<Resolved>()) { acc, resolution ->
      val resolved = resolution.resolve(lookingFor, parameters, argumentParameterDescriptor, substitutions, lookInSupertypes)
      when (resolved) {
        is Resolved -> acc + listOf(resolved)
        else -> acc
      }
    }.distinct()

    return if (candidates.isEmpty() && !lookInSupertypes) {
      resolve(
        lookingFor,
        parameters,
        argumentParameterDescriptor,
        substitutions,
        true
      )
    } else if (candidates.size == 1) {
      resolveArguments(
        candidates.first(),
        parameters,
        argumentParameterDescriptor,
        candidates.first().candidate.substitutions
      )
    } else if (candidates.size > 1) {
      val candidatesList = candidates.joinToString("\n") { "- ${it.candidate.value.fqNameSafe}" }
      return Unresolved(
        "Unable to resolve parameter ${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}" +
          "\nExtension resolution implements coherence: There must be a single extension in scope to satisfy the required " +
          "type constraints. Found conflicting candidates:\n$candidatesList"
      )
    } else {
      Unresolved(
        "Unable to resolve parameter (" +
          "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType})."
      )
    }
  }

  private fun resolveArguments(
    resolvedCandidate: Resolved,
    parameters: List<ValueParameterDescriptor>,
    argumentParameterDescriptor: ValueParameterDescriptor,
    substitutions: List<TypeSubstitution>
  ): ExtensionCandidateResolution {
    return when (val candidate = resolvedCandidate.candidate) {
      is ExtensionCandidate.FunctionParameter -> Resolved(candidate)
      is ExtensionCandidate.SingleClassCandidate -> {
        val extensionArguments = mutableListOf<ExtensionCandidate>()
        val newSubstitutions = java.util.ArrayList(substitutions)
        val scope = candidate.value.unsubstitutedPrimaryConstructor
        scope?.let {
          for (parameter in scope.valueParameters) {
            if (parameter.isWithAnnotated) {
              val extensionArgument = resolve(
                parameter,
                parameters,
                argumentParameterDescriptor,
                newSubstitutions
              )
              if (extensionArgument is Resolved) {
                extensionArguments.add(extensionArgument.candidate)
                newSubstitutions.addAll(extensionArgument.candidate.substitutions)
              } else {
                return Unresolved("Unable to resolve parameter (${parameter.name} : ${parameter.returnType}) in constructor for ${candidate.value.name.asString()}")
              }
            } else {
              return Unresolved("Found non-extension parameter in (${parameter.name} : ${parameter.returnType}) constructor when resolving instance for ${candidate.value.name.asString()}. Add the ''with'' keyword to let the compiler resolve it.")
            }
          }
        }
        if (extensionArguments.size > 0) {
          Resolved(
            ExtensionCandidate.NestedClassCandidate(
              candidate.value,
              extensionArguments,
              newSubstitutions
            )
          )
        } else {
          Resolved(candidate)
        }
      }
      is ExtensionCandidate.NestedClassCandidate -> Unresolved("Shouldn't happen.")
    }
  }
}