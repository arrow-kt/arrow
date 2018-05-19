package arrow.optics

import arrow.common.Package
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import javax.lang.model.element.TypeElement

data class AnnotatedOptic(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val targets: List<Target>) {
  val sourceClassName = classData.fullName.escapedClassName
  val sourceName = type.simpleName.toString().decapitalize()
  val packageName = classData.`package`.escapedClassName
}

inline val Target.targetNames inline get() = foci.map(Focus::className)

sealed class Target {
  abstract val foci: List<Focus>
}

data class IsoOptic(override val foci: List<Focus>) : Target()
data class PrismOptic(override val foci: List<Focus>) : Target()
data class LensOptic(override val foci: List<Focus>) : Target()
data class OptionalOptic(override val foci: List<Focus>) : Target()
data class SealedClassDsl(override val foci: List<Focus>) : Target()
data class DataClassDsl(override val foci: List<Focus>) : Target()

typealias NonNullFocus = Focus.NonNull
typealias OptionFocus = Focus.Option
typealias NullableFocus = Focus.Nullable

sealed class Focus {

  companion object {
    operator fun invoke(fullName: String, paramName: String): Focus = when {
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
    val nestedClassName = Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(className)!!.groupValues[1]
  }

  data class NonNull(override val className: String, override val paramName: String) : Focus()

}

val Lens = "arrow.optics.Lens"
val Iso = "arrow.optics.Iso"
val Optional = "arrow.optics.Optional"
val Prism = "arrow.optics.Prism"
val Getter = "arrow.optics.Getter"
val Setter = "arrow.optics.Setter"
val Traversal = "arrow.optics.Traversal"
val Fold = "arrow.optics.Fold"
val Tuple = "arrow.core.Tuple"

data class Snippet(val imports: Set<String> = emptySet(), val content: String)

operator fun Snippet.plus(snippet: Snippet): Snippet = Snippet(
  this.imports + snippet.imports,
  "${this.content}\n${snippet.content}"
)

fun Snippet.asFileText(packageName: Package): String = """
            |package $packageName
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
            """.trimMargin()