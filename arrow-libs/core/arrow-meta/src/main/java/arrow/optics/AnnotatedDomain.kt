package arrow.optics

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import javax.lang.model.element.TypeElement

data class AnnotatedElement(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val targets: List<Target>) {
  val sourceClassName = classData.fullName.escapedClassName
  val sourceName = type.simpleName.toString().decapitalize()
  val packageName = classData.`package`.escapedClassName

  operator fun Snippet.plus(snippet: Snippet): Snippet = copy(
    imports = imports + snippet.imports,
    content = "$content\n${snippet.content}"
  )
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

const val Lens = "arrow.optics.Lens"
const val Iso = "arrow.optics.Iso"
const val Optional = "arrow.optics.Optional"
const val Prism = "arrow.optics.Prism"
const val Getter = "arrow.optics.Getter"
const val Setter = "arrow.optics.Setter"
const val Traversal = "arrow.optics.Traversal"
const val Fold = "arrow.optics.Fold"
const val Tuple = "arrow.core.Tuple"

data class Snippet(
  val `package`: String,
  val name: String,
  val imports: Set<String> = emptySet(),
  val content: String
) {
  val fqName = "$`package`.$name"
}

fun Snippet.asFileText(): String = """
            |${if (`package` != "`unnamed package`") "package $`package`" else ""}
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
            """.trimMargin()
