package arrow.optics

import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
import me.eugeniomarletti.kotlin.metadata.escapedClassName
import java.io.File

class BoundSetterGenerator(
  private val annotatedList: Collection<AnnotatedOptic>,
  private val generatedDir: File
) {

  private val filePrefix = "optics.syntax"
  private val boundSetter = "arrow.optics.syntax.BoundSetter"

  fun generate() = annotatedList.map(this::processElement)
    .map { (element, funs) ->
      "$filePrefix.${element.classData.`package`}.${element.type.simpleName.toString().toLowerCase()}.kt" to
        funs.joinToString(prefix = "${fileHeader(element.classData.`package`.escapedClassName)}\n", separator = "\n")
    }.forEach { (name, fileString) -> File(generatedDir, name).writeText(fileString) }

  private fun String.toUpperCamelCase(): String = split(" ").joinToString("", transform = String::capitalize)

  private fun processElement(annotatedOptic: AnnotatedOptic): Pair<AnnotatedOptic, List<String>> =
    annotatedOptic to listOf(createDslFunction(annotatedOptic)) + annotatedOptic.targets.map { variable ->
      val sourceClassName = annotatedOptic.classData.fullName.escapedClassName
      val sourceName = annotatedOptic.type.simpleName.toString().decapitalize()
      val targetClassName = variable.fullName
      val targetName = variable.paramName

      when (variable) {
        is Target.NullableTarget -> processBoundSetter(sourceClassName, targetName, variable.nonNullFullName, sourceName)
        is Target.OptionTarget -> processBoundSetter(sourceClassName, targetName, variable.nestedFullName, sourceName)
        is Target.NonNullTarget -> processBoundSetter(sourceClassName, targetName, targetClassName, sourceName)
      }
    }

  private fun fileHeader(packageName: String): String =
    """package $packageName.syntax
               |
               |import $packageName.*
               |""".trimMargin()

  fun createDslFunction(annotatedOptic: AnnotatedOptic): String = """
        |/**
        | * @receiver [${annotatedOptic.sourceClassName.removeBackticks()}] the instance you want to bind the dsl on.
        | * @return [$boundSetter] an intermediate optics that is bound to the instance.
        | */
        |fun ${annotatedOptic.sourceClassName}.setter() = $boundSetter(this, arrow.optics.PSetter.id())
        |""".trimMargin()

  fun processBoundSetter(sourceClassName: String, targetName: String, targetClassName: String, sourceName: String) = """
      |inline val <T> $boundSetter<T, $sourceClassName>.$targetName: $boundSetter<T, $targetClassName>
      |    get() = this.compose($sourceName${targetName.toUpperCamelCase()}())
      |""".trimMargin()

}
