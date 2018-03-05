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

    /*
    //fun Person.tupled(): Tuple2<String, Int> =
//        Tuple2(this.name, this.age)



fun ${product.sourceName}Product(): arrow.products.Product<${product.sourceClassName}, ${focusType(product)}> = arrow.products.Product(
            |        get = { ${product.sourceName}: ${product.sourceClassName} -> ${getFunction(product)} },
            |        reverseGet = { ${reverseGetFunction(product)} }
            |)



            //interface PersonGenericSyntax<F> : ApplicativeSyntax<F> {
//
//    fun Kind<F, Tuple2<String, Int>>.toPerson(): Kind<F, Person> =
//            this.map { Person.fromTuple(it) }
//
//    fun Person.Companion.from(a: Kind<F, String>, b: Kind<F, Int>): Kind<F, Person> =
//            applicative().tupled(a, b).toPerson()
//
//}
     */

    private fun processElement(product: AnnotatedGeneric): Pair<AnnotatedGeneric, String> = product to """
            |package ${product.classData.`package`.escapedClassName}
            |
            |import arrow.syntax.applicative.*
            |import arrow.core.toT
            |
            |fun ${product.sourceClassName}.tupled(): ${focusType(product)} =
            |  ${tupleConstructor(product)}
            |
            |fun ${product.sourceClassName}.tupledLabelled(): ${labelledFocusType(product)} =
            |  ${product.targets.joinToString(" toT ") { """("${it.paramName}" toT ${it.paramName})""" }}
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
            |""".trimMargin()

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