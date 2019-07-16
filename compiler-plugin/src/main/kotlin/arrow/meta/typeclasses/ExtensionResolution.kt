package arrow.meta.typeclasses

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.LazyScopeAdapter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes
import arrow.meta.typeclasses.ExtensionCandidate.*
import arrow.meta.typeclasses.ExtensionCandidateResolution.*

val ClassDescriptor.isExtensionAnnotated: Boolean
  get() {
    println("isExtensionAnnotated"); TODO()
  }

sealed class ExtensionResolution {
  object FindInLocalFunction : ExtensionResolution() {
    override fun resolve(
      lookingFor: ValueParameterDescriptor,
      parameters: List<ValueParameterDescriptor>,
      argumentParameterDescriptor: ValueParameterDescriptor,
      substitutions: List<TypeSubstitution>,
      lookInSupertypes: Boolean
    ): ExtensionCandidateResolution {
      val candidates = mutableListOf<ExtensionCandidate>()
      val newSubstitutions = java.util.ArrayList(substitutions)
      for (i in 0 until parameters.size) {
        val parameter = parameters[i]
        if (parameter.isWithAnnotated) {
          val result = isReplaceable(parameter.returnType!!, lookingFor.returnType!!, substitutions, lookInSupertypes)
          if (result.canBeReplaced) {
            newSubstitutions.addAll(result.substitutions)
            candidates.add(FunctionParameter(parameter, i, newSubstitutions))
          }
        }
      }

      return when (candidates.size) {
        1 -> Resolved(candidates[0])
        else -> Unresolved(
          "Unable to resolve extension value in local function for argument " +
            "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}."
        )
      }
    }
  }

  object FindInPackage : ExtensionResolution() {

    override fun resolve(
      lookingFor: ValueParameterDescriptor,
      parameters: List<ValueParameterDescriptor>,
      argumentParameterDescriptor: ValueParameterDescriptor,
      substitutions: List<TypeSubstitution>,
      lookInSupertypes: Boolean
    ): ExtensionCandidateResolution {
      val scope = findPackageScopeFor(lookingFor)
      val error = Unresolved(
        "Unable to resolve extension value in package for argument " +
          "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}."
      )

      if (scope != null) {
        val result = getCompatibleClasses(lookingFor, scope, substitutions, lookInSupertypes)
        val candidates = result.candidates.filter { it.visibility == Visibilities.INTERNAL }
        return when (candidates.size) {
          1 -> Resolved(SingleClassCandidate(result.candidates[0], result.substitutions))
          else -> error
        }
      }

      return error
    }
  }

  object FindInTypeCompanion : ExtensionResolution() {
    override fun resolve(
      lookingFor: ValueParameterDescriptor,
      parameters: List<ValueParameterDescriptor>,
      argumentParameterDescriptor: ValueParameterDescriptor,
      substitutions: List<TypeSubstitution>,
      lookInSupertypes: Boolean
    ): ExtensionCandidateResolution {
      val arguments = lookingFor.returnType!!.arguments
      val error = Unresolved(
        "Unable to resolve extension value in companion object for argument " +
          "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}."
      )

      for (projection in arguments) {
        val companion = findCompanionFor(projection.type)

        if (companion != null) {
          val result = getCompatibleClasses(lookingFor, companion.unsubstitutedMemberScope, substitutions, lookInSupertypes)

          return when (result.candidates.size) {
            1 -> Resolved(SingleClassCandidate(result.candidates[0], result.substitutions))
            else -> error
          }
        }
      }

      return error
    }
  }

  object FindInContractInterfaceCompanion : ExtensionResolution() {
    override fun resolve(
      lookingFor: ValueParameterDescriptor,
      parameters: List<ValueParameterDescriptor>,
      argumentParameterDescriptor: ValueParameterDescriptor,
      substitutions: List<TypeSubstitution>,
      lookInSupertypes: Boolean
    ): ExtensionCandidateResolution {
      val companion = findCompanionFor(argumentParameterDescriptor.returnType!!)
      val error = Unresolved(
        "Unable to resolve extension value in type class companion object for argument " +
          "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}."
      )

      if (companion != null) {
        val result = getCompatibleClasses(lookingFor, companion.unsubstitutedMemberScope, substitutions, lookInSupertypes)

        return when (result.candidates.size) {
          1 -> Resolved(SingleClassCandidate(result.candidates[0], result.substitutions))
          else -> error
        }
      }

      return error
    }
  }

