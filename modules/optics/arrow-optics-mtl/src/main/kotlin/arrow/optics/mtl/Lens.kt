package arrow.optics.mtl

import arrow.core.Tuple2
import arrow.mtl.Reader
import arrow.mtl.State
import arrow.mtl.map
import arrow.optics.Lens

/**
 * Extracts the value viewed through the [get] function into [Reader].
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.Reader
 * import arrow.optics.Lens
 * import arrow.optics.mtl.ask
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: Reader<Enemy, Int> = Enemy.health.ask()
 *   val result = inspectHealth.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.ask(): Reader<S, A> = Reader(::get)

/** @see ask */
fun <S, A> Lens<S, A>.toReader(): Reader<S, A> = ask()

/**
 * Extracts the value viewed through the [get] and applies [f] to it.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.Reader
 * import arrow.optics.Lens
 * import arrow.optics.mtl.asks
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: Reader<Enemy, String> = Enemy.health.asks { health ->
 *     "Enemy has ${health}hp"
 *   }
 *   val result = inspectHealth.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 *
 * @param f function to apply to the focus.
 */
fun <S, A, C> Lens<S, A>.asks(f: (A) -> C): Reader<S, C> = ask().map(f)

/**
 * Extracts the focus [A] viewed through the [PLens] into [State].
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.extract
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, Int> = Enemy.health.extract()
 *   val result = inspectHealth.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.extract(): State<S, A> = State { s -> Tuple2(s, get(s)) }

/** @see extract */
fun <S, A> Lens<S, A>.toState(): State<S, A> = extract()

/**
 * Extracts and maps the focus [A] viewed through the [PLens] and applies [f] to it.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.extractMap
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val inspectHealth: State<Enemy, String> = Enemy.health.extractMap { health ->
 *     "Enemy has ${health}hp"
 *   }
 *   val result = inspectHealth.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A, C> Lens<S, A>.extractMap(f: (A) -> C): State<S, C> = extract().map(f)

/**
 * Update the focus [A] viewed through the [Lens] and returns its *new* value.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.update
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val hitEnemy: State<Enemy, Int> = Enemy.health.update { it - 15 }
 *   val result = hitEnemy.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.update(f: (A) -> A): State<S, A> = State { s ->
  val b = f(get(s))
  Tuple2(set(s, b), b)
}

/**
 * Update the focus [A] viewed through the [Lens] and returns its *old* value.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.updateOld
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val hitEnemy: State<Enemy, Int> = Enemy.health.updateOld { it - 15 }
 *   val result = hitEnemy.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.updateOld(f: (A) -> A): State<S, A> = State { s ->
  Tuple2(modify(s, f), get(s))
}

/**
 * Modify the focus [A] viewed through the [Lens] and ignores both values.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.update_
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val hitEnemy: State<Enemy, Unit> = Enemy.health.update_ { it - 15 }
 *   val result = hitEnemy.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), Unit) }

/**
 * Assign the focus [A] viewed through the [Lens] and returns its *new* value.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.assign
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val killEnemy: State<Enemy, Int> = Enemy.health.assign(0)
 *   val result = killEnemy.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.assign(a: A): State<S, A> =
  update { a }

/**
 * Assign the value focus [A] through the [Lens] and returns its *old* value.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.assignOld
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val killEnemy: State<Enemy, Int> = Enemy.health.assignOld(0)
 *   val result = killEnemy.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.assignOld(a: A): State<S, A> =
  updateOld { a }

/**
 * Assign the focus [A] viewed through the [Lens] and ignores both values.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Lens
 * import arrow.optics.mtl.assign_
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Lens<Enemy, Int> = Lens(Enemy::health) { enemy, health -> enemy.copy(health = health) }
 *   }
 * }
 *
 * val enemy = Enemy(70)
 *
 * fun main() {
 *   //sampleStart
 *   val killEnemy: State<Enemy, Unit> = Enemy.health.assign_(0)
 *   val result = killEnemy.run(enemy)
 *   //endSample
 *   println(result)
 * }
 * ```
 */
fun <S, A> Lens<S, A>.assign_(a: A): State<S, Unit> =
  update_ { a }
