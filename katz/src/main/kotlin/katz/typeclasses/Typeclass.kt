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

interface Typeclass

open class GlobalInstance<T : Typeclass> {
    val type: Type
        get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]

    init {
        GlobalInstances.put(type, this)
        println("Registered $this -> $type")
    }
}

open class TypeLiteral<T> {
    val type: Type
        get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
}

inline fun <reified T> typeLiteral(): Type = object : TypeLiteral<T>() {}.type

val GlobalInstances: MutableMap<Type, GlobalInstance<*>> = ConcurrentHashMap()

inline fun <reified T : Typeclass> instance(): T = GlobalInstances.getValue(typeLiteral<T>()) as T
