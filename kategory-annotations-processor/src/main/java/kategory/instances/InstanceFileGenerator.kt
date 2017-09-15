package kategory.instances

import java.io.File
import kategory.common.Package
import kategory.common.utils.ClassOrPackageDataWrapper
import kategory.common.utils.extractFullName
import kategory.common.utils.removeBackticks
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.deserialization.supertypes

data class FunctionMapping(
        val name: String,
        val typeclass: ClassOrPackageDataWrapper,
        val function: ProtoBuf.Function,
        val retTypeName: String)

data class Instance(
        val `package`: Package,
        val target: AnnotatedInstance
) {
    val name = target.classElement.simpleName

    val implicitObjectName: String = "${name}Implicits"

    val receiverTypeName = target.dataType.nameResolver.getString(target.dataType.classProto.fqName).replace("/", ".")

    val receiverTypeSimpleName = receiverTypeName.substringAfterLast(".")

    val companionFactoryName = name.toString()
            .replace(receiverTypeSimpleName, "")
            .replace("Instance", "")
            .decapitalize()

    fun expandedTypeArgs(reified: Boolean = false): String =
            target.classOrPackageProto.typeParameters.joinToString(
                    prefix = "<",
                    separator = ", ",
                    transform = {
                        val name = target.classOrPackageProto.nameResolver.getString(it.name)
                        if (reified) "reified $name" else name
                    },
                    postfix = ">"
            )

    private val abstractFunctions: List<FunctionMapping> =
            (target.superTypes + listOf(target.classOrPackageProto)).flatMap { tc ->
                tc.functionList
                        .filter { it.modality == ProtoBuf.Modality.ABSTRACT }
                        .flatMap {
                            val retTypeName = it.returnType.extractFullName(tc, failOnGeneric = false).removeBackticks().substringBefore("<")
                            val retType = target.processor.elementUtils.getTypeElement(
                                    it.returnType.extractFullName(tc, failOnGeneric = false).removeBackticks().substringBefore("<")
                            )
                            when {
                                retType != null -> {
                                    val current = target.processor.getClassOrPackageDataWrapper(retType) as ClassOrPackageDataWrapper.Class
                                    val typeTable = TypeTable(current.classProto.typeTable)
                                    val isTypeClassReturnType = current.classProto.supertypes(typeTable).any {
                                        val typeName = it.extractFullName(current, failOnGeneric = false)
                                        typeName == "`kategory`.`Typeclass`"
                                    }
                                    if (isTypeClassReturnType)
                                        listOf(FunctionMapping(tc.nameResolver.getString(it.name), tc, it, retTypeName))
                                    else
                                        emptyList()
                                }
                                else -> emptyList()
                            }
                        }
            }.fold(emptyList()) { acc, func ->
                val retType = func.function.returnType.extractFullName(func.typeclass, failOnGeneric = false).removeBackticks()
                val existingParamInfo = getParamInfo(func.name to retType)
                when {
                    acc.contains(func) -> acc //if the same param was already added ignore it
                    else -> { //remove accumulated functions whose return types  supertypes of the current evaluated one and add the current one
                        val remove = acc.find { av ->
                            val avRetType = av.function.returnType.extractFullName(av.typeclass, failOnGeneric = false)
                                    .removeBackticks().replace(".", "/").substringBefore("<")
                            existingParamInfo.superTypes.contains(avRetType)
                        }
                        val ignore = acc.any { av ->
                            val avRetTypeUnparsed = av.function.returnType.extractFullName(av.typeclass, failOnGeneric = false)
                                    .removeBackticks()
                            val parsedRetType = retType.replace(".", "/").substringBefore("<")
                            val avParamInfo = getParamInfo(av.name to avRetTypeUnparsed)
                            avParamInfo.superTypes.contains(parsedRetType)
                        }
                        if (remove != null) (acc - remove) + listOf(func)
                        else if (ignore) acc
                        else acc + listOf(func)
                    } //
                }
            }

    data class ParamInfo(
            val param: Pair<String, String>,
            val superTypes: List<String>,
            val paramTypeName: String,
            val typeVarName: String)

    private fun getParamInfo(param: Pair<String, String>): ParamInfo {
        val typeVarName = param.second.substringAfter("<").substringBefore(">")
        val rt = target.processor.elementUtils.getTypeElement(param.second.substringBefore("<"))
        val paramType = target.processor.getClassOrPackageDataWrapper(rt) as ClassOrPackageDataWrapper.Class
        val paramTypeName = paramType.nameResolver.getString(paramType.classProto.fqName)
        val typeTable = TypeTable(paramType.classProto.typeTable)
        val superTypes = target.processor.recurseTypeclassInterfaces(paramType, typeTable, emptyList()).map {
            val t = it as ClassOrPackageDataWrapper.Class
            t.nameResolver.getString(t.classProto.fqName)
        }
        return ParamInfo(param, superTypes, paramTypeName, typeVarName)
    }

    val args: List<Pair<String, String>> = abstractFunctions.map { (name, tc, func) ->
        val retType = func.returnType.extractFullName(tc, failOnGeneric = false)
        name to retType.removeBackticks()
    }

//    val args: List<Pair<String, String>> = abstractFunctions.map { (name, tc, func) ->
//        val retType = func.returnType.extractFullName(tc, failOnGeneric = false)
//        name to retType.removeBackticks()
//    }.fold(emptyList()) { acc, p ->
//        val rt = target.processor.elementUtils.getTypeElement(p.second.substringBefore("<"))
//        val paramType = target.processor.getClassOrPackageDataWrapper(rt) as ClassOrPackageDataWrapper.Class
//        val paramTypeName = paramType.nameResolver.getString(paramType.classProto.fqName)
//        val foundSubtype = acc.any {
//            val ot = target.processor.elementUtils.getTypeElement(it.second.substringBefore("<"))
//            val otherType = target.processor.getClassOrPackageDataWrapper(ot) as ClassOrPackageDataWrapper.Class
//            val typeTable = TypeTable(otherType.classProto.typeTable)
//            val superTypes = target.processor.recurseTypeclassInterfaces(otherType, typeTable, emptyList()).map {
//                val t = it as ClassOrPackageDataWrapper.Class
//                t.nameResolver.getString(t.classProto.fqName)
//            }
//            superTypes.contains(paramTypeName)
//        }
//        if (foundSubtype) acc
//        else acc + listOf(p)
//    }

    val expandedArgs: String = args.joinToString(separator = ", ", transform = {
        "${it.first}: ${it.second}"
    })

    val targetImplementations = args.joinToString(
            separator = "\n",
            transform = {
                "      override fun ${it.first}(): ${it.second} = ${it.first}"
            }
    )

}

