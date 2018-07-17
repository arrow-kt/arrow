package arrow.optics

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import arrow.optics.Optic.*
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

typealias AnnotatedSumType = AnnotatedType.AnnotatedClass.Sum
typealias AnnotatedProductType = AnnotatedType.AnnotatedClass.Product
typealias AnnotatedFunctionType = AnnotatedType.Function

sealed class AnnotatedType {

  abstract val element: Element
  abstract val `package`: String

  sealed class AnnotatedClass : AnnotatedType() {
    abstract val classData: ClassOrPackageDataWrapper.Class
    abstract val foci: List<Focus>

    inline val sourceClassName inline get() = classData.fullName.escapedClassName
    inline val sourceName inline get() = element.simpleName.toString().decapitalize()
    inline val packageName inline get() = classData.`package`.escapedClassName

    override val `package`: String get() = classData.`package`

    data class Sum(override val element: TypeElement, override val classData: ClassOrPackageDataWrapper.Class, override val foci: List<Focus>) : AnnotatedClass()

    data class Product(override val element: TypeElement, override val classData: ClassOrPackageDataWrapper.Class, override val foci: List<Focus>) : AnnotatedClass()
  }

  data class Function(override val element: ExecutableElement, val dslElement: DslElement) : AnnotatedType() {
    override val `package`: String get() = dslElement.`package`
  }

}

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

sealed class Optic {

  object Lens : Optic() {
    override fun toString() = "arrow.optics.Lens"
  }

  object Iso : Optic() {
    override fun toString() = "arrow.optics.Iso"
  }

  object Optional : Optic() {
    override fun toString() = "arrow.optics.Optional"
  }

  object Prism : Optic() {
    override fun toString() = "arrow.optics.Prism"
  }

  object Getter : Optic() {
    override fun toString() = "arrow.optics.Getter"
  }

  object Setter : Optic() {
    override fun toString() = "arrow.optics.Setter"
  }

  object Traversal : Optic() {
    override fun toString() = "arrow.optics.Traversal"
  }

  object Fold : Optic() {
    override fun toString() = "arrow.optics.Fold"
  }

  companion object {
    val values = listOf(Lens, Iso, Optional, Prism, Getter, Setter, Traversal, Fold)
  }
}

sealed class POptic {
  fun monomorphic(): Optic = when (this) {
    PLens -> Lens
    PIso -> Iso
    POptional -> Optional
    PPrism -> Prism
    PSetter -> Setter
    PTraversal -> Traversal
  }

  object PLens : POptic() {
    override fun toString() = "arrow.optics.PLens"
  }

  object PIso : POptic() {
    override fun toString() = "arrow.optics.PIso"
  }

  object POptional : POptic() {
    override fun toString() = "arrow.optics.POptional"
  }

  object PPrism : POptic() {
    override fun toString() = "arrow.optics.PPrism"
  }

  object PSetter : POptic() {
    override fun toString() = "arrow.optics.PSetter"
  }

  object PTraversal : POptic() {
    override fun toString() = "arrow.optics.PTraversal"
  }

  companion object {
    val values = listOf(PLens, PIso, POptional, PPrism, PSetter, PTraversal)
  }
}

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
            |package $`package`
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
            """.trimMargin()