/**
 * Set of extension functions for [Traversal] to simplify using complex structured [State] or complex dependencies with [Kleisli]/[Reader].
 */
package arrow.optics.mtl

import arrow.core.Tuple2
import arrow.mtl.State
import arrow.mtl.map
import arrow.optics.Traversal

/**
 * Extracts the focus [A] viewed through the [PTraversal].
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.extract
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val getAllEnemies = ListK.traversal<Enemy>().extract()
 *   val result = getAllEnemies.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.extract(): State<S, List<A>> =
  State { s -> Tuple2(s, getAll(s)) }

/** @see extract */
fun <S, A> Traversal<S, A>.toState(): State<S, List<A>> = extract()

/**
 * Extract and map the focus [A] viewed through the [PTraversal] and applies [f] to it.
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.extractMap
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val textEnemy = ListK.traversal<Enemy>().extractMap { enemy ->
 *     "Enemy with ${enemy.health}hp"
 *   }
 *   val result = textEnemy.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A, C> Traversal<S, A>.extractMap(f: (A) -> C): State<S, List<C>> =
  extract().map { it.map(f) }

/**
 * Update the focus [A] viewed through the [Traversal] and returns its *new* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.update
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val dropBomb = ListK.traversal<Enemy>()
 *     .update { it.copy(health = it.health - 50) }
 *   val result = dropBomb.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.update(f: (A) -> A): State<S, List<A>> = State { s ->
  val newS = modify(s, f)
  Tuple2(newS, getAll(newS))
}

/**
 * Update the focus [A] viewed through the [Traversal] and returns its *old* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.updateOld
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val dropBomb = ListK.traversal<Enemy>().updateOld { it.copy(health = it.health - 50) }
 *   val result = dropBomb.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.updateOld(f: (A) -> A): State<S, List<A>> =
  State { s -> Tuple2(modify(s, f), getAll(s)) }

/**
 * Update the focus [A] viewed through the [Traversal] and ignores both values
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.update_
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val dropBomb = ListK.traversal<Enemy>().update_ { it.copy(health = it.health - 50) }
 *   val result = dropBomb.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), Unit) }

/**
 * Assign the focus [A] viewed through the [Traversal] and returns its *new* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.assign
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val killAll = ListK.traversal<Enemy>().assign(Enemy(0))
 *   val result = killAll.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.assign(a: A): State<S, List<A>> =
  update { a }

/**
 * Assign the focus [A] viewed through the [Traversal] and returns its *old* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.assignOld
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val killAll = ListK.traversal<Enemy>().assignOld(Enemy(0))
 *   val result = killAll.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.assignOld(a: A): State<S, List<A>> =
  updateOld { a }

/**
 * Assign the focus [A] viewed through the [Traversal] and ignores both values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.ListK
 * import arrow.core.k
 * import arrow.mtl.run
 * import arrow.optics.extensions.traversal
 * import arrow.optics.mtl.assign_
 *
 * data class Enemy(val health: Int)
 * val battlefield = listOf(Enemy(70), Enemy(80), Enemy(65)).k()
 *
 * fun main() {
 *   //sampleStart
 *   val killAll = ListK.traversal<Enemy>().assign_(Enemy(0))
 *   val result = killAll.run(battlefield)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Traversal<S, A>.assign_(a: A): State<S, Unit> =
  update_ { a }
