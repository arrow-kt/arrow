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

package org.funktionale.playground.st

import org.funktionale.collections.prependTo
import org.funktionale.either.Disjunction
import org.funktionale.either.disjunctionTry
import org.funktionale.either.flatMap
import org.funktionale.validation.validate

class State<S, out T>(val run: (S) -> Pair<T, S>) {

    fun <R> map(f: (T) -> R): State<S, R> = flatMap { t -> unit<S, R>(f(t)) }

    fun <P1, R> map(sx: State<S, P1>, f: (T, P1) -> R): State<S, R> = flatMap { t -> sx.map { x -> f(t, x) } }

    fun <R> flatMap(f: (T) -> State<S, R>): State<S, R> = State { s ->
        val (t, s1) = run(s)
        f(t).run(s1)
    }


    companion object {
        fun <S, T> unit(t: T): State<S, T> = State { s -> t to s }

        fun <S> get(): State<S, S> = State { s -> s to s }

        fun <S> set(s: S): State<S, Unit> = State { s -> Unit to s }

        fun <S> modify(f: (S) -> S): State<S, Unit> = get<S>().flatMap { s: S -> set(f(s)).map { Unit } }
    }
}

fun <R, S, T> List<T>.stateTraverse(f: (T) -> State<S, R>): State<S, List<R>> {
    return foldRight(State.unit(emptyList())) { i: T, accumulator: State<S, List<R>> ->
        f(i).map(accumulator) { head: R, tail: List<R> ->
            head prependTo tail
        }
    }
}

fun <L, R> List<State<L, R>>.stateSequential(): State<L, List<R>> = stateTraverse { it }


sealed class Input {
    object Coin : Input()
    object Turn : Input()
}

data class Machine(val locked: Boolean, val candies: Int, val coins: Int)

object Candy {
    fun simulateMachine(inputs: List<Input>): State<Machine, Pair<Int, Int>> = inputs.map { i ->
        State.modify { s: Machine ->
            when {
                s.candies == 0 -> s
                i is Input.Coin -> when (s.locked) {
                    true -> s.copy(locked = false, coins = s.coins + 1)
                    false -> s
                }
                i is Input.Turn -> when (s.locked) {
                    true -> s
                    false -> s.copy(locked = true, candies = s.candies - 1)
                }
                else -> TODO("Not implemented")
            }
        }
    }.stateSequential().flatMap { State.get<Machine>().map { s: Machine -> s.coins to s.candies } }
}


fun divide(num: Int, den: Int): Int? {
    return if (num % den != 0) {
        null
    } else {
        num / den
    }
}

fun division(a: Int, b: Int, c: Int): Pair<Int, Int>? {
    val ac = divide(a, c)
    return when (ac) {
        is Int -> {
            val bc = divide(b, c)
            when (bc) {
                is Int -> ac to bc
                else -> null
            }
        }
        else -> null
    }
}


fun main(args: Array<String>) {

    data class User(val name: String)

    fun getUser(url: String): Disjunction<Exception, User> = disjunctionTry {
        User("Mario")
    }

    val disjunction = getUser("http://myapi.com/user/1")
    when (disjunction) {
        is Disjunction.Left -> println(disjunction.swap().get().message)
        is Disjunction.Right -> println(disjunction
                .map { it.copy(name = it.name.capitalize()) }
                .get())
    }

    val user1 = getUser("http://myapi.com/user/1")
    val user2 = getUser("http://myapi.com/user/2")

    val userList: Disjunction<Exception, List<User>> = user1.flatMap { u1 ->
        user2.map { u2 ->
            listOf(u1, u2)
        }
    }

    val userList2: Disjunction<List<Exception>, List<User>> = validate(user1, user2) { u1, u2 ->
        listOf(u1, u2)
    }


}