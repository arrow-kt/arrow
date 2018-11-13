package arrow.reflect

import arrow.extension
import io.github.classgraph.ClassGraph
import io.github.classgraph.TypeArgument

/**
 * The triple of a [dataType], a [typeClass] and an [instance]
 */
data class TypeClassExtension(
  val dataType: DataType,
  val typeClass: TypeClass,
  val instance: Instance
)

/**
 * A list of all type class instance @extensions in the classpath
 */
internal val classPathExtensions: List<TypeClassExtension> =
  ClassGraph()
    //.verbose()
    //.enableAllInfo()
    .enableClassInfo()
    .enableAnnotationInfo()
    .enableExternalClasses()
    .scan().use {
      it.getClassesWithAnnotation(extension::class.qualifiedName).mapNotNull { ext ->
        when {
          ext.typeSignature.superinterfaceSignatures.isNotEmpty() &&
            ext.typeSignature.superinterfaceSignatures[0].typeArguments.isNotEmpty() -> {
            val typeClass = ext.typeSignature.superinterfaceSignatures[0].fullyQualifiedClassName
            val dataType = ext.typeSignature.superinterfaceSignatures[0].typeArguments[0].unKind()
            TypeClassExtension(
              dataType,
              TypeClass(Class.forName(typeClass).kotlin),
              Instance(Class.forName(ext.name).kotlin)
            )
          }
          else -> {
            null
          }
        }
      }
    }

/**
 * The nasty bits. Down-kind a deeply nested kind to introspect the data type that is targeting
 */
private fun TypeArgument.unKind(): DataType =
  Class.forName(toString()
    .replace("arrow.Kind<? extends", "")
    .replace(", \\? extends \\w.*?".toRegex(), "")
    .replace("<.*".toRegex(), "")
    .replace(">", "")
    .replace(".For", ".")
    .trim()).kotlin.let(::DataType)