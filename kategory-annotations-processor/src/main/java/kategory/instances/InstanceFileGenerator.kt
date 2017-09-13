package kategory.instances

import java.io.File
import kategory.common.Package
import kategory.common.utils.ClassOrPackageDataWrapper
import kategory.common.utils.extractFullName
import kategory.common.utils.removeBackticks
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.serialization.ProtoBuf
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.deserialization.supertypes

data class Instance(
        val `package`: Package,
        val target: AnnotatedInstance
) {
    val name = target.classElement.simpleName

    val implicitObjectName: String = "${name}Implicits"

    val expandedTypeArgs: String =
            target.classOrPackageProto.typeParameters.joinToString(
                    prefix = "<",
                    separator = ", ",
                    transform = { target.classOrPackageProto.nameResolver.getString(it.name) },
                    postfix = ">"
            )

    private val abstractFunctions: List<Pair<ClassOrPackageDataWrapper, ProtoBuf.Function>> = try {
        (target.superTypes + listOf(target.classOrPackageProto)).flatMap { tc ->
            tc.functionList.filter { it.modality == ProtoBuf.Modality.ABSTRACT }.flatMap {
                val retType = target.processor.elementUtils.getTypeElement(
                        it.returnType.extractFullName(tc, failOnGeneric = false).removeBackticks().substringBefore("<")
                )
                if (retType != null) {
                    val current = target.processor.getClassOrPackageDataWrapper(retType) as ClassOrPackageDataWrapper.Class
                    val typeTable = TypeTable(current.classProto.typeTable)
                    val isTypeClassReturnType = current.classProto.supertypes(typeTable).any {
                        val typeName = it.extractFullName(current, failOnGeneric = false)
                        typeName == "`kategory`.`Typeclass`"
                    }
                    if (isTypeClassReturnType)
                        listOf(tc to it)
                    else
                        emptyList()
                } else {
                    emptyList()
                }
            }
        }
    } catch (e: Throwable) {
        throw e
    }

    val args: List<Pair<Name, String>> = abstractFunctions.map { (tc, func) ->
        val name = tc.nameResolver.getName(func.name)
        val retType = func.returnType.extractFullName(tc, failOnGeneric = false)
        name to retType.removeBackticks()
    }

    val expandedArgs: String = args.joinToString(separator = ", ", transform = {
        "${it.first}: ${it.second}"
    })

    val targetImplementations = args.joinToString(
            separator = "\n\n",
            transform = {
                "override fun ${it.first}(): ${it.second} = ${it.first}"
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
            val elementsToGenerate: List<String> = listOf(genImplicitObject(it))
            val source: String = elementsToGenerate.joinToString(prefix = "package ${it.`package`}\n\n", separator = "\n", postfix = "\n")
            val file = File(generatedDir, instanceAnnotationClass.simpleName + ".${it.target.classElement.qualifiedName}.kt")
            file.writeText(source)
        }
    }

    private fun genImplicitObject(i: Instance): String = """
            |object ${i.implicitObjectName} {
            |  @JvmStatic fun ${i.expandedTypeArgs} instance(${i.expandedArgs}): ${i.name}${i.expandedTypeArgs} =
            |    object : ${i.name}${i.expandedTypeArgs} {
            |      ${i.targetImplementations}
            |    }
            |}
            |""".trimMargin()

}