package arrow.meta.plugins.higherkind

import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.getAbbreviation
import org.jetbrains.kotlin.types.getSupertypeRepresentative
import org.jetbrains.kotlin.types.typeUtil.getImmediateSuperclassNotAny
import org.jetbrains.kotlin.types.typeUtil.supertypes
import java.lang.IllegalStateException

class KindAwareTypeChecker(val typeChecker: KotlinTypeChecker) : KotlinTypeChecker by typeChecker {

  fun KotlinType.isKindReference(other: KotlinType): Boolean {
    return arguments.isNotEmpty() &&
      other.arguments.isNotEmpty() &&
      (kindRegex().matches(other.toString()) ||
        kindErrorRegex().matches(other.toString()) ||
        kindAliasRegex().matches(other.toString()))
  }

  private fun KotlinType.kindAliasRegex() =
    "${constructor.declarationDescriptor?.name}Of<${arguments.joinToString(", ")}>".toRegex()

  private fun KotlinType.kindErrorRegex() =
    "Kind<\\[ERROR : For${constructor.declarationDescriptor?.name}], ${arguments.joinToString(", ")}>".toRegex()

  private fun KotlinType.kindRegex() =
    "Kind<For${constructor.declarationDescriptor?.name}, ${arguments.joinToString(", ")}>".toRegex()

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
    val underlyingResult = typeChecker.isSubtypeOf(p0, p1)
    return if (!underlyingResult) {
      val subType = p0
      val superType = p1
      val isKind: Boolean = subType.isKindReference(superType) || superType.isKindReference(subType)
      val result = isKind
      //println("KindAwareTypeChecker.isSubtypeOf: $p0 <-> $p1 = $result, subtype supertypes: ${subType.supertypes()}")
      result
    } else underlyingResult
  }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
    //println("KindAwareTypeChecker.equalTypes: $p0 <-> $p1")
    val result = typeChecker.equalTypes(p0, p1)
    //println("KindAwareTypeChecker.equalTypes: $p0 <-> $p1 = $result")
    return result
  }

  private fun KotlinType.isKind(): Boolean =
    constructor.declarationDescriptor?.fqNameSafe == kindName

  private fun KotlinType.typeAliasMatch(other: KotlinType): Boolean =
    try {
      val a = getAbbreviation()?.constructor?.declarationDescriptor?.fqNameSafe?.shortName()
      val b = other.constructor.declarationDescriptor?.fqNameSafe?.shortName()
        ?: other.getImmediateSuperclassNotAny()?.constructor?.declarationDescriptor?.fqNameSafe?.kindTypeAliasName
      a == b || a == other.getImmediateSuperclassNotAny()?.constructor?.declarationDescriptor?.fqNameSafe?.kindTypeAliasName
    } catch (e: IllegalStateException) { //rarely shortName throws
      false
    }

}