class InstanceFileGenerator(
        private val generatedDir: File,
        private val annotatedList: List<AnnotatedInstance>
) {

    private val instances: List<Instance> = annotatedList.map { Instance(it.classOrPackageProto.`package`, it) }

    /**
     * Main entry point for deriving extension generation
     */
    fun generate() {
        instances.forEach {
            val elementsToGenerate: List<String> = listOf(genImplicitObject(it), genCompanionExtensions(it))
            val source: String = elementsToGenerate.joinToString(prefix = "package ${it.`package`}\n\n", separator = "\n", postfix = "\n")
            val file = File(generatedDir, instanceAnnotationClass.simpleName + ".${it.target.classElement.qualifiedName}.kt")
            file.writeText(source)
        }
    }

    private fun genImplicitObject(i: Instance): String = """
            |object ${i.implicitObjectName} {
            |  @JvmStatic fun ${i.expandedTypeArgs()} instance(${i.expandedArgs}): ${i.name}${i.expandedTypeArgs()} =
            |    object : ${i.name}${i.expandedTypeArgs()} {
            |${i.targetImplementations}
            |    }
            |}
            |""".trimMargin()

    private fun genCompanionExtensions(i: Instance): String =
            """
                |fun ${i.expandedTypeArgs()} ${i.receiverTypeName}.Companion.${i.companionFactoryName}Instance(${i.expandedArgs}): ${i.name}${i.expandedTypeArgs()} =
                |  ${i.implicitObjectName}.instance(${i.args.map { it.first }.joinToString(", ")})
                |
                |inline fun ${i.expandedTypeArgs(reified = true)} ${i.receiverTypeName}.Companion.${i.companionFactoryName}(${i.args.map {
                "${it.first}: ${it.second} = ${it.second.split(".").map { it.decapitalize() }.joinToString(".")}()"
            }.joinToString(", ")
            }): ${i.name}${i.expandedTypeArgs()} =
                |  ${i.implicitObjectName}.instance(${i.args.map { it.first }.joinToString(", ")})
                |
                |
                |""".trimMargin()

}