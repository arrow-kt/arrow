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

val KSClassDeclaration.nameWithParentClass: String
  get() = when (val parent = parentDeclaration) {
    is KSClassDeclaration -> parent.nameWithParentClass + "." + simpleName.asString()
    else -> simpleName.asString()
  }

enum class OpticsTarget {
  ISO,
  LENS,
  PRISM,
  OPTIONAL,
  DSL,
}

typealias IsoTarget = Target.Iso

typealias PrismTarget = Target.Prism

typealias LensTarget = Target.Lens

typealias OptionalTarget = Target.Optional

typealias SealedClassDsl = Target.SealedClassDsl

typealias DataClassDsl = Target.DataClassDsl

typealias ValueClassDsl = Target.ValueClassDsl

sealed class Target {
  abstract val foci: List<Focus>

  data class Iso(override val foci: List<Focus>) : Target()
  data class Prism(override val foci: List<Focus>) : Target()
  data class Lens(override val foci: List<Focus>) : Target()
  data class Optional(override val foci: List<Focus>) : Target()
  data class SealedClassDsl(override val foci: List<Focus>) : Target()
  data class DataClassDsl(override val foci: List<Focus>) : Target()
  data class ValueClassDsl(val focus: Focus) : Target() {
    override val foci = listOf(focus)
  }
}

typealias NonNullFocus = Focus.NonNull

typealias OptionFocus = Focus.Option

typealias NullableFocus = Focus.Nullable

sealed class Focus {

  companion object {
    operator fun invoke(
      fullName: String,
      paramName: String,
      refinedType: KSType? = null,
      onlyOneSealedSubclass: Boolean = false,
    ): Focus =
      when {
        fullName.endsWith("?") -> Nullable(
          fullName,
          paramName,
          refinedType,
          onlyOneSealedSubclass = onlyOneSealedSubclass,
        )
        fullName.startsWith("`arrow`.`core`.`Option`") -> Option(
          fullName,
          paramName,
          refinedType,
          onlyOneSealedSubclass = onlyOneSealedSubclass,
        )
        else -> NonNull(
          fullName,
          paramName,
          refinedType,
          onlyOneSealedSubclass = onlyOneSealedSubclass,
        )
      }
  }

  abstract val className: String
  abstract val paramName: String

  // only used for type-refining prisms
  abstract val refinedType: KSType?
  abstract val onlyOneSealedSubclass: Boolean

  val refinedArguments: List<String>
    get() = refinedType?.arguments?.filter {
      it.type?.resolve()?.declaration is KSTypeParameter
    }?.map { it.qualifiedString() }.orEmpty()

  data class Nullable(
    override val className: String,
    override val paramName: String,
    override val refinedType: KSType?,
    override val onlyOneSealedSubclass: Boolean,
  ) : Focus() {
    val nonNullClassName = className.dropLast(1)
  }

  data class Option(
    override val className: String,
    override val paramName: String,
    override val refinedType: KSType?,
    override val onlyOneSealedSubclass: Boolean,
  ) : Focus() {
    val nestedClassName =
      Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(className)!!.groupValues[1]
  }

  data class NonNull(
    override val className: String,
    override val paramName: String,
    override val refinedType: KSType?,
    override val onlyOneSealedSubclass: Boolean,
  ) : Focus()
}

const val Lens = "arrow.optics.Lens"
const val Iso = "arrow.optics.Iso"
const val Optional = "arrow.optics.Optional"
const val Prism = "arrow.optics.Prism"
const val Getter = "arrow.optics.Getter"
const val Setter = "arrow.optics.Setter"
const val Traversal = "arrow.optics.Traversal"
const val Fold = "arrow.optics.Fold"
const val Every = "arrow.optics.Every"
const val Tuple = "arrow.core.Tuple"
const val Pair = "kotlin.Pair"
const val Triple = "kotlin.Triple"

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