  object FindInTypeSubpackages : ExtensionResolution() {
    override fun resolve(
      lookingFor: ValueParameterDescriptor,
      parameters: List<ValueParameterDescriptor>,
      argumentParameterDescriptor: ValueParameterDescriptor,
      substitutions: List<TypeSubstitution>,
      lookInSupertypes: Boolean
    ): ExtensionCandidateResolution {
      val arguments = lookingFor.returnType!!.arguments
      val subpackageResults = java.util.ArrayList<ExtensionCompatibilityResult>()
      val error = Unresolved(
        "Unable to resolve extension value in type subpackages for argument " +
          "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}."
      )

      for (projection in arguments) {
        val subpackages = findSubpackagesFor(projection.type)

        for (subpackage in subpackages) {
          val result = getCompatibleClasses(lookingFor, subpackage.memberScope, substitutions, lookInSupertypes)
          val candidates = result.candidates.filter { it.visibility == Visibilities.INTERNAL }
          if (candidates.size == 1) {
            subpackageResults.add(result)
          }
        }
      }

      if (subpackageResults.size == 1) {
        return when (subpackageResults[0].candidates.size) {
          1 -> Resolved(SingleClassCandidate(subpackageResults[0].candidates[0], subpackageResults[0].substitutions))
          else -> error
        }
      }

      return error
    }
  }

  object FindInContractInterfaceSubpackages : ExtensionResolution() {
    override fun resolve(
      lookingFor: ValueParameterDescriptor,
      parameters: List<ValueParameterDescriptor>,
      argumentParameterDescriptor: ValueParameterDescriptor,
      substitutions: List<TypeSubstitution>,
      lookInSupertypes: Boolean
    ): ExtensionCandidateResolution {
      val subpackageResults = java.util.ArrayList<ExtensionCompatibilityResult>()
      val subpackages = findSubpackagesFor(argumentParameterDescriptor.returnType!!)
      val error = Unresolved(
        "Unable to resolve extension value in type class subpackages for argument " +
          "${argumentParameterDescriptor.name} : ${argumentParameterDescriptor.returnType}."
      )

      for (subpackage in subpackages) {
        val result = getCompatibleClasses(lookingFor, subpackage.memberScope, substitutions, lookInSupertypes)
        val candidates = result.candidates.filter { it.visibility == Visibilities.INTERNAL }
        if (candidates.size == 1) {
          subpackageResults.add(result)
        }
      }

      if (subpackageResults.size == 1) {
        return when (subpackageResults[0].candidates.size) {
          1 -> Resolved(SingleClassCandidate(subpackageResults[0].candidates[0], subpackageResults[0].substitutions))
          else -> error
        }
      }

      return error
    }
  }

  abstract fun resolve(
    lookingFor: ValueParameterDescriptor,
    parameters: List<ValueParameterDescriptor>,
    argumentParameterDescriptor: ValueParameterDescriptor,
    substitutions: List<TypeSubstitution>,
    lookInSupertypes: Boolean
  ): ExtensionCandidateResolution

  internal fun findCompanionFor(type: KotlinType): ClassDescriptor? {
    val scope = type.memberScope
    val descriptor =
      scope.getContributedClassifier(Name.identifier("Companion"), NoLookupLocation.FOR_DEFAULT_IMPORTS) as ClassDescriptor?
    if (descriptor != null) {
      return descriptor.original
    }
    return null
  }

  internal fun findPackageScopeFor(lookingFor: ValueParameterDescriptor): MemberScope? {
    val descriptor = findPackageDescriptor(lookingFor)

    if (descriptor != null) {
      val packageDescriptor = descriptor as PackageFragmentDescriptor
      val moduleDescriptor = packageDescriptor.containingDeclaration
      return moduleDescriptor.getPackage(packageDescriptor.fqName).memberScope
    }
    return null
  }

  internal fun findSubpackagesFor(lookingFor: KotlinType): Collection<PackageViewDescriptor> {
    var descriptor = lookingFor.constructor.declarationDescriptor?.containingDeclaration

    while (descriptor !is PackageFragmentDescriptor && descriptor != null) {
      descriptor = descriptor.containingDeclaration
    }

    if (descriptor != null) {
      val packageDescriptor = descriptor as PackageFragmentDescriptor
      val moduleDescriptor = packageDescriptor.containingDeclaration
      val subpackageNames = moduleDescriptor.getSubPackagesOf(packageDescriptor.fqName, { _ -> true })
      return subpackageNames.map { moduleDescriptor.getPackage(it) }
    }
    return emptyList()
  }

