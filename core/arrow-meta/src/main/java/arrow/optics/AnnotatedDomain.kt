package arrow.optics

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import javax.lang.model.element.TypeElement

public data class AnnotatedElement(val type: TypeElement, val classData: ClassOrPackageDataWrapper.Class, val targets: List<Target>) {
  public val sourceClassName: String = classData.fullName.escapedClassName
  public val sourceName: String = type.simpleName.toString().decapitalize()
  public val packageName: String = classData.`package`.escapedClassName

  public operator fun Snippet.plus(snippet: Snippet): Snippet = copy(
    imports = imports + snippet.imports,
    content = "$content\n${snippet.content}"
  )
}

public typealias IsoTarget = Target.Iso
public typealias PrismTarget = Target.Prism
public typealias LensTarget = Target.Lens
public typealias OptionalTarget = Target.Optional
public typealias SealedClassDsl = Target.SealedClassDsl
public typealias DataClassDsl = Target.DataClassDsl

public sealed class Target {
  public abstract val foci: List<Focus>

  public data class Iso(override val foci: List<Focus>) : Target()
  public data class Prism(override val foci: List<Focus>) : Target()
  public data class Lens(override val foci: List<Focus>) : Target()
  public data class Optional(override val foci: List<Focus>) : Target()
  public data class SealedClassDsl(override val foci: List<Focus>) : Target()
  public data class DataClassDsl(override val foci: List<Focus>) : Target()
}

public typealias NonNullFocus = Focus.NonNull
public typealias OptionFocus = Focus.Option
public typealias NullableFocus = Focus.Nullable

public sealed class Focus {

  public companion object {
    public operator fun invoke(fullName: String, paramName: String): Focus = when {
      fullName.endsWith("?") -> Nullable(fullName, paramName)
      fullName.startsWith("`arrow`.`core`.`Option`") -> Option(fullName, paramName)
      else -> NonNull(fullName, paramName)
    }
  }

  public abstract val className: String
  public abstract val paramName: String

  public data class Nullable(override val className: String, override val paramName: String) : Focus() {
    public val nonNullClassName: String = className.dropLast(1)
  }

  public data class Option(override val className: String, override val paramName: String) : Focus() {
    public val nestedClassName: String = Regex("`arrow`.`core`.`Option`<(.*)>$").matchEntire(className)!!.groupValues[1]
  }

  public data class NonNull(override val className: String, override val paramName: String) : Focus()
}

public const val Lens: String = "arrow.optics.Lens"
public const val Iso: String = "arrow.optics.Iso"
public const val Optional: String = "arrow.optics.Optional"
public const val Prism: String = "arrow.optics.Prism"
public const val Getter: String = "arrow.optics.Getter"
public const val Setter: String = "arrow.optics.Setter"
public const val Traversal: String = "arrow.optics.Traversal"
public const val Fold: String = "arrow.optics.Fold"
public const val Every: String = "arrow.optics.Every"
public const val Tuple: String = "arrow.core.Tuple"
public const val Pair: String = "kotlin.Pair"
public const val Triple: String = "kotlin.Triple"

public data class Snippet(
  val `package`: String,
  val name: String,
  val imports: Set<String> = emptySet(),
  val content: String
) {
  public val fqName: String = "$`package`.$name"
}

public fun Snippet.asFileText(): String = """
            |${if (`package` != "`unnamed package`") "package $`package`" else ""}
            |${imports.joinToString(prefix = "\n", separator = "\n", postfix = "\n")}
            |$content
            """.trimMargin()
