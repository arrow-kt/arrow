package arrow.reflect

import kotlin.reflect.KClass

/**
 * A type class instance pairing a type class contract with a data type implementation of such contract
 */
data class Extension(val kclass: KClass<*>)