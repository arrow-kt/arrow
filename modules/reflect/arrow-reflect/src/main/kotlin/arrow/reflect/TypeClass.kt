package arrow.reflect

import arrow.Kind
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

/**
 * A type class such as arrow.typeclasses.Functor
 */
data class TypeClass(val kclass: KClass<*>)

/**
 * @return a list of [TypeClass] extensions that implement the receiver [TypeClass]
 */
fun TypeClass.extensions(): List<TypeClassExtension> =
  classPathExtensions.filter { it.typeClass == this }

private fun TypeClass.declaredSuperInterfaces(): List<TypeClass> =
  kclass.superclasses
    .filterNot { Any::class == it || Kind::class == it }
    .map(::TypeClass)

data class Extends(val a: TypeClass, val b: TypeClass)

fun TypeClass.extends(other: TypeClass): Extends? =
  if (kclass.isSubclassOf(other.kclass) && this != other)
    Extends(this, other)
  else null

fun TypeClass.hierarchy(): List<Extends> {
  val superInterfaces: List<TypeClass> = declaredSuperInterfaces()
  val localExtensions: List<Extends> = superInterfaces.mapNotNull { this.extends(it) }
  return (localExtensions + superInterfaces.flatMap(TypeClass::hierarchy)).distinct()
}

/**
 * @return a list of [DataType] that provide instances for the [TypeClass]
 */
fun TypeClass.supportedDataTypes(): List<DataType> =
  extensions().map { it.dataType }
