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

package org.funktionale.playground.lz

import org.funktionale.collections.prependTo
import org.funktionale.memoization.memoize
import org.funktionale.option.Option

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 19/11/16
 * Time: 3:46 PM
 */

fun <T> if2(cond: Boolean, onTrue: () -> T, onFalse: () -> T): T = if (cond) onTrue() else onFalse()

fun main(args: Array<String>) {
    val stream = Stream(*(1..1000).toList().toTypedArray())
    val toList = timeElapsed {
        stream.toList().forEach(::println)
    }
    val toFastList = timeElapsed {
            stream.toFastList().forEach(::println)
        }
    println("toList = $toList")
    println("toFastList = $toFastList")
}

fun timeElapsed(body: () -> Unit): Long {
    val start = System.currentTimeMillis()
    body()
    val end = System.currentTimeMillis()
    return end - start
}

sealed class Stream<out T : Any> {

    fun headOption(): Option<T> = when (this) {
        is Stream.Empty -> Option.None
        is Stream.Cons -> Option.Some(head())
    }

    fun toList(): List<T> {
        fun go(s: Stream<T>, acc: List<T>): List<T> = when (s) {
            is Stream.Empty -> acc
            is Stream.Cons -> go(s.tail(), s.head() prependTo acc)
        }

        return go(this, listOf()).reversed()
    }

    fun toFastList(): List<T>{
        val buf = arrayListOf<T>()
        fun go(s:Stream<T>):List<T> = when(s){
            is Stream.Empty -> buf
            is Stream.Cons -> {
                buf.add(s.head())
                go(s.tail())
            }
        }
        return go(this)
    }

    class Empty : Stream<Nothing>()
    class Cons<out T : Any>(val head: () -> T, val tail: () -> Stream<T>) : Stream<T>()
    companion object {
        fun <T : Any> empty(): Stream<T> = Empty()
        fun <T : Any> cons(head: () -> T, tail: () -> Stream<T>): Stream<T> = Cons(head.memoize(), tail.memoize())
        operator fun <T : Any> invoke(vararg t: T): Stream<T> {
            return if (t.isEmpty()) {
                empty()
            } else {
                cons({ t.first() }) { invoke(t.drop(1)) }
            }
        }

        operator fun <T : Any> invoke(t: List<T>): Stream<T> {
            return if (t.isEmpty()) {
                empty()
            } else {
                cons({ t.first() }) { invoke(t.drop(1)) }
            }
        }

    }
}