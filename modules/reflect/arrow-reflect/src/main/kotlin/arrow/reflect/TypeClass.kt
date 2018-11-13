package arrow.reflect

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

/**
 * @return a list of [DataType] that provide instances for the [TypeClass]
 */
fun TypeClass.supportedDataTypes(): List<DataType> =
  extensions().map { it.dataType }