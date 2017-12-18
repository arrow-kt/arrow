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

package org.funktionale.playground.gs

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/04/15
 * Time: 10:53 PM
 */

fun abs(n: Int): Int = if (n < 0) -n else n

private fun formatAbs(x: Int) = "The absolute value of $x is ${abs(x)}"

fun factorial_(n: Long): Long {
    var result = 1L
    (1..n).forEach { result *= it }
    return result
}

fun factorial(n: Long): Long {
    tailrec fun go(n: Long, acc: Long): Long {
        return if (n <= 0) {
            acc
        } else {
            go(n - 1, n * acc)
        }
    }
    return go(n, 1)
}

//private fun formatFactorial(n: Long) = "The factorial of $n is ${factorial(n)}"

fun fib_(n: Long): Long = when (n) {
    0L -> 0
    1L -> 1
    else -> {
        var a = 0L
        var b = 1L
        var c = 0L
        (2..n).forEach {
            c = a + b
            a = b
            b = c
        }
        c
    }
}

fun fib(n: Long): Long {

    tailrec fun go(n: Long, prev: Long, cur: Long): Long {
        return if (n == 0L) prev
        else go(n - 1, cur, prev + cur)

    }
    return go(n, 0, 1)
}

fun <T> formatResult(name: String, n: T, f: (T) -> T) = "the $name of $n is ${f(n)}"

fun main(args: Array<String>) {

    println(formatResult("absolute value", -42, ::abs))
    println(formatResult("factorial", 20, ::factorial_))
    println(formatResult("factorial", 20, ::factorial))
    println(formatResult("fibonnaci", 92, ::fib_))
    println(formatResult("fibonnaci", 92, ::fib))
    println(formatResult("increment", 9) { x: Int -> x + 1 })
    println(formatResult("increment", 7) { x: Int -> x + 1 })
    println(formatResult("increment", 7, { it + 1 }))
    println(formatResult("increment", 7) { it + 1 })
}