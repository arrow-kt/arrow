/*
 * Copyright 2013 - 2017 Mario Arias
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

package org.funktionale.state

import org.funktionale.collections.prependTo

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 29/05/17
 * Time: 12:54 AM
 */
class State<S, out T>(val run: (S) -> Pair<S, T>) {

	fun <R> map(f: (T) -> R): State<S, R> = flatMap { t -> pure<S, R>(f(t)) }

	fun <P1, R> map(sx: State<S, P1>, f: (T, P1) -> R): State<S, R> = flatMap { t -> sx.map { x -> f(t, x) } }

	fun <R> flatMap(f: (T) -> State<S, R>): State<S, R> = State { s ->
		val (s1, t) = run(s)
		f(t).run(s1)
	}

	companion object {
		fun <S, T> pure(t: T): State<S, T> = State { s -> s to t }

		fun <S> get(): State<S, S> = State { s -> s to s }

		fun <S> set(s: S): State<S, Unit> = State { s to Unit }

		fun <S> modify(f: (S) -> S): State<S, Unit> = get<S>().flatMap { s: S -> set(f(s)).map { Unit } }
	}
}

fun <R, S, T> List<T>.stateTraverse(f: (T) -> State<S, R>): State<S, List<R>> = foldRight(State.pure(emptyList())) { i: T, accumulator: State<S, List<R>> ->
		f(i).map(accumulator) { head: R, tail: List<R> ->
			head prependTo tail
		}
	}

fun <S, T> List<State<S, T>>.stateSequential(): State<S, List<T>> = stateTraverse { it }