package arrow.generic

import me.eugeniomarletti.kotlin.metadata.escapedClassName
import me.eugeniomarletti.kotlin.metadata.plusIfNotBlank
import java.io.File

class ProductFileGenerator(
        private val annotatedList: Collection<AnnotatedGeneric>,
        private val generatedDir: File
) {

    private val tuple = "arrow.core.Tuple"
    private val letters = ('a'..'j').toList()

    fun generate() = buildProduct(annotatedList)

    private fun buildProduct(products: Collection<AnnotatedGeneric>) =
            products.map(this::processElement)
                    .forEach { (element, funString) ->
                        File(generatedDir, "${productAnnotationClass.simpleName}.${element.classData.`package`}.${element.sourceName}.kt").printWriter().use { w ->
                            w.println(funString)
                        }
                    }

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
            |  fun tupleSemigroup(): arrow.typeclasses.Semigroup<${focusType(product)}>
            |  override fun combine(a: ${product.sourceClassName}, b: ${product.sourceClassName}): ${product.sourceClassName} =
            |    tupleSemigroup().combine(a.tupled(), b.tupled()).to${product.sourceSimpleName}()
            |}
            |
            |object ${product.sourceSimpleName}SemigroupInstanceImplicits {
            |  fun instance(tupleSemigroup: arrow.typeclasses.Semigroup<${focusType(product)}>): ${product.sourceSimpleName}SemigroupInstance =
            |    object : ${product.sourceSimpleName}SemigroupInstance {
            |      override fun tupleSemigroup(): arrow.typeclasses.Semigroup<${focusType(product)}> =
            |        tupleSemigroup
            |    }
            |}
            |
            |interface ${product.sourceSimpleName}MonoidInstance : ${product.sourceSimpleName}SemigroupInstance, arrow.typeclasses.Monoid<${product.sourceClassName}> {
            |  fun tupleMonoid(): arrow.typeclasses.Monoid<${focusType(product)}>
            |  override fun tupleSemigroup(): arrow.typeclasses.Semigroup<${focusType(product)}> = tupleMonoid()
            |  override fun empty(): ${product.sourceClassName} =
            |    tupleMonoid().empty().to${product.sourceSimpleName}()
            |}
            |
            |object ${product.sourceSimpleName}MonoidInstanceImplicits {
            |  fun instance(tupleMonoid: arrow.typeclasses.Monoid<${focusType(product)}>): ${product.sourceSimpleName}MonoidInstance =
            |    object : ${product.sourceSimpleName}MonoidInstance {
            |      override fun tupleMonoid(): arrow.typeclasses.Monoid<${focusType(product)}> =
            |        tupleMonoid
            |    }
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
            |  fun tupleOrder(): arrow.typeclasses.Order<${focusType(product)}>
            |  override fun eqv(a: ${product.sourceClassName}, b: ${product.sourceClassName}): Boolean =
            |    a == b
            |  override fun compare(a: ${product.sourceClassName}, b: ${product.sourceClassName}): Int =
            |    tupleOrder().compare(a.tupled(), b.tupled())
            |}
            |
            |object ${product.sourceSimpleName}OrderInstanceImplicits {
            |  fun instance(tupleOrder: arrow.typeclasses.Order<${focusType(product)}>): ${product.sourceSimpleName}OrderInstance =
            |    object : ${product.sourceSimpleName}OrderInstance {
            |      override fun tupleOrder(): arrow.typeclasses.Order<${focusType(product)}> = tupleOrder
            |    }
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
            |
            |""".trimMargin()

    /**
    a + b	a.plus(b)
    a - b	a.minus(b)
    a * b	a.times(b)
    a / b	a.div(b)
    a % b	a.rem(b), a.mod(b) (deprecated)
    a..b	a.rangeTo(b)
     */

//    private fun getFunction(product: AnnotatedGeneric) =
//            if (product.hasTupleFocus) tupleConstructor(product)
//            else "${product.sourceName}.${product.targets.first().paramName}"
//
//    private fun reverseGetFunction(product: AnnotatedGeneric) =
//            if (product.hasTupleFocus) "tuple: ${focusType(product)} -> ${classConstructorFromTuple(product.sourceClassName, product.focusSize)}"
//            else "${product.sourceClassName}(it)"

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
}