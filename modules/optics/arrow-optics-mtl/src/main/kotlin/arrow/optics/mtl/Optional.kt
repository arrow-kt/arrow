package arrow.optics.mtl

import arrow.core.Option
import arrow.core.Tuple2
import arrow.mtl.State
import arrow.mtl.map
import arrow.optics.Optional

/**
 * Extracts the focus [A] viewed through the [POptional] into [State].
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.extract
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Option<String>> = Enemy.health.extract()
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.extract(): State<S, Option<A>> =
  State { s -> Tuple2(s, getOption(s)) }

/** @see extract */
fun <S, A> Optional<S, A>.toState(): State<S, Option<A>> =
  extract()

/**
 * Extract and map the focus [A] viewed through the [POptional] and applies [f] to it.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.extractMap
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Option<String>> = Enemy.health.extractMap { health ->
 *     "Enemy has ${health}hp"
 *   }
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A, C> Optional<S, A>.extractMap(f: (A) -> C): State<S, Option<C>> = extract().map { it.map(f) }

/**
 * Update the focus [A] viewed through the [Optional] and returns its *new* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.update
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Option<String>> = Enemy.health.update(String::toLowerCase)
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.update(f: (A) -> A): State<S, Option<A>> =
  updateOld(f).map { it.map(f) }

/**
 * Update the focus [A] viewed through the [Optional] and returns its *old* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.updateOld
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Option<String>> = Enemy.health.updateOld(String::toLowerCase)
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.updateOld(f: (A) -> A): State<S, Option<A>> =
  State { s -> Tuple2(modify(s, f), getOption(s)) }

/**
 * Update the focus [A] viewed through the [Optional] and ignores both values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.update_
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Unit> = Enemy.health.update_(String::toLowerCase)
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), Unit) }

/**
 * Assign the focus [A] viewed through the [Optional] and returns its *new* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.assign
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Option<String>> = Enemy.health.assign("Enemy")
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.assign(a: A): State<S, Option<A>> =
  update { a }

/**
 * Assign the value focus [A] through the [Optional] and returns its *old* value.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.assign
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Option<String>> = Enemy.health.assign("Enemy")
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.assignOld(a: A): State<S, Option<A>> =
  updateOld { a }

/**
 * Assign the focus [A] viewed through the [Optional] and ignores both values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Optional
 * import arrow.optics.mtl.assign_
 *
 * data class Enemy(val health: Int, val name: Option<String>) {
 *   companion object {
 *     val health = Optional(Enemy::name) { enemy, name -> enemy.copy(name = Some(name)) }
 *   }
 * }
 *
 * val FinalBoss = Enemy(100, Some("(Abstract) Boss Manager"))
 * val Minion = Enemy(25, None)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Unit> = Enemy.health.assign_("Enemy")
 *   val result1 = inspectHealth.run(FinalBoss)
 *   val result2 = inspectHealth.run(Minion)
 *   //endSample
 *   println("result1: $result1, result2: $result2")
 * }
 * ```
 */
fun <S, A> Optional<S, A>.assign_(a: A): State<S, Unit> =
  update_ { a }
