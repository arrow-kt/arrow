/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

/**
 * Marker trait that all Functional typeclasses such as Monad, Functor, etc... must implement to be considered
 * candidates to pair with global instances
 */
interface Typeclass

/**
 * A parametrized type calculated from walking up the interface chain in a Typeclass and finding other relevant
 * typeclasses that should be registered
 */
data class InstanceParametrizedType(val raw: Type, val typeArgs: List<Type>) : ParameterizedType {
    override fun getRawType(): Type = raw

    override fun getOwnerType(): Type = null as Type

    override fun getActualTypeArguments(): Array<Type> = typeArgs.toTypedArray()
}

/**
 * Auto registers subtypes as a global instance for all the functional typeclass interfaces they implement
 */
open class GlobalInstance<T : Typeclass>() {

    init {
        recurseInterfaces(javaClass)
    }

    /**
     * The original typeclass parametrization that trigger this interface lookup
     */
    val type: ParameterizedType
        get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as ParameterizedType

    /**
     * REcursively scan all implemented interfaces and add as global instances all the ones that match a Typeclass
     */
    fun recurseInterfaces(c : Class<*>) : Unit {
        return when {
            c.interfaces.isEmpty() -> println("$c has no interfaces")
            else -> {
                c.interfaces.filter {
                    it != Typeclass::class.java && Typeclass::class.java.isAssignableFrom(it)
                }.forEach { i ->
                    val instanceType = InstanceParametrizedType(i, listOf(type.actualTypeArguments[0]))
                    GlobalInstances.put(type, this)
                    println("registered global instance for type class : $instanceType")
                    recurseInterfaces(i)
                }
            }
        }
    }

}

/**
 * Instrospects the generic type arguments to locate generic interfaces and their type args
 */
open class TypeLiteral<T> {
    val type: Type
        get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
}

inline fun <reified T> typeLiteral(): Type = object : TypeLiteral<T>() {}.type

/**
 * A concurrent hash map of local global instances that may be invoked at runtime as if they were implicitly summoned
 */
val GlobalInstances: MutableMap<Type, GlobalInstance<*>> = ConcurrentHashMap()

/**
 * Obtains a global registered typeclass instance when fast unsafe runtime lookups are desired over passing instances
 * as args to functions. Use with care. This lookup will throw an exception if a typeclass is summoned and can't be found
 * in the GlobalInstances map
 */

data class TypeClassInstanceNotFound(val type : Type)
    : RuntimeException("$type not found in Global Typeclass Instances registry. " +
        "Please ensure your instances implement `GlobalInstance<$type>` for automatic registration." +
        "Alternatively invoke `GlobalInstances.put(typeLiteral<$type>(), instance)` if you wish to register " +
        "or override a typeclass manually")

inline fun <reified T : Typeclass> instance(): T = GlobalInstances.getValue(typeLiteral<T>()) as T
