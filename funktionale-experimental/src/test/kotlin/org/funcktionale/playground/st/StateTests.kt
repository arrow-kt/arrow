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

package org.funcktionale.playground.st

import org.funktionale.playground.st.*
import org.funktionale.playground.st.Direction.East
import org.funktionale.playground.st.Direction.West
import org.funktionale.playground.st.Instruction.A
import org.funktionale.playground.st.Instruction.R
import org.funktionale.playground.st.Player.R1
import org.funktionale.playground.st.Player.R2
import org.testng.Assert.assertEquals
import org.testng.annotations.Test


class StateTests {

	val simplePlayGroundStore = Playground(Point(0, 0), Point(3, 3),
			emptySet(),
			Robot(R1, listOf(Position(0, 0, Direction.North))),
			Robot(R2, listOf(Position(3, 3, Direction.South))))

	val playWithCoins = Playground(Point(0, 0), Point(3, 3),
			setOf(Point(0, 1), Point(1, 2), Point(3, 1)),
			Robot(R1, listOf(Position(0, 0, Direction.North))),
			Robot(R2, listOf(Position(3, 3, Direction.South))))

	@Test fun `return initial state if no instruction is provided`() {
		val (state: Playground, scores: Pair<Score, Score>) = compileInstructions(listOf(), listOf()).run(simplePlayGroundStore)
		val (score1, score2) = scores
		assertEquals(simplePlayGroundStore, state)
		assertEquals(score1, Score(R1, 0))
		assertEquals(score2, Score(R2, 0))
	}

	@Test fun `return a state convenient regarding to instructions`() {
		val (state, _) = compileInstructions(listOf(A, A, R, A), listOf(A, A, R, A)).run(simplePlayGroundStore)
		assertEquals(state.r1.player, R1)
		assertEquals(state.r1.currentPosition, Position(1, 2, East))
		assertEquals(state.r2.player, R2)
		assertEquals(state.r2.currentPosition, Position(2, 1, West))
	}

	@Test fun `We can use State_gets to get final position`() {

	}
}