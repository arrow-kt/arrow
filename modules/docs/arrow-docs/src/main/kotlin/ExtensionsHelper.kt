package arrow.reflect

import kotlin.reflect.KClass

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
