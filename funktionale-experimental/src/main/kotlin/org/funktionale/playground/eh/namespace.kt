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

package org.funktionale.playground.eh

import org.funktionale.either.Either
import org.funktionale.either.Either.Left
import org.funktionale.either.Either.Right

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 4/07/15
 * Time: 1:05 PM
 */

/*fun mean(xs: Collection<Double>): Double {
    if (xs.isEmpty()) {
        throw ArithmeticException("mean of empty collection")
    } else {
        return xs.sum() / xs.size()
    }
}*/

/*fun mean(xs: Collection<Double>, onEmpty: Double): Double {
    return if (xs.isEmpty()) {
        onEmpty
    } else {
        xs.sum() / xs.size()
    }
}

fun mean(xs: Collection<Double>): Option<Double> {
    return if (xs.isEmpty()) {
        None
    } else {
        Some(xs.sum() / xs.size())
    }
}

fun variance(xs: Collection<Double>): Option<Double> {
    return mean(xs).flatMap { m ->
        mean(xs.map { x ->
            Math.pow(x - m, 2.0)
        })
    }
}

fun<P1, R> ((P1) -> R).lift(): (Option<P1>) -> Option<R> {
    return { it.map(this) }
}

fun<T> Collection<Option<T>>.sequence(): Option<Collection<T>> {
    return if (this.isEmpty()) {
        Some(emptyList<T>())
    } else {
        first().flatMap { hh -> tail().sequence().map { hh concat it } }
    }
}

fun<T> Collection<Option<T>>.sequence2(): Option<Collection<T>> {
    return toList().foldRight(Some(emptyList())){ x: Option<T>, y: Option<Collection<T>> ->
        x.map(y){i1,i2 ->
            i1 concat i2
        }
    }
}

fun<T> Collection<Option<T>>.sequence3(): Option<Collection<T>> {
    return this.traverse { it }
}


fun<T,R> Collection<T>.traverse(f:(T) -> Option<R>):Option<Collection<R>>{
    return toList().foldRight(Some(emptyList())){x, y ->
        f(x).map(y){ i1,i2 ->
            i1 concat i2
        }
    }
}


fun<T> Iterable<T>.tail(): Collection<T> {
    return this.drop(1)
}

fun<T> T.concat(rest: Collection<T>): Collection<T> {
    return listOf(this) + rest
}

fun parseInts(a: Collection<String>): Option<Collection<Int>> {
    return a.map { Try { it.toInt() } }.sequence3()
}*/

/*fun map2<P1,P2,R>(a:Option<P1>,b:Option<P2>,f:(P1,P2) -> R):Option<R>{
    return a.flatMap { aa -> b.map { bb -> f(aa,bb) } }
}*/

/*fun parseInsuranceRateQuotes(age: String, numberOfSpeedingTickets: String): Option<Double> {
    val optAge = Try { age.toInt() }
    val optTickers = Try {numberOfSpeedingTickets.toInt()}
}*/

/*fun Try<T>(body: () -> T): Option<T> {
    return try {
        Some(body())
    } catch(e: Exception) {
        None
    }
}*/

fun mean(xs: Collection<Double>): Either<String, Double> = if (xs.isEmpty()) {
        Left("mean of empty list!")
    } else {
        Right(xs.sum() / xs.size)
    }

//fun main(args: Array<String>) {
//    /*val abs: (Double) -> Double = { i: Double -> Math.abs(i) }
//    val liftAbs: (Option<Double>) -> Option<Double> = abs.lift()
//    println(liftAbs((-2.0).toOption()))*/
//    //println(parseInts(listOf("1", "2", "3")))
//}