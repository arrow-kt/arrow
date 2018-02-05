package arrow.tc

import arrow.common.Package
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.extractFullName
import arrow.common.utils.removeBackticks
import arrow.common.utils.typeConstraints
import arrow.derive.HKArgs
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.serialization.ProtoBuf
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

    val typeConstraints = target.classOrPackageProto.typeConstraints()
    val name: String = clazz.nameResolver.getString(clazz.classProto.fqName).replace("/", ".")
    val simpleName = name.substringAfterLast(".")
}

data class SyntaxFunctionSignature(
        val typeClass: Typeclass,
        val tparams: List<String>,
        val name: String,
        val args: List<Pair<String, String>>,
        val retType: String,
        val hkArgs: HKArgs,
        val receiverType: String,
        val isAbstract: Boolean

) {

    fun generate(): String {
        val typeParamsS = tparams.joinToString(prefix = "<`", separator = "`, `", postfix = "`>")
        val argsS = args.drop(1).map { "${it.first}: ${it.second}" }.joinToString(prefix = "(`", separator = "`, `", postfix = "`)")
        return """|fun $typeParamsS ${receiver()}__name__$argsS: $retType =
                  |    ${implBody()}""".removeBackticks().replace("__name__", "`$name`").trimMargin()
    }

    fun receiver(): String =
            when (hkArgs) {
                is HKArgs.None -> ""
                is HKArgs.First -> "${args[0].second}."
                is HKArgs.Unknown -> ""
            }

    fun implBody(): String =
            when (hkArgs) {
                is HKArgs.None -> ""
                is HKArgs.First -> {
                    val thisArgs = listOf("this" to "") + args.drop(1)
                    "${typeClass.simpleName.decapitalize()}().__name__(${thisArgs.joinToString(", ") { it.first }})"
                }
                is HKArgs.Unknown -> "${typeClass.simpleName.decapitalize()}().$name(${args.joinToString(", ") { it.first }})"
            }

    companion object {

        fun from(receiverType: String, typeClass: Typeclass, f: ProtoBuf.Function): SyntaxFunctionSignature {
            val nameResolver = typeClass.clazz.nameResolver
            val typeParams = f.typeParameterList.map { nameResolver.getString(it.name) }
            val typeClassAbstractKind = nameResolver.getString(typeClass.clazz.typeParameters[0].name)
            val args = f.valueParameterList.map {
                val argName = nameResolver.getString(it.name)
                val argType = it.type.extractFullName(typeClass.clazz, failOnGeneric = false)
                argName to argType
            }
            val abstractReturnType = f.returnType.extractFullName(typeClass.clazz, failOnGeneric = false)
            val isAbstract = f.modality == ProtoBuf.Modality.ABSTRACT
            return SyntaxFunctionSignature(
                    typeClass = typeClass,
                    tparams = typeParams,
                    name = nameResolver.getString(f.name),
                    args = args,
                    retType = abstractReturnType,
                    hkArgs = when {
                        f.valueParameterList.isEmpty() -> HKArgs.None
                        nameResolver.getString(f.getValueParameter(0).type.className).startsWith("arrow/HK") -> HKArgs.First
                        else -> HKArgs.Unknown
                    },
                    receiverType = receiverType,
                    isAbstract = isAbstract
            )
        }
    }
}

class TypeclassFileGenerator(
        private val generatedDir: File,
        annotatedList: List<AnnotatedTypeclass>
) {

    private val typeclasses: List<Typeclass> = annotatedList.map { Typeclass(it.classOrPackageProto.`package`, it) }

    private fun functionSignatures(typeClass: Typeclass): List<SyntaxFunctionSignature> {
        return typeClass.target.classOrPackageProto.functionList.map {
            SyntaxFunctionSignature.from("arrow.HK", typeClass, it)
        }.filter { it.hkArgs == HKArgs.First }
    }

    /**
     * Main entry point for higher kinds extension generation
     */
    fun generate() {
        typeclasses.forEachIndexed { _, tc ->
            val elementsToGenerate = listOf(genLookup(tc), genSyntax(tc))
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

    private fun genSyntax(tc: Typeclass): String {
        val delegatedFunctions: List<String> = functionSignatures(tc).map { it.generate() }

        return """
            |interface ${tc.simpleName}Syntax${tc.expandedTypeArgs(false)} {
            |
            |  fun ${tc.simpleName.decapitalize()}(): ${tc.simpleName}${tc.expandedTypeArgs(false)}
            |
            |  ${delegatedFunctions.joinToString("\n\n  ")}
            |}
            |""".trimMargin()
    }

}
