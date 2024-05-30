@file:Suppress("ktlint:standard:property-naming")

package arrow.optics.plugin.internals

import arrow.optics.plugin.companionObject
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Visibility
import java.util.Locale

data class ADT(val pckg: KSName, val declaration: KSClassDeclaration, val targets: List<Target>) {
  val sourceClassName = declaration.qualifiedNameOrSimpleName
  val sourceName = declaration.simpleName.asString().replaceFirstChar { it.lowercase(Locale.getDefault()) }
  val simpleName = declaration.nameWithParentClass
  val packageName = pckg.asSanitizedString()
  val visibilityModifierName = when (declaration.companionObject?.getVisibility()) {
    Visibility.INTERNAL -> "internal"
    else -> "public"
  }
  val typeParameters: List<String> = declaration.typeParameters.map { it.simpleName.asString() }
  val angledTypeParameters: String = when {
    typeParameters.isEmpty() -> ""
    else -> "<${typeParameters.joinToString(separator = ",")}>"
  }

  operator fun Snippet.plus(snippet: Snippet): Snippet =
    copy(imports = imports + snippet.imports, content = "$content\n${snippet.content}")
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
}

typealias IsoTarget = Target.Iso

typealias PrismTarget = Target.Prism

typealias LensTarget = Target.Lens

typealias SealedClassDsl = Target.SealedClassDsl

typealias DataClassDsl = Target.DataClassDsl

typealias ValueClassDsl = Target.ValueClassDsl

sealed class Target {
  abstract val foci: List<Focus>

  data class Prism(override val foci: List<Focus>) : Target()
  data class Lens(override val foci: List<Focus>) : Target()
  data class Iso(override val foci: List<Focus>) : Target()
  data class SealedClassDsl(override val foci: List<Focus>) : Target()
  data class DataClassDsl(override val foci: List<Focus>) : Target()
  data class ValueClassDsl(val focus: Focus) : Target() {
    override val foci = listOf(focus)
  }
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
    }?.map { it.qualifiedString() }.orEmpty()

  companion object {
    operator fun invoke(fullName: String, paramName: String, subclasses: List<String> = emptyList()): Focus =
      Focus(fullName, paramName, null, subclasses = subclasses)
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
) {
  val fqName = "$`package`.$name"
}

fun Snippet.asFileText(): String =
  """
            |${if (`package`.isNotBlank() && `package` != "`unnamed package`") "package $`package`" else ""}
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
  """.trimMargin()
