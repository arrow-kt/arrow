package arrow.optics.mtl

import arrow.core.Tuple2
import arrow.mtl.Reader
import arrow.mtl.State
import arrow.mtl.map
import arrow.optics.Getter

/**
 * Extracts the value viewed through the [get] function into [Reader].
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.Reader
 * import arrow.optics.Getter
 * import arrow.optics.mtl.ask
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Getter<Enemy, Int> = Getter(Enemy::health)
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
fun <S, A> Getter<S, A>.ask(): Reader<S, A> = Reader(::get)

/** @see ask */
fun <S, A> Getter<S, A>.toReader(): Reader<S, A> = ask()

/**
 * Extracts the value viewed through the [get] and applies [f] to it.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.Reader
 * import arrow.optics.Getter
 * import arrow.optics.mtl.asks
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Getter<Enemy, Int> = Getter(Enemy::health)
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
fun <S, A, B> Getter<S, A>.asks(f: (A) -> B): Reader<S, B> = ask().map(f)

/**
 * Extracts the focus [A] viewed through the [Getter] into [State].
 * We're unable to modify the state using just a [Getter] but we can use it to extract a focus [A] out of a structure [S].
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Getter
 * import arrow.optics.mtl.extract
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Getter<Enemy, Int> = Getter(Enemy::health)
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
fun <S, A> Getter<S, A>.extract(): State<S, A> =
  State { s -> Tuple2(s, get(s)) }

/** @see extract */
fun <S, A> Getter<S, A>.toState(): State<S, A> = extract()

/**
 * Extract and map the focus [A] viewed through the [Getter] and applies [f] to it.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.run
 * import arrow.mtl.State
 * import arrow.optics.Getter
 * import arrow.optics.mtl.extractMap
 *
 * data class Enemy(val health: Int) {
 *   companion object {
 *     val health: Getter<Enemy, Int> = Getter(Enemy::health)
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
fun <S, A, B> Getter<S, A>.extractMap(f: (A) -> B): State<S, B> = extract().map(f)
