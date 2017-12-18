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

package org.funktionale.playground.nn

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/07/15
 * Time: 4:03 PM
 */

fun nonlin(x: Double, deriv: Boolean = false): Double = if (deriv) {
        x * (1 - x)
    } else {
        1 / (1 + StrictMath.exp(-x))
    }

fun <I : Comparable<I>, T : Thing<I>> load(thing: T, id: I): T? = when (thing) {
       is Any -> Egal() as T

       else -> null
   }

fun main(args: Array<String>) {
    load(Egal(),"")
}

abstract class Thing<I : Comparable<I>>

class Egal : Thing<String>()

class Bar : Thing<Int>() {
    companion object
}