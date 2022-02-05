package arrow.optics.plugin.internals

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import java.util.Locale

data class ADT(val pckg: KSName, val el: KSClassDeclaration, val targets: List<Target>) {
  val sourceClassName = el.qualifiedName?.asString() ?: el.simpleName
  val sourceName = el.simpleName.asString().replaceFirstChar { it.lowercase(Locale.getDefault()) }
  val simpleName = el.simpleName.asString()
  val packageName = pckg.asString()

  operator fun Snippet.plus(snippet: Snippet): Snippet =
    copy(imports = imports + snippet.imports, content = "$content\n${snippet.content}")
}

enum class OpticsTarget {
  ISO,
  LENS,
  PRISM,
  OPTIONAL,
  DSL
}

typealias IsoTarget = Target.Iso

typealias PrismTarget = Target.Prism

typealias LensTarget = Target.Lens

typealias OptionalTarget = Target.Optional

typealias SealedClassDsl = Target.SealedClassDsl

typealias DataClassDsl = Target.DataClassDsl

sealed class Target {
  abstract val foci: List<Focus>

  data class Iso(override val foci: List<Focus>) : Target()
  data class Prism(override val foci: List<Focus>) : Target()
  data class Lens(override val foci: List<Focus>) : Target()
  data class Optional(override val foci: List<Focus>) : Target()
  data class SealedClassDsl(override val foci: List<Focus>) : Target()
  data class DataClassDsl(override val foci: List<Focus>) : Target()
}

typealias NonNullFocus = Focus.NonNull

typealias OptionFocus = Focus.Option

typealias NullableFocus = Focus.Nullable

sealed class Focus {

  companion object {
    operator fun invoke(fullName: String, paramName: String): Focus =
      when {
        fullName.endsWith("?") -> Nullable(fullName, paramName)
        fullName.startsWith("`arrow`.`core`.`Option`") -> Option(fullName, paramName)
        else -> NonNull(fullName, paramName)
      }
  }

  abstract val className: String
  abstract val paramName: String

  data class Nullable(override val className: String, override val paramName: String) : Focus() {
    val nonNullClassName = className.dropLast(1)
  }

  data class Option(override val className: String, override val paramName: String) : Focus() {
    val nestedClassName =
      Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(className)!!.groupValues[1]
  }

  data class NonNull(override val className: String, override val paramName: String) : Focus()
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
  val content: String
) {
  val fqName = "$`package`.$name"
}

fun Snippet.asFileText(): String =
  """
            |${if (`package`.isNotBlank() && `package` != "`unnamed package`") "package $`package`" else ""}
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
            """.trimMargin()
