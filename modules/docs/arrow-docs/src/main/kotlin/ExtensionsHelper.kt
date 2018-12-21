package arrow.reflect

import arrow.aql.Box
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions

private val lineSeparator: String = System.getProperty("line.separator")

/**
 * @return a list of [TypeClass] supported by this [DataType]
 */
fun DataType.tcMarkdownList(): String =
  "| Module | Type classes |$lineSeparator" +
    supportedTypeClasses()
      .asSequence()
      .groupBy { it.kclass.java.canonicalName.substringBeforeLast(".") }
      .toSortedMap()
      .map { entry ->
        "|__${entry.key}__|" +
          entry.value.joinToString(
            separator = ", ",
            transform = TypeClass::docsMarkdownLink
          ) + "|"
      }.joinToString(lineSeparator)

/**
 * @return a list of [TypeClass] supported by this [DataType]
 */
fun TypeClass.dtMarkdownList(): String =
  "| Module | Data types |$lineSeparator" +
    supportedDataTypes()
      .asSequence()
      .filterNot { it.kclass == Box::class }
      .groupBy { it.kclass.java.`package`.name }
      .toSortedMap()
      .map { entry ->
        "|__${entry.key}__|" +
          entry.value.joinToString(
            separator = ", ",
            transform = DataType::docsMarkdownLink
          ) + "|"
      }.joinToString(lineSeparator)

fun TypeClass.docsMarkdownLink(): String =
  kclass.docsMarkdownLink()

fun DataType.docsMarkdownLink(): String =
  kclass.docsMarkdownLink()

fun <A : Any> KClass<A>.docsMarkdownLink(): String =
  "[$simpleName]({{ '/docs/${qualifiedName?.toLowerCase()?.replace(".", "/")}' | relative_url }})"

fun TypeClass.hierarchyGraph(): String =
  """
    |#font: monoidregular
    |#arrowSize: 1
    |#bendSize: 0.3
    |#direction: down
    |#gutter: 5
    |#edgeMargin: 0
    |#edges: rounded
    |#fillArrows: false
    |#fontSize: 10
    |#leading: 1.25
    |#lineWidth: 1
    |#padding: 8
    |#spacing: 40
    |#stroke: #485C8A
    |#title: ${kclass.simpleName}
    |#zoom: 1
    |#.typeclass: fill=#FFFFFF visual=class bold
    |${nomnomlMethods()}
    |${nomnomlHierarchy()}
  """.trimMargin()

fun TypeClass.nomnomlMethods(): String =
  "[<typeclass>${kclass.simpleName}|${kclass.declaredFunctions.groupBy { it.name }.toList().joinToString("|") {
    if (it.second.size > 1) "${it.first}(${it.second.size})"
    else it.first
  }}]"

fun TypeClass.nomnomlBlock(): String =
  "[<typeclass>${kclass.simpleName}]"

fun Extends.nomnomlExtends(): String =
  "${b.nomnomlBlock()}<-${a.nomnomlBlock()}"

fun TypeClass.nomnomlHierarchy(): String =
  hierarchy().joinToString(lineSeparator, transform = Extends::nomnomlExtends)

fun TypeClass.nomnomlExtensions(): String =
  "${nomnomlBlock()}<-[${kclass.simpleName} @extension|${extensions().joinToString("|") { it.instance.kclass.java.simpleName }}]"

fun List<TypeClass>.nomnomlMixedHierarchyGraph(): String =
  flatMap { it.hierarchy() }
    .distinct()
    .joinToString(lineSeparator, transform = Extends::nomnomlExtends)

fun List<TypeClass>.mixedHierarchyGraph(): String = """
  |#font: monoidregular
  |#arrowSize: 1
  |#bendSize: 0.3
  |#direction: down
  |#gutter: 5
  |#edgeMargin: 0
  |#edges: rounded
  |#fillArrows: false
  |#fontSize: 10
  |#leading: 1.25
  |#lineWidth: 1
  |#padding: 8
  |#spacing: 40
  |#stroke: #485C8A
  |#zoom: 1
  |#.typeclass: fill=#FFFFFF visual=class bold
  |#.selected: fill=#61A8FF visual=class bold
  |${nomnomlMixedHierarchyGraph()}
""".trimMargin()
