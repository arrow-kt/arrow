package arrow.reflect

import kotlin.reflect.KClass

private val lineSeparator: String = "\n" //System.getProperty("line.separator")

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
    .groupBy { it.kclass.java.canonicalName.substringBeforeLast(".") }
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

fun <A: Any> KClass<A>.docsMarkdownLink(): String =
  "[$simpleName]({{ '/docs/${qualifiedName?.toLowerCase()?.replace(".", "/")}' | relative_url }})"

fun TypeClass.hierarchyGraphScript(): String =
  """
    <script>
        ${hierarchyGraph()}
        var canvas = document.getElementById('${classInfo.simpleName}-hierarchy-canvas');
        nomnoml.draw(canvas, graph);
    </script>
  """.trimIndent()

fun TypeClass.hierarchyGraph(): String =
  """
    |#font: monoidregular
    |#arrowSize: 1
    |#bendSize: 0.3
    |#direction: right
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
    |#title: ${classInfo.simpleName}
    |#zoom: 1
    |#.typeclass: fill=#FFFFFF visual=class bold
    |${nomnomlMethods()}
    |${nomnomlHierarchy()}
  """.trimMargin()

fun TypeClass.nomnomlMethods(): String =
  "[<typeclass>${classInfo.simpleName}|${declaredMethodNamesAndTypes().joinToString("|"){ "${it.a}: ${it.b.joinToString(" -> ")}" }}]"

fun TypeClass.nomnomlBlock(): String =
  "[<typeclass>${classInfo.simpleName}]"

fun TypeClass.nomnomlExtends(other: TypeClass): String =
  "${other.nomnomlBlock()}<-${nomnomlBlock()}"

fun TypeClass.nomnomlHierarchy(): String =
  (listOf(this) + hierarchy())
    .zipWithNext(TypeClass::nomnomlExtends)
    .joinToString(lineSeparator)

fun TypeClass.nomnomlExtensions(): String =
  "${nomnomlBlock()}<-[${classInfo.simpleName} @extension|${extensions().joinToString("|") { it.instance.kclass.java.simpleName }}]"
