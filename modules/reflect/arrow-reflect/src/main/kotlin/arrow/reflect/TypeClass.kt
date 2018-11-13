package arrow.reflect

import arrow.extension
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
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
    .scan().use {
      it.getClassInfo(kclass.java.canonicalName)
    }

fun TypeClass.hierarchy(): List<TypeClass> =
  classInfo.interfaces.map { TypeClass(Class.forName(it.name).kotlin) }

/**
 * @return a list of [DataType] that provide instances for the [TypeClass]
 */
fun TypeClass.supportedDataTypes(): List<DataType> =
  extensions().map { it.dataType }