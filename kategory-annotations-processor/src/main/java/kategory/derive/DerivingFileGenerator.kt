package kategory.derive

import kategory.common.utils.ClassOrPackageDataWrapper
import kategory.common.utils.extractFullName
import kategory.common.utils.removeBackticks
import kategory.higherkinds.HKMarkerPostFix
import kategory.higherkinds.KindPostFix
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.serialization.ProtoBuf
import java.io.File

fun argAsSeenFromReceiver(typeClassFirstTypeArg: String, abstractType: String, receiverType: String): String =
        abstractType.replace("`kategory`.`HK`<`$typeClassFirstTypeArg`,\\s".toRegex(), "`$receiverType$KindPostFix`<")

fun retTypeAsSeenFromReceiver(typeClassFirstTypeArg: String, abstractType: String, receiverType: String): String =
        if (abstractType.startsWith("`kategory`.`HK`")) {
            abstractType.replace("`kategory`.`HK`<`$typeClassFirstTypeArg`,\\s".toRegex(), "`$receiverType`<")
        } else {
            abstractType.replace("`kategory`.`HK`<`$typeClassFirstTypeArg`,\\s".toRegex(), "`$receiverType$KindPostFix`<")
        }

sealed class HKArgs {
    object None : HKArgs()
    object First : HKArgs()
    object Unknown : HKArgs()
}

data class FunctionSignature(
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
        val argsS = args.map { "${it.first}: ${it.second}" }.joinToString(prefix = "(`", separator = "`, `", postfix = "`)")
        return """|override fun $typeParamsS `$name`$argsS: $retType =
                  |    ${implBody()}""".removeBackticks().trimMargin()
    }

    fun implBody(): String =
            when (hkArgs) {
                is HKArgs.None -> "${receiverType}.${name}()"
                is HKArgs.First -> "${args[0].first}.ev().${name}(${args.drop(1).map { it.first }.joinToString(", ")})"
                is HKArgs.Unknown -> "${receiverType}.${name}(${args.map { it.first }.joinToString(", ")})"
            }

    companion object {

        fun from(receiverType: String, typeClass: ClassOrPackageDataWrapper, f: ProtoBuf.Function): FunctionSignature {
            val typeParams = f.typeParameterList.map { typeClass.nameResolver.getString(it.name) }
            val typeClassAbstractKind = typeClass.nameResolver.getString(typeClass.typeParameters[0].name)
            val args = f.valueParameterList.map {
                val argName = typeClass.nameResolver.getString(it.name)
                val argType = it.type.extractFullName(typeClass, failOnGeneric = false)
                argName to argAsSeenFromReceiver(typeClassAbstractKind, argType, receiverType)
            }
            val abstractReturnType = f.returnType.extractFullName(typeClass, failOnGeneric = false)
            val concreteType = retTypeAsSeenFromReceiver(typeClassAbstractKind, abstractReturnType, receiverType)
            val isAbstract = f.modality == ProtoBuf.Modality.ABSTRACT
            return FunctionSignature(
                    tparams = typeParams,
                    name = typeClass.nameResolver.getString(f.name),
                    args = args,
                    retType = concreteType,
                    hkArgs = when {
                        f.valueParameterList.isEmpty() -> HKArgs.None
                        typeClass.nameResolver.getString(f.getValueParameter(0).type.className).startsWith("kategory/HK") -> HKArgs.First
                        else -> HKArgs.Unknown
                    },
                    receiverType = receiverType,
                    isAbstract = isAbstract
            )
        }
    }
}

class TypeclassInstanceGenerator(
        val targetType: AnnotatedDeriving,
        val typeClass: ClassOrPackageDataWrapper.Class) {

    val target: ClassOrPackageDataWrapper.Class = targetType.classOrPackageProto as ClassOrPackageDataWrapper.Class

    val receiverType: String = targetType.classElement.qualifiedName.toString()

    val receiverSimpleName: String = receiverType.substringAfterLast(".")

    val receiverName: String = "$receiverSimpleName$HKMarkerPostFix"

    val typeClassFQName: String =
            typeClass.nameResolver.getString(typeClass.classProto.fqName).replace("/", ".")

    val typeClassName: String = typeClassFQName.substringAfterLast(".")

    val tparams: List<String> = typeClass.typeParameters.map { typeClass.nameResolver.getString(it.name) }

    val tparamsAsSeenFromReceiver: List<String> = listOf(receiverName) + tparams.drop(1)

    fun functionSignatures(): List<FunctionSignature> {
        val tcs = listOf(typeClass) + targetType.typeclassSuperTypes[typeClass]!!
        return tcs.flatMap { tc ->
            tc.functionList.map {
                FunctionSignature.from(receiverType, tc, it)
            }
        }.distinctBy { it.name }
    }

    val companionFactoryName: String = typeClassName[0].toLowerCase() + typeClassName.drop(1)

    val instanceName: String = "$receiverSimpleName${typeClassName}Instance"

    fun targetHasFunction(f: FunctionSignature, c: ClassOrPackageDataWrapper): Boolean =
            c.functionList.any {
                val typeClassFunctionName = c.nameResolver.getString(it.name)
                typeClassFunctionName == f.name
            }

    fun targetRequestsDelegation(f: FunctionSignature): Boolean = when (f.hkArgs) {
            is HKArgs.None -> f.isAbstract || targetHasFunction(f, targetType.companionClassProto)
            is HKArgs.First -> f.isAbstract || targetHasFunction(f, target)
            is HKArgs.Unknown -> f.isAbstract || targetHasFunction(f, targetType.companionClassProto)
        }

    val delegatedFunctions: List<String> = functionSignatures().filter(this::targetRequestsDelegation).map { it.generate() }

    fun generate(): String {
        val tArgs = tparamsAsSeenFromReceiver.joinToString(", ")
        return """
            |interface $instanceName : ${typeClassFQName}<$tArgs> {
            |  ${delegatedFunctions.joinToString("\n\n  ")}
            |}
            |
            |object ${instanceName}Implicits {
            |  fun instance(): $instanceName = ${receiverType}.Companion.${companionFactoryName}()
            |}
            |
            |fun ${receiverType}.Companion.${companionFactoryName}(): $instanceName =
            |  object : $instanceName, ${typeClassFQName}<$tArgs> {}
            |
        """.removeBackticks().trimMargin()
    }
}

class DerivingFileGenerator(
        private val generatedDir: File,
        private val annotatedList: List<AnnotatedDeriving>
) {

    /**
     * Main entry point for deriving extension generation
     */
    fun generate() {
        annotatedList.forEachIndexed { _, c ->
            val elementsToGenerate = listOf(genImpl(c))
            val source: String = elementsToGenerate.joinToString(prefix = "package ${c.classOrPackageProto.`package`}\n\n", separator = "\n")
            val file = File(generatedDir, derivingAnnotationClass.simpleName + ".${c.classElement.qualifiedName}.kt")
            file.writeText(source)
        }
    }

    private fun genImpl(c: AnnotatedDeriving): String =
            c.derivingTypeclasses.filter { it is ClassOrPackageDataWrapper.Class }.map {
                TypeclassInstanceGenerator(c, it as ClassOrPackageDataWrapper.Class).generate()
            }.joinToString("\n\n")

}