  private fun findPackageDescriptor(lookingFor: ValueParameterDescriptor): DeclarationDescriptor? {
    var descriptor: DeclarationDescriptor? = lookingFor.containingDeclaration
    while (descriptor != null && descriptor !is PackageFragmentDescriptor) {
      descriptor = descriptor!!.containingDeclaration
    }
    return descriptor
  }

  internal fun getCompatibleClasses(
    lookingFor: ValueParameterDescriptor,
    scope: MemberScope,
    substitutions: List<TypeSubstitution>,
    lookInSupertypes: Boolean
  ): ExtensionCompatibilityResult {
    val declarations = scope.getContributedDescriptors(DescriptorKindFilter.ALL) { name -> true }
    val candidates = java.util.ArrayList<ClassDescriptor>()
    val newSubstitutions = java.util.ArrayList(substitutions)

    for (descriptor in declarations) {
      if (descriptor is ClassDescriptor) {
        if (descriptor.isExtensionAnnotated) {
          val result = isCompatible(descriptor.defaultType, lookingFor.returnType!!, newSubstitutions, lookInSupertypes)
          if (result.canBeReplaced) {
            candidates.add(descriptor)
            newSubstitutions.addAll(result.substitutions)
          }
        }
      }
    }
    return ExtensionCompatibilityResult(candidates, newSubstitutions)
  }

  private fun isCompatible(
    candidate: KotlinType,
    type: KotlinType,
    substitutions: List<TypeSubstitution>,
    lookInSupertypes: Boolean
  ): SubstitutionResult {
    val result = isReplaceable(candidate, type, substitutions, lookInSupertypes)
    if (result.canBeReplaced) {
      return result
    }
    val supertypes = candidate.constructor.supertypes
    val newSubstitutions = java.util.ArrayList(substitutions)
    for (supertype in supertypes) {
      val result = isReplaceable(supertype, type, newSubstitutions, lookInSupertypes)
      if (!result.canBeReplaced) {
        return SubstitutionResult(false, substitutions)
      } else {
        newSubstitutions.addAll(result.substitutions)
      }
    }
    return SubstitutionResult(true, newSubstitutions)
  }

  internal fun isReplaceable(
    candidate: KotlinType,
    target: KotlinType,
    substitutions: List<TypeSubstitution>,
    lookInSupertypes: Boolean
  ): SubstitutionResult {
    val newSubstitutions = java.util.ArrayList(substitutions)
    if (candidate.memberScope is LazyScopeAdapter) {
      newSubstitutions.add(TypeSubstitution(candidate, target))
      return SubstitutionResult(true, newSubstitutions)
    }

    findEquivalence(candidate, target, substitutions)?.let { (candidate, target) ->
      if (candidate.arguments.size == target.arguments.size) {
        for (i in 0 until candidate.arguments.size) {
          val supertypeArgument = candidate.arguments[i]
          val typeArgument = target.arguments[i]
          val result = isReplaceable(supertypeArgument.type, typeArgument.type, newSubstitutions, lookInSupertypes)
          if (!result.canBeReplaced) {
            return SubstitutionResult(false, substitutions)
          } else {
            newSubstitutions.addAll(result.substitutions)
          }
        }
        return SubstitutionResult(true, newSubstitutions)
      }
    }

    if (lookInSupertypes) {
      for (supertype in target.supertypes()) {
        val result = isReplaceable(candidate, supertype, substitutions, false)
        if (result.canBeReplaced) {
          return result
        }
      }
    }

    return SubstitutionResult(false, substitutions)
  }

  private fun findEquivalence(
    candidate: KotlinType,
    target: KotlinType,
    substitutions: List<TypeSubstitution>
  ): Pair<KotlinType, KotlinType>? {
    if (candidate.constructor.toString().equals(target.constructor.toString())) {
      return candidate to target
    }
    val candidateSubstitution = findSubstitution(candidate, substitutions)
    if ((candidateSubstitution?.constructor?.toString() ?: "").equals(target.constructor.toString())) {
      return candidateSubstitution!! to target
    }
    val targetSubstitution = findSubstitution(target, substitutions)
    if ((targetSubstitution?.constructor?.toString() ?: "").equals(candidate.constructor.toString())) {
      return candidate to targetSubstitution!!
    }

    return null
  }

  private fun findSubstitution(candidate: KotlinType, substitutions: List<TypeSubstitution>): KotlinType? {
    return substitutions
      .reversed()
      .firstOrNull { it.source.equals(candidate) }
      ?.target
  }
}