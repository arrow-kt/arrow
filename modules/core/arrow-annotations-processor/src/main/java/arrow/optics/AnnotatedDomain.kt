package arrow.optics

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import javax.lang.model.element.TypeElement

data class AnnotatedOptic(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class) {
  val sourceClassName = classData.fullName.escapedClassName
  val sourceName = type.simpleName.toString().decapitalize()
  val packageName = "package ${classData.`package`.escapedClassName}"
}

sealed class Target {
  abstract val foci: List<Focus>

  val targetNames = foci.map(Focus::fullName)
  val focusSize: Int = foci.size
}

data class IsoOptic(override val foci: List<Focus>) : Target()
data class PrismOptic(override val foci: List<Focus>) : Target()
data class LensOptic(override val foci: List<Focus>) : Target()
data class OptionalOptic(override val foci: List<Focus>) : Target()

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

  abstract val fullName: String
  abstract val paramName: String

  data class Nullable(override val fullName: String, override val paramName: String) : Focus() {
    val nonNullFullName = fullName.dropLast(1)
  }

  data class Option(override val fullName: String, override val paramName: String) : Focus() {
    val nestedFullName = Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(fullName)!!.groupValues[1]
  }

  data class NonNull(override val fullName: String, override val paramName: String) : Focus()

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
