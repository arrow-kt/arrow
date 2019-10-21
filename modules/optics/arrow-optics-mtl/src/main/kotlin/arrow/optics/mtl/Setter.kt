/**
 * Set of extension functions for [Setter] to simplify using complex structured [State].
 */
package arrow.optics.mtl

import arrow.core.Tuple2
import arrow.mtl.State
import arrow.optics.Setter

/**
 * Update the focus [A] seen through the [Setter] while ignoring the output of the function.
 * We can only ignore the output of the function because [Setter] can only modify the original value, and not `get` it.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.*
 * import arrow.optics.*
 * import arrow.optics.mtl.update_
 *
 * data class Player(val health: Int)
 *
 * val playerSetter: Setter<Player, Int> = Setter { player: Player, f: (Int) -> Int ->
 *   player.copy(health = f(player.health))
 * }
 *
 * fun main() {
 *  //sampleStart
 *  val takeDamage = playerSetter.update_ { it - 15 }
 *  val result = takeDamage.run(Player(75))
 *  //endSample
 *  println(result)
 * }
 * ```
 * @receiver the [Setter] you want to use to create a [State].
 * @param f the function you want to apply to the [State] [S].
 * @return the resulting [State].
 */
fun <S, A> Setter<S, A>.update_(f: (A) -> A): State<S, Unit> =
  State { s -> Tuple2(modify(s, f), Unit) }

/**
 * Set the focus [A] seen through the [Setter]  while ignoring the output of the function.
 * We can only ignore the output of the function because [Setter] can only modify the original value, and not `get` it.
 *
 * ```kotlin:ank:playground
 * import arrow.mtl.*
 * import arrow.optics.*
 * import arrow.optics.mtl.assign_
 *
 * data class Player(val health: Int)
 *
 * val playerSetter: Setter<Player, Int> = Setter { player: Player, f: (Int) -> Int ->
 *   player.copy(health = f(player.health))
 * }
 *
 * fun main() {
 *  //sampleStart
 *  val restoreHealth = playerSetter.assign_(100)
 *  val result = restoreHealth.run(Player(75))
 *  //endSample
 *  println(result)
 * }
 * ```
 * @receiver the [Setter] you want to use to create a [State].
 * @param a the value to set in the [State] [S].
 * @return the resulting [State].
 */
fun <S, A> Setter<S, A>.assign_(a: A): State<S, Unit> =
  update_ { a }
