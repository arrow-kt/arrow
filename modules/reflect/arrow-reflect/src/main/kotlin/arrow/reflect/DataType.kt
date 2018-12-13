package arrow.reflect

import kotlin.reflect.KClass

/**
 * Represents a data type providing a type class instance
 */
data class DataType(val kclass: KClass<*>)

/**
 * @return a list of [TypeClass] extensions that the receiver [DataType] provides
 */
fun DataType.extensions(): List<TypeClassExtension> =
  classPathExtensions.filter { it.dataType == this }

/**
 * @return a list of [TypeClass] that the receiver [DataType] supports
 */
fun DataType.supportedTypeClasses(): List<TypeClass> =
  extensions().map { it.typeClass }
