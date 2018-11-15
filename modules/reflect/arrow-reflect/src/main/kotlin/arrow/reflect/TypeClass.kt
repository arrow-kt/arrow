package arrow.reflect

import arrow.core.Tuple2
import arrow.core.toT
import arrow.extension
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.TypeSignature
import kotlin.reflect.KClass

/**
 * A type class such as arrow.typeclasses.Functor
 */
data class TypeClass(val kclass: KClass<*>)

/**
 * @return a list of [TypeClass] extensions that implement the receiver [TypeClass]
 */
fun TypeClass.extensions(): List<TypeClassExtension> =
  classPathExtensions.filter { it.typeClass == this }


val TypeClass.classInfo: ClassInfo
  get() = ClassGraph()
    .enableClassInfo()
    .enableAnnotationInfo()
    .enableExternalClasses()
    .enableMethodInfo()
    .scan().use {
      it.getClassInfo(kclass.java.canonicalName)
    }

fun TypeClass.hierarchy(): List<TypeClass> =
  classInfo.interfaces.map { TypeClass(Class.forName(it.name).kotlin) }

fun TypeClass.declaredMethodNamesAndTypes(): List<Tuple2<String, List<String>>> =
  classInfo.declaredMethodInfo.map {
    it.name toT it.parameterInfo.map { p ->
      p.typeSignatureOrTypeDescriptor.simplifiedToString()
    } + it.typeSignature.resultType.simplifiedToString()
  }

private fun TypeSignature.simplifiedToString(): String =
  toString()
    .replace("? extends ", "")
    .replace("? super ", "")
    .replace("java.lang.", "")
    .replace("(.*?).(\\w*?)<".toRegex(), "$2<")

/**
 * @return a list of [DataType] that provide instances for the [TypeClass]
 */
fun TypeClass.supportedDataTypes(): List<DataType> =
  extensions().map { it.dataType }