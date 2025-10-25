@file:Suppress("ktlint:standard:property-naming")

package arrow.optics.plugin.internals

import arrow.optics.plugin.companionObject
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.symbol.Visibility
import java.util.Locale

data class ADT(val declaration: KSClassDeclaration, val targets: List<Target>) {
  val sourceClassName = declaration.qualifiedNameOrSimpleName
  val sourceName = declaration.simpleName.asString().replaceFirstChar { it.lowercase(Locale.getDefault()) }.sanitize()
  val simpleName = declaration.nameWithParentClass
  val packageName = declaration.packageName.asSanitizedString()
  val visibilityModifierName = when (declaration.companionObject?.getVisibility()) {
    Visibility.INTERNAL -> "internal"
    else -> "public"
  }
  val typeParameters: List<String> = declaration.typeParameters.map { tyParam ->
    if (tyParam.variance == Variance.STAR) return@map "*"
    // val prefix = when (it.variance) {
    //   Variance.COVARIANT, Variance.CONTRAVARIANT -> "${it.variance.label} "
    //   else -> ""
    // }
    val boundNames = tyParam.bounds.mapNotNull {
      it.resolve().qualifiedString().takeIf { it != "kotlin.Any?" }
    }
    val bounds = when (boundNames.count()) {
      0 -> ""
      else -> ": ${boundNames.joinToString()}"
    }
    return@map "${tyParam.simpleName.asString()}$bounds"
  }
  val angledTypeParameters: String = when {
    typeParameters.isEmpty() -> ""
    else -> "<${typeParameters.joinToString(separator = ",")}>"
  }
  val angledTypeParameterNames: String = when {
    typeParameters.isEmpty() -> ""
    else -> "<${declaration.typeParameters.joinToString(separator = ",") { if (it.variance == Variance.STAR) "*" else it.simpleName.asString() } } >"
  }

  operator fun Snippet.plus(snippet: Snippet): Snippet = copy(imports = imports + snippet.imports, content = "$content\n${snippet.content}")
}

@Suppress("RecursivePropertyAccessor")
val KSClassDeclaration.nameWithParentClass: String
  get() = when (val parent = parentDeclaration) {
    is KSClassDeclaration -> parent.nameWithParentClass + "." + simpleName.asString()
    else -> simpleName.asString()
  }

enum class OpticsTarget {
  ISO,
  LENS,
  PRISM,
  DSL,
  COPY,
}

typealias IsoTarget = Target.Iso

typealias PrismTarget = Target.Prism

typealias LensTarget = Target.Lens

typealias SealedClassDsl = Target.SealedClassDsl

typealias DataClassDsl = Target.DataClassDsl

typealias ValueClassDsl = Target.ValueClassDsl

typealias CopyTarget = Target.Copy

sealed class Target {
  sealed class TargetWithFoci : Target() {
    abstract val foci: List<Focus>
  }

  data class Prism(override val foci: List<Focus>) : TargetWithFoci()
  data class Lens(override val foci: List<Focus>) : TargetWithFoci()
  data class Iso(override val foci: List<Focus>) : TargetWithFoci()
  data class SealedClassDsl(override val foci: List<Focus>) : TargetWithFoci()
  data class DataClassDsl(override val foci: List<Focus>) : TargetWithFoci()
  data class ValueClassDsl(val focus: Focus) : TargetWithFoci() {
    override val foci = listOf(focus)
  }

  data class Copy(val companionName: String) : Target()
}

data class Focus(
  val className: String,
  val paramName: String,
  val refinedType: KSType?,
  val onlyOneSealedSubclass: Boolean = false,
  val subclasses: List<String> = emptyList(),
  val classNameWithParameters: String? = className,
) {
  val escapedParamName = paramName.plusIfNotBlank(
    prefix = "`",
    postfix = "`",
  )
  val refinedArguments: List<String>
    get() = refinedType?.arguments?.filter {
      it.type?.resolve()?.declaration is KSTypeParameter
    }?.map {
      it.qualifiedString()
    }.orEmpty()

  companion object {
    operator fun invoke(fullName: String, paramName: String, subclasses: List<String> = emptyList()): Focus = Focus(fullName, paramName, null, subclasses = subclasses)
  }
}

const val Lens = "arrow.optics.Lens"
const val Iso = "arrow.optics.Iso"
const val Optional = "arrow.optics.Optional"
const val Prism = "arrow.optics.Prism"
const val Traversal = "arrow.optics.Traversal"

data class Snippet(
  val `package`: String,
  val name: String,
  val imports: Set<String> = emptySet(),
  val content: String,
)

fun Snippet.asFileText(): String =
  """
            |${if (`package`.isNotBlank() && `package` != "`unnamed package`") "package $`package`" else ""}
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
  """.trimMargin()
