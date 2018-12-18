package arrow.meta.encoder.jvm

import arrow.common.utils.removeBackticks
import arrow.meta.ast.PackageName
import arrow.meta.ast.TypeName
import arrow.meta.encoder.KotlinReservedKeywords

internal fun String.removeVariance(): String =
  replace("out ", "").replace("in ", "")

fun String.asPlatform(): String =
  removeBackticks()
    .replaceFirst("kotlin.collections.", "java.util.")
    .replaceFirst("kotlin.", "java.lang.")

fun String.asKotlin(): String =
  removeBackticks()
    .replace("/", ".")
    .replace("kotlin.jvm.functions", "kotlin")
    .replace("java.util.List", "kotlin.collections.List")
    .replace("java.util.Set", "kotlin.collections.Set")
    .replace("java.util.Map", "kotlin.collections.Map")
    .replace("java.util.SortedMap", "kotlin.collections.SortedMap")
    .replace("java.util.Collection", "kotlin.collections.Collection")
    .replace("java.lang.Throwable", "kotlin.Throwable").let {
      if (it == "java.lang") it.replace("java.lang", "kotlin")
      else it
    }.let {
      if (it == "java.util") it.replace("java.util", "kotlin.collections")
      else it
    }
    .replace("kotlin.Integer", "kotlin.Int")
    .replace("Integer", "Int")
    .replace("java.lang.String", "kotlin.String")

internal fun String.asClassy(): TypeName.Classy {
  val seed = toString().asKotlin()
  val rawTypeName = seed.substringBefore("<")
  val pckg = if (rawTypeName.contains(".")) rawTypeName.substringBeforeLast(".") else ""
  val simpleName = rawTypeName.substringAfterLast(".")
  return TypeName.Classy(simpleName, rawTypeName, PackageName(pckg))
}

private val kindRegex: Regex =
  "(arrow\\.Kind|arrow\\.typeclasses\\.Conested)<(.*), (.*)>".toRegex()

fun String.downKParts(): List<String> =
  when (val matchResult = kindRegex.find(this)) {
    null -> listOf(this)
    else -> {
      val witness = matchResult.groupValues[2]
      val value = matchResult.groupValues[3]
      witness.downKParts() + value.downKParts()
    }
  }

data class DownKindReduction(
  val pckg: String,
  val name: String,
  val additionalTypeArgs: List<String> = emptyList()
)

internal fun String.downKind(): DownKindReduction {
  val parts = downKParts()
  return if (parts.isEmpty()) {
    val pckg =
      if (this.contains(".")) this.substringBefore("<").substringBeforeLast(".")
      else ""
    DownKindReduction(pckg, this)
  } else {
    val dataType = parts[0]
    val pckg =
      if (dataType.contains(".")) dataType.substringBeforeLast(".")
      else ""
    val simpleName = dataType.substringAfterLast(".").substringBefore("<")
    val unAppliedName =
      if (simpleName.startsWith("For")) simpleName.drop("For".length)
      else simpleName
    when {
      unAppliedName.endsWith("PartialOf") ->
        DownKindReduction(pckg, unAppliedName.substringBeforeLast("PartialOf"), parts.drop(1))
      unAppliedName.endsWith("Of") ->
        DownKindReduction(pckg, unAppliedName.substringBeforeLast("Of"), parts.drop(1))
      else -> {
        DownKindReduction(pckg, unAppliedName, parts.drop(1))
      }
    }
  }
}

fun String.quote(): String =
  split(".").joinToString(".") {
    if (KotlinReservedKeywords.contains(it)) "`$it`"
    else it
  }
