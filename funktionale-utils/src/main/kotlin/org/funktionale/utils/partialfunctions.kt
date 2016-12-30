/*
 * Copyright 2013 - 2016 Mario Arias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.funktionale.utils

class PartialFunction<in P1, out R>(private val definetAt: (P1) -> Boolean, private val f: (P1) -> R) : Function1<P1, R> {
    override fun invoke(p1: P1): R {
        if(definetAt(p1)) {
            return f(p1)
        } else {
            throw IllegalArgumentException("Value: ($p1) isn't supported by this function")
        }
    }

    fun isDefinedAt(p1: P1) = definetAt(p1)
}

fun <P1, R> PartialFunction<P1, R>.invokeOrElse(p1: P1, default: R): R {
    return if (this.isDefinedAt(p1)) {
        this(p1)
    } else {
        default
    }
}

infix fun <P1, R> PartialFunction<P1, R>.orElse(that: PartialFunction<P1, R>): PartialFunction<P1, R> {
    return PartialFunction({ this.isDefinedAt(it) || that.isDefinedAt(it) }) {
        when {
            this.isDefinedAt(it) -> this(it)
            that.isDefinedAt(it) -> that(it)
            else -> throw IllegalArgumentException("function not definet for parameter ($it)")
        }
    }
}

fun <P1, R> Function1<P1, R>.toPartialFunction(definedAt: (P1) -> Boolean): PartialFunction<P1, R> {
    return PartialFunction(definedAt, this)
}