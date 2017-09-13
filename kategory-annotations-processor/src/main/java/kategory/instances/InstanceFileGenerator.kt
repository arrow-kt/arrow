package kategory.instances

import java.io.File
import kategory.common.Package
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.serialization.ProtoBuf

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

    val abstractFunctions: List<ProtoBuf.Function> = target.classOrPackageProto.functionList.filter {
        it.modality == ProtoBuf.Modality.ABSTRACT
    }

    val factoryArgs: List<String> = emptyList()

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
            |  @JvmStatic fun ${i.expandedTypeArgs} instance(${i.factoryArgs}): ${i.name}${i.expandedTypeArgs} =
            |    object : ${i.name}${i.expandedTypeArgs} {
            |
            |    }
            |}
            |""".trimMargin()

}