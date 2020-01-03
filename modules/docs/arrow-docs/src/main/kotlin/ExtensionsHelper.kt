package arrow.reflect

import arrow.aql.Box
import arrow.syntax.function.partially2
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions

private val lineSeparator: String = System.getProperty("line.separator")

private val moduleNames: Map<String, String> = mapOf(
  "arrow.aql" to "arrow-aql",
  "arrow.aql.extensions" to "arrow-aql",
  "arrow.core.extensions" to "arrow.core",
  "arrow.core.internal" to "arrow.core",
  "arrow.core" to "arrow-core-data",
  "arrow.typeclasses" to "arrow-core-data",
  "arrow.typeclasses.internal" to "arrow-core-data",
  "arrow.typeclasses.suspended" to "arrow-core-data",
  "arrow.free.extensions" to "arrow-free",
  "arrow.free" to "arrow-free-data",
  "arrow.fx" to "arrow-fx",
  "arrow.fx.extensions" to "arrow-fx",
  "arrow.fx.internal" to "arrow-fx",
  "arrow.fx.typeclasses" to "arrow-fx",
  "arrow.fx.mtl" to "arrow-fx-mtl",
  "arrow.fx.reactor" to "arrow-fx-reactor",
  "arrow.fx.reactor.extensions" to "arrow-fx-reactor",
  "arrow.fx.rx2" to "arrow-fx-rx2",
  "arrow.fx.rx2.extensions" to "arrow-fx-rx2",
  "arrow.mtl.extensions" to "arrow-mtl",
  "arrow.mtl" to "arrow-mtl-data",
  "arrow.mtl.typeclasses" to "arrow-mtl-data",
  "arrow.optics" to "arrow-optics",
  "arrow.optics.dsl" to "arrow-optics",
  "arrow.optics.extensions" to "arrow-optics",
  "arrow.optics.std" to "arrow-optics",
  "arrow.optics.typeclasses" to "arrow-optics",
  "arrow.optics.mtl" to "arrow-optics-mtl",
  "arrow.recursion.extensions" to "arrow-recursion",
  "arrow.recursion.data" to "arrow-recursion-data",
  "arrow.recursion.pattern" to "arrow-recursion-data",
  "arrow.recursion.typeclasses" to "arrow-recursion-data",
  "arrow.reflect" to "arrow-reflect",
  "arrow.streams" to "arrow-streams",
  "arrow.streams.internal" to "arrow-streams",
  "arrow.ui.extensions" to "arrow-ui",
  "arrow.ui" to "arrow-ui-data",
  "arrow.validation.refinedTypes" to "arrow-validation",
  "arrow.validation.refinedTypes.bool" to "arrow-validation",
  "arrow.validation.refinedTypes.generic" to "arrow-validation",
  "arrow.validation.refinedTypes.numeric" to "arrow-validation"
)

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
            transform = TypeClass::docsMarkdownLink.partially2(moduleNames[entry.key]).partially2(entry.key)
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
            transform = DataType::docsMarkdownLink.partially2(moduleNames[entry.key]).partially2(entry.key)
          ) + "|"
      }.joinToString(lineSeparator)

fun TypeClass.docsMarkdownLink(moduleName: String?, packageName: String): String =
  kclass.docsMarkdownLink(moduleName, packageName)

fun DataType.docsMarkdownLink(moduleName: String?, packageName: String): String =
  kclass.docsMarkdownLink(moduleName, packageName)

fun String.toKebabCase(): String {
  var text: String = ""
  this.forEach {
    if (it.isUpperCase()) {
      text += "-"
      text += it.toLowerCase()
    } else {
      text += it
    }
  }
  return text
}

fun <A : Any> KClass<A>.docsMarkdownLink(moduleName: String?, packageName: String): String =
  "[$simpleName]({{ '/docs/apidocs/$moduleName/${packageName.toKebabCase()}/${simpleName?.toKebabCase()}' | relative_url }})"

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
