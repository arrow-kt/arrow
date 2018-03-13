package arrow.generic

import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

sealed class DerivedTypeClass(val type: String)
object Semigroup: DerivedTypeClass("arrow.typeclasses.Semigroup")
object Monoid: DerivedTypeClass("arrow.typeclasses.Monoid")
object Eq: DerivedTypeClass("arrow.typeclasses.Eq")
object Order: DerivedTypeClass("arrow.typeclasses.Order")
object Show: DerivedTypeClass("arrow.typeclasses.Show")
object Hash: DerivedTypeClass("arrow.typeclasses.Hash")

class ProductFileGenerator(
        private val annotatedList: Collection<AnnotatedGeneric>,
        private val generatedDir: File
) {

    private val tuple = "arrow.core.Tuple"
    private val letters = ('a'..'j').toList()

    fun generate() {
        buildProduct(annotatedList)
        buildInstances(annotatedList)
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

    private fun processElement(product: AnnotatedGeneric): Pair<AnnotatedGeneric, String> = product to """
            |package ${product.classData.`package`.escapedClassName}
            |
            |import arrow.syntax.applicative.*
            |import arrow.core.toT
            |
            |fun ${product.sourceClassName}.combine(other: ${product.sourceClassName}): ${product.sourceClassName} =
            |  this + other
            |
            |operator fun ${product.sourceClassName}.plus(other: ${product.sourceClassName}): ${product.sourceClassName} =
            |  arrow.typeclasses.semigroup<${product.sourceClassName}>().combine(this, other)
            |
            |fun empty${product.sourceSimpleName}(): ${product.sourceClassName} =
            |  arrow.typeclasses.monoid<${product.sourceClassName}>().empty()
            |
            |fun ${product.sourceClassName}.tupled(): ${focusType(product)} =
            |  ${tupleConstructor(product)}
            |
            |fun ${product.sourceClassName}.tupledLabelled(): ${labelledFocusType(product)} =
            |  ${product.targets.joinToString(" toT ") { """("${it.paramName}" toT ${it.paramName})""" }}
            |
            |fun <B> ${product.sourceClassName}.foldLabelled(f: ${product.targetNames.joinToString(prefix = "(", separator = ", ", postfix = ")") { "arrow.core.Tuple2<kotlin.String, $it>" }} -> B): B {
            |  val t = tupledLabelled()
            |  return f(${(0 until product.focusSize).joinToString(", ") { "t.${letters[it]}" }})
            |}
            |
            |fun ${focusType(product)}.to${product.sourceSimpleName}(): ${product.sourceClassName} =
            |  ${classConstructorFromTuple(product.sourceClassName, product.focusSize)}
            |
            |interface ${product.sourceSimpleName}ApplicativeSyntax<F> : arrow.typeclasses.ApplicativeSyntax<F> {
            |  fun arrow.Kind<F, ${focusType(product)}>.to${product.sourceSimpleName}(): arrow.Kind<F, ${product.sourceClassName}> =
            |    this.map { it.to${product.sourceSimpleName}() }
            |
            |  fun mapTo${product.sourceSimpleName}${kindedProperties("F", product)}: arrow.Kind<F, ${product.sourceClassName}> =
            |    applicative().tupled(${product.targets.joinToString(", ") { it.paramName }}).to${product.sourceSimpleName}()
            |}
            |
            |interface ${product.sourceSimpleName}SemigroupInstance : arrow.typeclasses.Semigroup<${product.sourceClassName}> {
            |  override fun combine(a: ${product.sourceClassName}, b: ${product.sourceClassName}): ${product.sourceClassName} =
            |    arrow.typeclasses.semigroup<${focusType(product)}>().combine(a.tupled(), b.tupled()).to${product.sourceSimpleName}()
            |}
            |
            |object ${product.sourceSimpleName}SemigroupInstanceImplicits {
            |  fun instance(): ${product.sourceSimpleName}SemigroupInstance =
            |    object : ${product.sourceSimpleName}SemigroupInstance {}
            |}
            |
            |interface ${product.sourceSimpleName}MonoidInstance : ${product.sourceSimpleName}SemigroupInstance, arrow.typeclasses.Monoid<${product.sourceClassName}> {
            |  override fun empty(): ${product.sourceClassName} =
            |    arrow.typeclasses.monoid<${focusType(product)}>().empty().to${product.sourceSimpleName}()
            |}
            |
            |object ${product.sourceSimpleName}MonoidInstanceImplicits {
            |  fun instance(): ${product.sourceSimpleName}MonoidInstance =
            |    object : ${product.sourceSimpleName}MonoidInstance {}
            |}
            |
            |interface ${product.sourceSimpleName}EqInstance : arrow.typeclasses.Eq<${product.sourceClassName}> {
            |  override fun eqv(a: ${product.sourceClassName}, b: ${product.sourceClassName}): Boolean =
            |    a == b
            |}
            |
            |object ${product.sourceSimpleName}EqInstanceImplicits {
            |  fun instance(): ${product.sourceSimpleName}EqInstance =
            |    object : ${product.sourceSimpleName}EqInstance {}
            |}
            |
            |interface ${product.sourceSimpleName}OrderInstance : arrow.typeclasses.Order<${product.sourceClassName}> {
            |  override fun compare(a: ${product.sourceClassName}, b: ${product.sourceClassName}): Int =
            |    arrow.typeclasses.order<${focusType(product)}>().compare(a.tupled(), b.tupled())
            |}
            |
            |object ${product.sourceSimpleName}OrderInstanceImplicits {
            |  fun instance(): ${product.sourceSimpleName}OrderInstance =
            |    object : ${product.sourceSimpleName}OrderInstance {}
            |}
            |
            |interface ${product.sourceSimpleName}ShowInstance : arrow.typeclasses.Show<${product.sourceClassName}> {
            |  override fun show(a: ${product.sourceClassName}): String =
            |    a.toString()
            |}
            |
            |object ${product.sourceSimpleName}ShowInstanceImplicits {
            |  fun instance(): ${product.sourceSimpleName}ShowInstance =
            |    object : ${product.sourceSimpleName}ShowInstance {}
            |}
            |""".trimMargin()

    private fun tupleConstructor(product: AnnotatedGeneric): String =
            product.targets.joinToString(prefix = "$tuple${product.focusSize}(", postfix = ")", transform = { "this.${it.paramName.plusIfNotBlank(prefix = "`", postfix = "`")}" })

    private fun labelledFocusType(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus) product.targetNames.map { "${tuple}2<String, $it>" }.joinToString(prefix = "$tuple${product.targets.size}<", postfix = ">")
            else product.targetNames.first()

    private fun focusType(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus) product.targetNames.joinToString(prefix = "$tuple${product.targets.size}<", postfix = ">")
            else product.targetNames.first()

    private fun classConstructorFromTuple(sourceClassName: String, propertiesSize: Int): String =
            (0 until propertiesSize).joinToString(prefix = "$sourceClassName(", postfix = ")", transform = { "this.${letters[it]}" })

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

    private fun semigroupTupleNInstance(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus)
                """|
                |interface Tuple${product.focusSize}SemigroupInstance<${product.expandedTypeArgs()}>
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
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Semigroup)}): Tuple${product.focusSize}SemigroupInstance<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}SemigroupInstance<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Semigroup)}
                |    }
                |}
                |""".trimMargin()
            else ""

    private fun monoidTupleNInstance(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus)
                """|
                |interface Tuple${product.focusSize}MonoidInstance<${product.expandedTypeArgs()}>
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
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Monoid)}): Tuple${product.focusSize}MonoidInstance<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}MonoidInstance<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Monoid)}
                |    }
                |}
                |""".trimMargin()
            else ""

    private fun eqTupleNInstance(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus)
                """|
                |interface Tuple${product.focusSize}EqInstance<${product.expandedTypeArgs()}>
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
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Eq)}): Tuple${product.focusSize}EqInstance<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}EqInstance<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Eq)}
                |    }
                |}
                |""".trimMargin()
            else ""

    private fun orderTupleNInstance(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus)
                """|
                |interface Tuple${product.focusSize}OrderInstance<${product.expandedTypeArgs()}>
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
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Order)}): Tuple${product.focusSize}OrderInstance<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}OrderInstance<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Order)}
                |    }
                |}
                |""".trimMargin()
            else ""

    private fun showTupleNInstance(product: AnnotatedGeneric): String =
            if (product.hasTupleFocus)
                """|
                |interface Tuple${product.focusSize}ShowInstance<${product.expandedTypeArgs()}>
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
                |  fun <${product.expandedTypeArgs()}> instance(${product.arityInstanceInjections(Show)}): Tuple${product.focusSize}ShowInstance<${product.expandedTypeArgs()}> =
                |    object : Tuple${product.focusSize}ShowInstance<${product.expandedTypeArgs()}> {
                |      ${product.arityInstanceImplementations(Show)}
                |    }
                |}
                |""".trimMargin()
            else ""
}

/*

@instance(Tuple2::class)
interface Tuple2MonoidInstance<A, B> : Monoid<Tuple2<A, B>> {

    fun MA(): Monoid<A>

    fun MB(): Monoid<B>

    override fun empty(): Tuple2<A, B> = Tuple2(MA().empty(), MB().empty())

    override fun combine(a: Tuple2<A, B>, b: Tuple2<A, B>): Tuple2<A, B> {
        val (xa, xb) = a
        val (ya, yb) = b
        return Tuple2(MA().combine(xa, ya), MB().combine(xb, yb))
    }
}

@instance(Tuple2::class)
interface Tuple2EqInstance<A, B> : Eq<Tuple2<A, B>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    override fun eqv(a: Tuple2<A, B>, b: Tuple2<A, B>): Boolean =
            EQA().eqv(a.a, b.a) && EQB().eqv(a.b, b.b)
}

@instance(Tuple2::class)
interface Tuple2ShowInstance<A, B> : Show<Tuple2<A, B>> {
    override fun show(a: Tuple2<A, B>): String =
            a.toString()
}
 */