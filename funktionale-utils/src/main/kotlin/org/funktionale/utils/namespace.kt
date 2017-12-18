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

fun <T> identity(): (T) -> T = { t: T -> t }

fun <P1, T> constant(t: T): (P1) -> T = { _: P1 -> t }

typealias Predicate<T> = (T) -> Boolean

fun <T : Any> Predicate<T>.mapNullable(): (T?) -> Boolean = { it?.let { this@mapNullable(it) } ?: false }

inline fun <T> T?.hashCodeForNullable(i: Int, f: (Int, Int) -> Int): Int = when (this) {
        null -> i
        else -> f(i, this.hashCode())
    }