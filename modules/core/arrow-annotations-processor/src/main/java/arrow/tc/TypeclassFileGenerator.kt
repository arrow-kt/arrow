package arrow.tc

import arrow.common.Package
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.extractFullName
import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
import arrow.derive.HKArgs
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.serialization.ProtoBuf
import java.io.File

fun ClassOrPackageDataWrapper.expandedTypeArgs(reified: Boolean = false): String =
        if (typeParameters.isNotEmpty()) {
            typeParameters.joinToString(
                    prefix = "<",
                    separator = ", ",
                    transform = {
                        val name = nameResolver.getString(it.name)
                        if (reified) "reified $name" else name
                    },
                    postfix = ">"
            )
        } else {
            ""
        }

data class Typeclass(
        val `package`: Package,
        val target: AnnotatedTypeclass
) {
    val clazz = target.classOrPackageProto as ClassOrPackageDataWrapper.Class
    val typeArgs: List<String> = target.classOrPackageProto.typeParameters.map { target.classOrPackageProto.nameResolver.getString(it.name) }
    fun expandedTypeArgs(reified: Boolean = false): String =
            target.classOrPackageProto.expandedTypeArgs(reified)

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
        val typeParamsS = if (tparams.isEmpty()) "" else tparams.joinToString(prefix = "<`", separator = "`, `", postfix = "`>")
        val argsS = when (hkArgs) {
            is HKArgs.None -> "(dummy: Unit = Unit)"
            else -> args.drop(1).map {
                "${it.first}: ${it.second}"
            }.joinToString(prefix = "(`", separator = "`, `", postfix = "`)")
        }
        return """|fun $typeParamsS ${receiver()}__name__$argsS: $retType =
                  |    ${implBody()}""".removeBackticks().replace("__name__", "`$name`").trimMargin()
    }

    fun receiver(): String =
            when (hkArgs) {
                is HKArgs.None -> ""
                is HKArgs.First -> "${args[0].second}."
                is HKArgs.Unknown -> "${args[0].second}."
            }

    fun implBody(): String =
            when (hkArgs) {
                is HKArgs.None -> "this@${typeClass.simpleName}Syntax.${typeClass.simpleName.decapitalize()}().__name__(${args.drop(2).joinToString(", ")})"
                is HKArgs.First -> {
                    val thisArgs = listOf("this" to "") + args.drop(2)
                    "this@${typeClass.simpleName}Syntax.${typeClass.simpleName.decapitalize()}().__name__(${thisArgs.joinToString(", ") { it.first }})"
                }
                is HKArgs.Unknown -> {
                    val thisArgs = listOf("this" to "") + args.drop(2)
                    "this@${typeClass.simpleName}Syntax.${typeClass.simpleName.decapitalize()}().__name__(${thisArgs.joinToString(", ") { it.first }})"
                }
            }

    companion object {

        fun from(receiverType: String, typeClass: Typeclass, f: ProtoBuf.Function): SyntaxFunctionSignature {
            val nameResolver = typeClass.clazz.nameResolver
            val typeParams = f.typeParameterList.map { nameResolver.getString(it.name) }
            val argsC = f.valueParameterList.map {
                val argName = nameResolver.getString(it.name)
                val argType = it.type.extractFullName(typeClass.clazz, failOnGeneric = false)
                argName to argType
            }
            val dummyErasureArg = listOf("dummy" to "Unit = Unit")
            val hkArgs = when {
                f.valueParameterList.isEmpty() -> HKArgs.None
                nameResolver.getString(f.getValueParameter(0).type.className).startsWith("arrow/HK") -> HKArgs.First
                else -> HKArgs.Unknown
            }
            val args = when (hkArgs) {
                HKArgs.None -> dummyErasureArg
                HKArgs.First -> listOf(argsC[0]) + dummyErasureArg + argsC.drop(1)
                HKArgs.Unknown -> listOf(argsC[0]) + dummyErasureArg + argsC.drop(1)
            }
            val abstractReturnType = f.returnType.extractFullName(typeClass.clazz, failOnGeneric = false)
            val isAbstract = f.modality == ProtoBuf.Modality.ABSTRACT
            return SyntaxFunctionSignature(
                    typeClass = typeClass,
                    tparams = typeParams,
                    name = nameResolver.getString(f.name),
                    args = args,
                    retType = abstractReturnType,
                    hkArgs = hkArgs,
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

    private val typeclasses: List<Typeclass> = annotatedList.map {
        Typeclass(it.classOrPackageProto.`package`, it)
    }

    private fun functionSignatures(typeClass: Typeclass): List<SyntaxFunctionSignature> = typeClass.target.classOrPackageProto.functionList.map {
        SyntaxFunctionSignature.from("arrow.HK", typeClass, it)
    }

    private fun removeOverrides(tc: Typeclass, functions: List<SyntaxFunctionSignature>): List<SyntaxFunctionSignature> {
        val superFunctionNames = tc.target.superTypes.flatMap { c ->
            c.functionList.map { c.nameResolver.getString(it.name) }
        }
        return functions.fold(emptyList()) { acc, f ->
            val discard = superFunctionNames.any { it == f.name }
            if (discard) acc else acc + listOf(f)
        }
    }

    /**
     * Main entry point for higher kinds extension generation
     */
    fun generate() {
        typeclasses.forEachIndexed { _, tc ->
            val elementsToGenerate = if (tc.target.syntax) listOf(genLookup(tc), genSyntax(tc)) else listOf(genLookup(tc))
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

    fun List<String>.renderExtendsClause(): String =
            if (isEmpty()) "" else ": " + joinToString(separator = ", ")

    fun List<String>.renderOverrides(): String =
            if (isEmpty()) "" else joinToString(separator = "\n\n  ")

    private fun genSyntax(tc: Typeclass): String {
        val delegatedFunctions: List<String> = removeOverrides(tc, functionSignatures(tc)).map { it.generate() }
        val superTypes: List<String> = tc.target.superTypes.map { extends ->
            val superName = extends.fullName.replace("/", ".")
            "${superName}Syntax${extends.expandedTypeArgs(false)}"
        }
        val overrides: List<String> = tc.target.superTypes.map { extends ->
            val superName = extends.fullName.replace("/", ".")
            val simpleSuperName = superName.substringAfterLast(".")
            "override fun ${simpleSuperName.decapitalize()}() : $superName ${extends.expandedTypeArgs(false)} = ${tc.simpleName.decapitalize()}()"
        }
        val diamondOverrides: List<String> = tc.target.diamondTypes.map { extends ->
            val superName = extends.fullName.replace("/", ".")
            val simpleSuperName = superName.substringAfterLast(".")
            "override fun ${simpleSuperName.decapitalize()}() : $superName ${extends.expandedTypeArgs(false)} = ${tc.simpleName.decapitalize()}()"
        }
        return """
            |interface ${tc.simpleName}Syntax${tc.expandedTypeArgs(false)} ${superTypes.renderExtendsClause()} {
            |
            |  fun ${tc.simpleName.decapitalize()}(): ${tc.simpleName}${tc.expandedTypeArgs(false)}
            |
            |  ${overrides.renderOverrides()}
            |
            |  ${diamondOverrides.renderOverrides()}
            |
            |  ${delegatedFunctions.renderOverrides()}
            |}
            |""".trimMargin()
    }

}
