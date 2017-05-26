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
import org.funktionale.collections.tail
import org.funktionale.playground.m.timeElapsed
import org.funktionale.playground.st.Direction.*
import org.funktionale.playground.st.Instruction.*

class State<S, out T>(val run: (S) -> Pair<S, T>) {

	fun <R> map(f: (T) -> R): State<S, R> = flatMap { t -> unit<S, R>(f(t)) }

	fun <P1, R> map(sx: State<S, P1>, f: (T, P1) -> R): State<S, R> = flatMap { t -> sx.map { x -> f(t, x) } }

	fun <R> flatMap(f: (T) -> State<S, R>): State<S, R> = State { s ->
		val (s1, t) = run(s)
		f(t).run(s1)
	}


	companion object {
		fun <S, T> unit(t: T): State<S, T> = State { s -> s to t }

		fun <S> get(): State<S, S> = State { s -> s to s }

		fun <S> set(s: S): State<S, Unit> = State { s to Unit }

		fun <S, T> gets(f: (S) -> T): State<S, T> = State { s -> s to f(s) }

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
	}.stateSequential().flatMap { State.get<Machine>().map { (_, candies, coins) -> coins to candies } }
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

	/*data class User(val name: String)

	fun getUser(url: String): Disjunction<Throwable, User> = disjunctionTry {
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

	val userList: Disjunction<Throwable, List<User>> = user1.flatMap { u1 ->
		user2.map { u2 ->
			listOf(u1, u2)
		}
	}

	val userList2: Disjunction<List<Throwable>, List<User>> = validate(user1, user2) { u1, u2 ->
		listOf(u1, u2)
	}*/

	println(timeElapsed { println("knapsack " + knapsack(9, listOf(3, 3, 5, 3, 7), listOf(1, 2, 4, 2, 3))) })
	println(timeElapsed { println("knapsack2 " + knapsack2(9, listOf(3, 3, 5, 3, 7), listOf(1, 2, 4, 2, 3))) })
	println(timeElapsed { println("knapsack3 " + knapsack3(9, listOf(3, 3, 5, 3, 7), listOf(1, 2, 4, 2, 3))) })
	println(timeElapsed { println("knapsack4 " + knapsack4(9, listOf(3, 3, 5, 3, 7), listOf(1, 2, 4, 2, 3))) })
}

fun fill(n1: Int, n2: Int, f: () -> Int): MutableList<MutableList<Int>> {
	val l1 = arrayListOf<MutableList<Int>>()
	(1..n1).forEach { i ->
		val ln = arrayListOf<Int>()
		(1..n2).forEach { j -> ln.add(f()) }
		l1.add(ln)
	}
	return l1
}

fun fill(n1: Int, f: () -> Int): MutableList<Int> {
	val l1 = arrayListOf<Int>()
	(1..n1).forEach { i ->
		l1.add(f())
	}
	return l1
}

fun knapsack(maxWeight: Int, value: List<Int>, weight: List<Int>): Int {
	val n = value.size;
	val solutions = fill(n + 1, maxWeight + 1) { 0 }
	(1..n).forEach { i ->
		(1..maxWeight).forEach { j ->
			solutions[i][j] = if (j - weight[i - 1] >= 0) {
				Math.max(solutions[i - 1][j], solutions[i - 1][j - weight[i - 1]] + value[i - 1])
			} else {
				solutions[i - 1][j]
			}
		}
	}
	return solutions[n][maxWeight]
}

fun knapsack2(maxWeight: Int, value: List<Int>, weight: List<Int>): Int {
	val n = value.size
	var solutions = fill(maxWeight + 1) { 0 }
	(1..n).forEach { i ->
		val newSolutions = fill(maxWeight + 1) { 0 }
		(1..maxWeight).forEach { j ->
			newSolutions[j] = if (j - weight[i - 1] >= 0) {
				Math.max(solutions[j], solutions[j - weight[i - 1]] + value[i - 1])
			} else {
				solutions[j]
			}
		}
		solutions = newSolutions
	}
	return solutions[maxWeight]
}

fun knapsack3(maxWeight: Int, value: List<Int>, weight: List<Int>): Int {
	val firstRow = fill(maxWeight + 1) { 0 }
	val lastRow = (1..value.size).fold(firstRow) { upperRow, i ->
		(0 prependTo (1..maxWeight).map { j ->
			if (j - weight[i - 1] >= 0) {
				Math.max(upperRow[j], upperRow[j - weight[i - 1]] + value[i - 1])
			} else {
				upperRow[j]
			}
		}).toMutableList()
	}
	return lastRow.last()
}

