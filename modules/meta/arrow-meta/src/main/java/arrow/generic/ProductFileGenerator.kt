package arrow.generic

import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

sealed class DerivedTypeClass(val type: String)
object Semigroup : DerivedTypeClass("arrow.typeclasses.Semigroup")
object Monoid : DerivedTypeClass("arrow.typeclasses.Monoid")
object Eq : DerivedTypeClass("arrow.typeclasses.Eq")
object Order : DerivedTypeClass("arrow.typeclasses.Order")
object Show : DerivedTypeClass("arrow.typeclasses.Show")
object Hash : DerivedTypeClass("arrow.typeclasses.Hash")

class ProductFileGenerator(
  private val annotatedList: Collection<AnnotatedGeneric>,
  private val generatedDir: File
) {

  private val tuple = "arrow.core.Tuple"
  private val hlist = "arrow.generic.HList"
  private val letters = ('a'..'j').toList()

  fun generate() {
    buildProduct(annotatedList)
    // buildInstances(annotatedList)
  }

  private fun buildProduct(products: Collection<AnnotatedGeneric>) =
    products.map(this::processElement)
      .forEach { (element, funString) ->
        File(generatedDir, "${productAnnotationClass.simpleName}.${element.classData.`package`}.${element.sourceName}.kt").printWriter().use { w ->
          w.println(funString)
        }
      }

  private fun buildInstances(products: Collection<AnnotatedGeneric>) =
    products.map { p -> processInstancesForElement(p) }
      .forEach { (element, funString) ->
        File(generatedDir, "${productAnnotationClass.simpleName}.${element.classData.`package`}.${element.sourceName}.instances.kt").printWriter().use { w ->
          w.println(funString)
        }
      }

  private fun processInstancesForElement(product: AnnotatedGeneric): Pair<AnnotatedGeneric, String> = product to """
            |package arrow.core
            |
            |${semigroupTupleNInstance(product)}
            |${monoidTupleNInstance(product)}
            |${eqTupleNInstance(product)}
            |${orderTupleNInstance(product)}
            |${showTupleNInstance(product)}
            |
            |""".trimMargin()

  private fun semigroupExtensions(product: AnnotatedGeneric): String =
    """|
                |fun ${product.sourceClassName}.combine(other: ${product.sourceClassName}): ${product.sourceClassName} =
                |  this + other
                |
                |fun List<${product.sourceClassName}>.combineAll(): ${product.sourceClassName} =
                |  this.reduce { a, b -> a + b }
                |
                |operator fun ${product.sourceClassName}.plus(other: ${product.sourceClassName}): ${product.sourceClassName} =
                |  with(${product.sourceClassName}.semigroup()) { this@plus.combine(other) }
                |""".trimMargin()

  private fun monoidExtensions(product: AnnotatedGeneric): String =
    """|fun empty${product.sourceSimpleName}(): ${product.sourceClassName} =
                |  ${product.sourceClassName}.monoid().empty()
                |""".trimMargin()

  private fun tupledExtensions(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |fun ${product.sourceClassName}.tupled(): ${focusType(product)} =
                | ${tupleConstructor(product)}
                |
                |fun ${product.sourceClassName}.tupledLabeled(): ${labeledFocusType(product)} =
                |  ${product.targets.joinToString(prefix = "arrow.core.Tuple${product.focusSize}(", postfix = ")") { """("${it.paramName}" toT ${it.paramName})""" }}
                |
                |fun <B> ${product.sourceClassName}.foldLabeled(f: ${product.targetNames.joinToString(prefix = "(", separator = ", ", postfix = ")") { "arrow.core.Tuple2<kotlin.String, $it>" }} -> B): B {
                |  val t = tupledLabeled()
                |  return f(${(0 until product.focusSize).joinToString(", ") { "t.${letters[it]}" }})
                |}
                |
                |fun ${focusType(product)}.to${product.sourceSimpleName}(): ${product.sourceClassName} =
                |  ${classConstructorFromTuple(product.sourceClassName, product.focusSize)}
                |""".trimMargin()
    else ""

  private fun hListExtensions(product: AnnotatedGeneric): String =
    """|
                |fun ${product.sourceClassName}.toHList(): ${focusHListType(product)} =
                |  ${hListConstructor(product)}
                |
                |fun ${focusHListType(product)}.to${product.sourceSimpleName}(): ${product.sourceClassName} =
                |  ${classConstructorFromHList(product.sourceClassName, product.focusSize)}
                |
                |fun ${product.sourceClassName}.toHListLabeled(): ${labeledHListFocusType(product)} =
                |  ${product.targets.joinToString(prefix = "arrow.generic.hListOf(", postfix = ")") { """("${it.paramName}" toT ${it.paramName})""" }}
                |""".trimMargin()

  private fun applicativeExtensions(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |fun <F> arrow.typeclasses.Applicative<F>.mapTo${product.sourceSimpleName}${kindedProperties("F", product)}: arrow.Kind<F, ${product.sourceClassName}> =
                |    this.map(${product.targets.joinToString(", ") { it.paramName }}, { it.to${product.sourceSimpleName}() })
                |
                |""".trimMargin()
    else ""

  private fun semigroupInstance(product: AnnotatedGeneric): String =
    """|
                |interface ${product.sourceSimpleName}Semigroup : arrow.typeclasses.Semigroup<${product.sourceClassName}> {
                |  override fun ${product.sourceClassName}.combine(b: ${product.sourceClassName}): ${product.sourceClassName} {
                |    val (${product.types().joinToString(", ") { "x$it" }}) = this
                |    val (${product.types().joinToString(", ") { "y$it" }}) = b
                |    return ${product.sourceClassName}(${product.types().zip(product.targetNames).joinToString(", ") { "with(${it.second.companionFromType()}.semigroup${it.second.typeArg()}(${it.second.instance(product, "${product.sourceSimpleName}Semigroup", "semigroup()")})){ x${it.first}.combine(y${it.first}) }" }})
                |  }
                |
                |  companion object {
                |    val defaultInstance : arrow.typeclasses.Semigroup<${product.sourceClassName}> =
                |      object : ${product.sourceSimpleName}Semigroup{}
                |  }
                |}
                |
                |fun ${product.sourceClassName}.Companion.semigroup(): ${Semigroup.type}<${product.sourceClassName}> =
                |  ${product.sourceSimpleName}Semigroup.defaultInstance
                |""".trimMargin()

  private fun monoidInstance(product: AnnotatedGeneric): String =
    """|
                |interface ${product.sourceSimpleName}Monoid: arrow.typeclasses.Monoid<${product.sourceClassName}>, ${product.sourceSimpleName}Semigroup {
                |  override fun empty(): ${product.sourceClassName} =
                |    ${product.sourceClassName}(${product.types().zip(product.targetNames).joinToString(", ") { "with(${it.second.companionFromType()}.monoid${it.second.typeArg()}(${it.second.instance(product, "${product.sourceSimpleName}Monoid", "monoid()")})){ empty() }" }})
                |
                |    companion object {
                |       val defaultInstance : arrow.typeclasses.Monoid<${product.sourceClassName}> =
                |           object : ${product.sourceSimpleName}Monoid{}
                |    }
                |}
                |
                |fun ${product.sourceClassName}.Companion.monoid(): ${Monoid.type}<${product.sourceClassName}> =
                |  ${product.sourceSimpleName}Monoid.defaultInstance
                |""".trimMargin()

  private fun eqInstance(product: AnnotatedGeneric): String =
    """|
                |interface ${product.sourceSimpleName}Eq : arrow.typeclasses.Eq<${product.sourceClassName}> {
                |  override fun ${product.sourceClassName}.eqv(b: ${product.sourceClassName}): Boolean =
                |    this == b
                |
                |  companion object {
                |    val defaultInstance : arrow.typeclasses.Eq<${product.sourceClassName}> =
                |           object : ${product.sourceSimpleName}Eq{}
                |  }
                |}
                |
                |fun ${product.sourceClassName}.Companion.eq(): ${Eq.type}<${product.sourceClassName}> =
                |  ${product.sourceSimpleName}Eq.defaultInstance
                |""".trimMargin()

  // TODO instance imports are hardcoded
  private fun processElement(product: AnnotatedGeneric): Pair<AnnotatedGeneric, String> = product to """
            |${if (product.classData.`package`.escapedClassName != "`unnamed package`") "package ${product.classData.`package`.escapedClassName}" else ""}
            |
            |import arrow.typeclasses.*
            |import arrow.core.*
            |import arrow.core.extensions.*
            |import arrow.core.extensions.option.monoid.monoid
            |import arrow.core.extensions.option.semigroup.semigroup
            |
            |${semigroupExtensions(product)}
            |
            |${monoidExtensions(product)}
            |
            |${tupledExtensions(product)}
            |
            |${hListExtensions(product)}
            |
            |${applicativeExtensions(product)}
            |
            |${semigroupInstance(product)}
            |
            |${monoidInstance(product)}
            |
            |${eqInstance(product)}
            |
            |interface ${product.sourceSimpleName}Show : arrow.typeclasses.Show<${product.sourceClassName}> {
            |  override fun ${product.sourceClassName}.show(): String =
            |    this.toString()
            |}
            |
            |fun ${product.sourceClassName}.Companion.show(): ${Show.type}<${product.sourceClassName}> =
            |  object : ${product.sourceSimpleName}Show{}
            |
            |""".trimMargin()

  private fun String.companionFromType(): String =
    substringBefore("<")

  private fun String.typeArg(): String =
    if (contains("<"))
      "<${substringAfter("<").substringBeforeLast(">")}>"
    else ""

  private fun String.instance(product: AnnotatedGeneric, instance: String, factory: String): String =
    if (contains("<")) {
      val type = typeArg().removeSurrounding("<", ">")
      if (product.sourceClassName == type) "this@$instance"
      else "$type.$factory"
    } else ""

  private fun tupleConstructor(product: AnnotatedGeneric): String =
    product.targets.joinToString(prefix = "$tuple${product.focusSize}(", postfix = ")", transform = { "this.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

  private fun hListConstructor(product: AnnotatedGeneric): String =
    product.targets.joinToString(prefix = "arrow.generic.hListOf(", postfix = ")", transform = { "this.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

  private fun labeledFocusType(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus) product.targetNames.map { "${tuple}2<String, $it>" }.joinToString(prefix = "$tuple${product.targets.size}<", postfix = ">")
    else product.targetNames.first()

  private fun focusType(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus) product.targetNames.joinToString(prefix = "$tuple${product.targets.size}<", postfix = ">")
    else product.targetNames.first()

  private fun focusHListType(product: AnnotatedGeneric): String =
    product.targetNames.joinToString(prefix = "$hlist${product.targets.size}<", postfix = ">")

  private fun labeledHListFocusType(product: AnnotatedGeneric): String =
    product.targetNames.map { "${tuple}2<String, $it>" }.joinToString(prefix = "$hlist${product.targets.size}<", postfix = ">")

  private fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int): String =
    (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "this.${letters[it]}" })

  private fun classConstructorFromHList(sourceClassName: String, propertiesSize: Int): String =
    (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = {
      "this." + (0 until it).fold("") { acc, _ ->
        acc + "tail."
      } + "head"
    })

  private fun kindedProperties(prefix: String, product: AnnotatedGeneric): String =
    product.targets.map { it.paramName }.zip(product.targetNames).joinToString(
      prefix = "(",
      separator = ", ",
      transform = { "${it.first}: arrow.Kind<$prefix, ${it.second}>" },
      postfix = ")"
    )

  private fun AnnotatedGeneric.types(): List<String> =
    (0 until focusSize).toList().map { letters[it].toString().capitalize() }

  private fun AnnotatedGeneric.arityInstanceProviders(tc: DerivedTypeClass): String =
    types().joinToString("\n  ") { "fun ${tc.type.substringAfterLast(".")[0]}$it(): ${tc.type}<$it>" }

  private fun AnnotatedGeneric.arityInstanceInjections(tc: DerivedTypeClass): String =
    types().joinToString(", ") { "${tc.type.substringAfterLast(".")[0]}$it: ${tc.type}<$it>" }

  private fun AnnotatedGeneric.arityInstanceImplementations(tc: DerivedTypeClass): String =
    types().joinToString("\n      ") { "override fun ${tc.type.substringAfterLast(".")[0]}$it(): ${tc.type}<$it> = ${tc.type.substringAfterLast(".")[0]}$it" }

  private fun AnnotatedGeneric.expandedTypeArgs(): String =
    types().joinToString(", ")

  private fun AnnotatedGeneric.isRecursive(name: String): Boolean =
    sourceClassName in name

  private fun semigroupTupleNInstance(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |interface Tuple${product.focusSize}Semigroup<${product.expandedTypeArgs()}>
                | : ${Semigroup.type}<arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>> {
                |
                |  ${product.arityInstanceProviders(Semigroup)}
                |
                |  override fun combine(
                |    a: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>,
                |    b: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>
                |  ): arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}> {
                |    val (${product.types().joinToString(", ") { "x$it" }}) = a
                |    val (${product.types().joinToString(", ") { "y$it" }}) = b
                |    return arrow.core.Tuple${product.focusSize}(${product.types().joinToString(", ") { "S$it().combine(x$it, y$it)" }})
                |  }
                |}
                |
                |object Tuple${product.focusSize}SemigroupInstanceImplicits {
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Semigroup)}): Tuple${product.focusSize}Semigroup<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}Semigroup<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Semigroup)}
                |    }
                |}
                |""".trimMargin()
    else ""

  private fun monoidTupleNInstance(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |interface Tuple${product.focusSize}Monoid<${product.expandedTypeArgs()}>
                | : ${Monoid.type}<arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>> {
                |
                |  ${product.arityInstanceProviders(Monoid)}
                |
                |  override fun combine(
                |    a: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>,
                |    b: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>
                |  ): arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}> {
                |    val (${product.types().joinToString(", ") { "x$it" }}) = a
                |    val (${product.types().joinToString(", ") { "y$it" }}) = b
                |    return arrow.core.Tuple${product.focusSize}(${product.types().joinToString(", ") { "M$it().combine(x$it, y$it)" }})
                |  }
                |
                |  override fun empty(): arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}> =
                |    arrow.core.Tuple${product.focusSize}(${product.types().joinToString(", ") { "M$it().empty()" }})
                |
                |}
                |
                |object Tuple${product.focusSize}MonoidInstanceImplicits {
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Monoid)}): Tuple${product.focusSize}Monoid<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}Monoid<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Monoid)}
                |    }
                |}
                |""".trimMargin()
    else ""

  private fun eqTupleNInstance(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |interface Tuple${product.focusSize}Eq<${product.expandedTypeArgs()}>
                | : ${Eq.type}<arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>> {
                |
                |  ${product.arityInstanceProviders(Eq)}
                |
                |  override fun eqv(
                |    a: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>,
                |    b: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>
                |  ): Boolean {
                |    val (${product.types().joinToString(", ") { "x$it" }}) = a
                |    val (${product.types().joinToString(", ") { "y$it" }}) = b
                |    return ${product.types().joinToString(" && ") { "E$it().eqv(x$it, y$it)" }}
                |  }
                |}
                |
                |object Tuple${product.focusSize}EqInstanceImplicits {
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Eq)}): Tuple${product.focusSize}Eq<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}Eq<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Eq)}
                |    }
                |}
                |""".trimMargin()
    else ""

  private fun orderTupleNInstance(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |interface Tuple${product.focusSize}Order<${product.expandedTypeArgs()}>
                | : ${Order.type}<arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>> {
                |
                |  ${product.arityInstanceProviders(Order)}
                |
                |  override fun compare(
                |    a: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>,
                |    b: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>
                |  ): Int {
                |    val (${product.types().joinToString(", ") { "x$it" }}) = a
                |    val (${product.types().joinToString(", ") { "y$it" }}) = b
                |    return ${product.types().joinToString(" + ") { "O$it().compare(x$it, y$it)" }}
                |  }
                |
                |}
                |
                |object Tuple${product.focusSize}OrderInstanceImplicits {
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Order)}): Tuple${product.focusSize}Order<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}Order<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Order)}
                |    }
                |}
                |""".trimMargin()
    else ""

  private fun showTupleNInstance(product: AnnotatedGeneric): String =
    if (product.hasTupleFocus)
      """|
                |interface Tuple${product.focusSize}Show<${product.expandedTypeArgs()}>
                | : ${Show.type}<arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>> {
                |
                |  ${product.arityInstanceProviders(Show)}
                |
                |  override fun show(
                |    a: arrow.core.Tuple${product.focusSize}<${product.expandedTypeArgs()}>
                |  ): String {
                |    val (${product.types().joinToString(", ") { "x$it" }}) = a
                |    return ${product.types().joinToString(" + ") { "S$it().show(x$it)" }}
                |  }
                |
                |}
                |
                |object Tuple${product.focusSize}ShowInstanceImplicits {
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Show)}): Tuple${product.focusSize}Show<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}Show<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Show)}
                |    }
                |}
                |""".trimMargin()
    else ""
}
