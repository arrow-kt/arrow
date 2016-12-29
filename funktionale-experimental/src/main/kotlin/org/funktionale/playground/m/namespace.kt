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

package org.funktionale.playground.m

import org.funktionale.memoization.memoize

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 13/02/16
 * Time: 9:09 PM
 */

fun main(args: Array<String>) {

    var fib: (Long) -> Long = { it }
    fib = { n: Long ->
        if (n < 2) n else fib(n - 1) + fib(n - 2)
    }
    var m: (Long) -> Long = { it }
    m = { n: Long ->
        if (n < 2) n else m(n - 1) + m(n - 2)
    }.memoize()

    println(timeElapsed { fib(40) })
    println(timeElapsed { fib_(40) })

    println(timeElapsed { m(40) })
}

fun fib_(n: Long): Long = when (n) {
        0L -> 0
        1L -> 1
        else -> {
            var a = 0L
            var b = 1L
            var c = 0L
            for (it in 2..n) {
                c = a + b
                a = b
                b = c
            }
            c
        }
    }

fun timeElapsed(body: () -> Unit): Long {
    val start = System.currentTimeMillis()
    body()
    val end = System.currentTimeMillis()
    return end - start
}
