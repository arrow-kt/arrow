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

package org.funktionale.playground.ds

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 4/05/15
 * Time: 2:37 PM
 */

interface FList<out T> {
    companion object {
        fun sum(ints: FList<Int>): Int = foldRight(ints, 0) { x, y ->
                x + y
            }

        fun product(ds: FList<Double>): Double = foldRight(ds, 1.0) { x, y ->
                x * y
            }

        fun <T, R> foldRight(l: FList<T>, z: R, f: (T, R) -> R): R = when (l) {
                is Nil -> z
                is Cons -> f(l.head, foldRight(l.tail, z, f))
                else -> throw IllegalArgumentException()
            }

        /*fun invoke<T>(vararg members: T): FList<T> {
            return if (members.isEmpty()) {
                Nil
            } else {
                Cons(members.first(), invoke(*members.drop(1)))
            }
        }*/
    }
}

object Nil : FList<Nothing>

data class Cons<out T>(val head: T, val tail: FList<T>) : FList<T>

interface FTree<out T> {
    fun size(): Int = when (this) {
        is Leaf -> 1
        is Branch -> 1 + left.size() + right.size()
        else -> throw IllegalStateException()
    }

    fun depth(): Int = when (this) {
        is Leaf -> 0
        is Branch -> 1 + (Math.max(left.depth(), right.depth()))
        else -> throw IllegalStateException()
    }

    fun <R> map(f: (T) -> R): FTree<R> = when (this) {
        is Leaf -> Leaf(f(value))
        is Branch -> Branch(left.map(f), right.map(f))
        else -> throw IllegalStateException()
    }

    fun <R> fold(f: (T) -> R, g: (R, R) -> R): R = when (this) {
        is Leaf -> f(value)
        is Branch -> g(left.fold(f, g), right.fold(f, g))
        else -> throw IllegalStateException()
    }

    fun sizeViaFold(): Int = fold({ 1 }) { l, f ->
        1 + l + f
    }

    fun depthViaFold(): Int = fold({ 0 }) { l, r ->
        1 + (Math.max(l, r))
    }

    fun <R> mapViaFold(f: (T) -> R): FTree<R> = fold({ Leaf(f(it)) }) { l: FTree<R>, r: FTree<R> ->
        Branch(l, r)
    }

}

fun FTree<Int>.maximun(): Int = when (this) {
    is Leaf -> value
    is Branch -> Math.max(left.maximun(), right.maximun())
    else -> throw IllegalStateException()
}

fun FTree<Int>.maximunViaFold(): Int = this.fold({ it }) { l, r ->
    Math.max(l, r)
}

data class Leaf<T>(val value: T) : FTree<T>
data class Branch<T>(val left: FTree<T>, val right: FTree<T>) : FTree<T>

open class Foo
open class Bar