fun knapsack4(maxWeight: Int, value: List<Int>, weight: List<Int>): Int {
	val n = value.size
	val initialState = fill(maxWeight + 1) { 0 }
	val st: State<List<Int>, List<Unit>> = (1..n).toList().stateTraverse { i ->
		val x: State<List<Int>, Unit> = State.get<List<Int>>().flatMap { solutions ->
			val newSolutions = (0 prependTo (1..maxWeight).map { j ->
				if (j - weight[i - 1] >= 0) {
					Math.max(solutions[j], solutions[j - weight[i - 1]] + value[i - 1])
				} else {
					solutions[j]
				}
			}).toList()
			State.set(newSolutions)
		}
		x
	}
	val (s, t) = st.run(initialState)
	return s[maxWeight]
}

sealed class Instruction {
	object L : Instruction()
	object R : Instruction()
	object A : Instruction()
}

sealed class Direction {
	abstract fun turn(i: Instruction): Direction

	object North : Direction() {
		override fun turn(i: Instruction): Direction = when (i) {
			L -> West
			R -> East
			else -> this
		}
	}

	object South : Direction() {
		override fun turn(i: Instruction): Direction = when (i) {
			L -> East
			R -> West
			else -> this
		}
	}

	object East : Direction() {
		override fun turn(i: Instruction): Direction = when (i) {
			L -> North
			R -> South
			else -> this
		}
	}

	object West : Direction() {
		override fun turn(i: Instruction): Direction = when (i) {
			L -> South
			R -> North
			else -> this
		}
	}
}

data class Point(val x: Int, val y: Int)

data class Position(val point: Point, val dir: Direction) {

	constructor(x: Int, y: Int, dir: Direction) : this(Point(x, y), dir)

	fun move(s: Playground): Position {
		val newPosition = when (dir) {
			North -> copy(point = point.copy(y = point.y + 1))
			South -> copy(point = point.copy(y = point.y - 1))
			East -> copy(point = point.copy(x = point.x + 1))
			West -> copy(point = point.copy(x = point.x - 1))
		}
		return if (s.isPossiblePosition(newPosition)) newPosition else this
	}

	fun turn(instruction: Instruction): Position = copy(dir = dir.turn(instruction))

}

sealed class Player {
	object R1 : Player()
	object R2 : Player()
}

data class Score(val player: Player,
				 val score: Int)


data class Robot(val player: Player,
				 val positions: List<Position>,
				 val coins: List<Point> = listOf()) {
	val currentPosition: Position by lazy {
		positions.first()
	}

	val score: Score by lazy {
		Score(player, coins.size)
	}

	fun addPosition(next: Position) = copy(positions = next prependTo positions)

	fun addCoin(coin: Point) = copy(coins = coin prependTo coins)
}

data class Playground(val bottomLeft: Point,
					  val topRight: Point,
					  val coins: Set<Point>,
					  val r1: Robot, val r2: Robot) {

	fun isInPlayground(point: Point): Boolean = bottomLeft.x <= point.x && point.x <= topRight.x && bottomLeft.y <= point.y && point.y <= topRight.y

	val scores: Pair<Score, Score> by lazy {
		r1.score to r2.score
	}

	fun isPossiblePosition(pos: Position): Boolean = isInPlayground(pos.point) && r2.currentPosition.point != pos.point

	fun swapRobots(): Playground = copy(r1 = r2, r2 = r1)
}

fun processInstructions(i: Instruction, s: Playground): Playground {
	val next = when (i) {
		A -> s.r1.currentPosition.move(s)
		else -> s.r1.currentPosition.turn(i)
	}

	return if (s.coins.contains(next.point)) {
		s.copy(coins = s.coins - next.point, r1 = s.r1.addCoin(next.point).addPosition(next))
	} else {
		s.copy(r1 = s.r1.addPosition(next))
	}
}


fun compileInstructions(i1: List<Instruction>, i2: List<Instruction>): State<Playground, Pair<Score, Score>> {
	return when {
		i1.isEmpty() && i2.isEmpty() -> State.gets { it.scores }
		i1.isEmpty() -> State<Playground, Pair<Score, Score>> { s -> s.swapRobots() to s.scores }.flatMap { compileInstructions(i2, i1) }
		else -> {
			val head = i1.first()
			val tail = i1.tail()
			State<Playground, Pair<Score, Score>> { s ->
				val s1 = processInstructions(head, s)
				s1.swapRobots() to s1.scores
			}.flatMap { compileInstructions(i2, tail) }
		}
	}
}

fun declareWinners(scores: Pair<Score, Score>): String {
	val (winner, looser) = if (scores.first.score > scores.second.score) scores else scores.second to scores.first

	return "Robot ${winner.player} wins against ${looser.player} with a score ${winner.score} over ${looser.score}"
}

fun testForComprehension(i1: List<Instruction>, i2: List<Instruction>): State<Playground, Pair<String, Pair<Position, Position>>> {
	fun getPositions(p: Playground) = p.r1.currentPosition to p.r2.currentPosition

	return compileInstructions(i1, i2).flatMap { scores ->
		State.gets(::getPositions).map { position -> declareWinners(scores) to position }
	}
}

infix operator fun <T> Set<T>.minus(t: T): Set<T> {
	val mut = toMutableSet()
	mut.remove(t)
	return mut
}

