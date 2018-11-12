package arrow.reflect

import arrow.effects.IO
import arrow.extension
import io.github.classgraph.ClassGraph
import io.github.classgraph.TypeArgument
import kotlin.reflect.KClass

object Extensions {
  @JvmStatic
  fun main(args: Array<String>) {
    println(DataType(IO::class).extensionsTable())
  }
}

private fun TypeArgument.unKind(): String =
  toString()
    .replace("arrow.Kind<? extends", "")
    .replace(", \\? extends \\w.*?".toRegex(), "")
    .replace("<.*".toRegex(), "")
    .replace(">", "")
    .replace(".For", ".")
    .trim()

val classPathExtensions: List<TypeClassExtension> =
  ClassGraph()
    .verbose()                   // Log to stderr
    .enableAllInfo()             // Scan classes, methods, fields, annotations
    .enableExternalClasses()
    .scan().use {
      it.getClassesWithAnnotation(extension::class.qualifiedName).mapNotNull { ext ->
        when {
            ext.typeSignature.superinterfaceSignatures.isNotEmpty() &&
            ext.typeSignature.superinterfaceSignatures[0].typeArguments.isNotEmpty() -> {
            val typeClass = ext.typeSignature.superinterfaceSignatures[0].fullyQualifiedClassName
            val dataType = ext.typeSignature.superinterfaceSignatures[0].typeArguments[0].unKind()
            TypeClassExtension(
              DataType(Class.forName(dataType).kotlin),
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

data class DataType(val kclass: KClass<*>)
data class TypeClass(val kclass: KClass<*>)
data class Instance(val kclass: KClass<*>)

data class TypeClassExtension(
  val dataType: DataType,
  val typeClass: TypeClass,
  val instance: Instance
)

fun DataType.extensions(): List<TypeClassExtension> =
  classPathExtensions.filter { it.dataType == this }

fun DataType.supportedTypeClasses(): List<TypeClass> =
  extensions().map { it.typeClass }

fun DataType.extensionsTable(): String =
    extensions().fold("|Type Class|Extension|") { prevLine, ext ->
      prevLine + "\n" + "|${ext.typeClass.kclass.qualifiedName}|${ext.instance.kclass.qualifiedName}|"
    }

fun TypeClass.extensions(): List<TypeClassExtension> =
  classPathExtensions.filter { it.typeClass == this }

fun TypeClass.supportedDataTypes(): List<DataType> =
  extensions().map { it.dataType }



