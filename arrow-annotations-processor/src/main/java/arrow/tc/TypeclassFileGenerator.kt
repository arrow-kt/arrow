package arrow.tc

import arrow.common.Package
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.typeConstraints
import java.io.File

data class Typeclass(
        val `package`: Package,
        val target: AnnotatedTypeclass
) {
    val clazz = target.classOrPackageProto as ClassOrPackageDataWrapper.Class
    val typeArgs: List<String> = target.classOrPackageProto.typeParameters.map { target.classOrPackageProto.nameResolver.getString(it.name) }
    fun expandedTypeArgs(reified: Boolean = false): String =
            if (target.classOrPackageProto.typeParameters.isNotEmpty()) {
                target.classOrPackageProto.typeParameters.joinToString(
                        prefix = "<",
                        separator = ", ",
                        transform = {
                            val name = target.classOrPackageProto.nameResolver.getString(it.name)
                            if (reified) "reified $name" else name
                        },
                        postfix = ">"
                )
            } else {
                ""
            }
    val name: String = clazz.nameResolver.getString(clazz.classProto.fqName).replace("/", ".")
    val simpleName = name.substringAfterLast(".")
}

class TypeclassFileGenerator(
        private val generatedDir: File,
        annotatedList: List<AnnotatedTypeclass>
) {

    private val typeclasses: List<Typeclass> = annotatedList.map { Typeclass(it.classOrPackageProto.`package`, it) }

    /**
     * Main entry point for higher kinds extension generation
     */
    fun generate() {
        typeclasses.forEachIndexed { _, tc ->
            val elementsToGenerate = listOf(genLookup(tc))
            val source: String = elementsToGenerate.joinToString(prefix = "package ${tc.`package`}\n\n", separator = "\n", postfix = "\n")
            val file = File(generatedDir, typeClassAnnotationClass.simpleName + ".${tc.target.classElement.qualifiedName}.kt")
            file.writeText(source)
        }
    }

    /*
    inline fun <reified F> monad(): Monad<F> = instance(InstanceParametrizedType(Monad::class.java, listOf(typeLiteral<F>())))
     */

    private fun genLookup(tc: Typeclass): String {
        val typeLiterals = tc.typeArgs.map { "typeLiteral<$it>()" }
        return """
            |import arrow.*
            |
            |inline fun ${tc.expandedTypeArgs(true)} ${tc.simpleName.decapitalize()}(): ${tc.name}${tc.expandedTypeArgs()} =
            |  instance(InstanceParametrizedType(${tc.name}::class.java, listOf(${typeLiterals.joinToString(",")})))
            |""".trimMargin()
    }

}
