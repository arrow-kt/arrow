package arrow.fx.stm

public fun <A> STM.newTArray(size: Int, f: (Int) -> A): TArray<A> =
  TArray(Array(size) { i -> TVar(f(i)) })
public fun <A> STM.newTArray(size: Int, a: A): TArray<A> =
  newTArray(size) { a }
public fun <A> STM.newTArray(vararg arr: A): TArray<A> =
  TArray(arr.map { newTVar(it) }.toTypedArray())
public fun <A> STM.newTArray(xs: Iterable<A>): TArray<A> =
  TArray(xs.map { newTVar(it) }.toTypedArray())

/**
 * A [TArray] is an array of transactional variables.
 *
 * ## Creating [TArray]
 *
 * Similar to normal arrays there are a few ways to create a [TArray]:
 *
 * ```kotlin:ank
 * import arrow.fx.stm.TArray
 * import arrow.fx.stm.atomically
 *
 * suspend fun example() {
 *   //sampleStart
 *   // Create a size 10 array and fill it by using the construction function.
 *   TArray.new(10) { i -> i * 2 }
 *   // Create a size 10 array and fill it with a constant
 *   TArray.new(size = 10, 2)
 *   // Create an array from `vararg` arguments:
 *   TArray.new(5, 2, 10, 600)
 *   // Create an array from any iterable
 *   TArray.new(listOf(5,4,3,2))
 *   //sampleEnd
 * }
 * ```
 *
 * ## Reading a value from the array
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TArray
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tarr = TArray.new(size = 10, 2)
 *   val result = atomically {
 *     tarr[5]
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * ## Setting a value in the array
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TArray
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tarr = TArray.new(size = 10, 2)
 *   val result = atomically {
 *     tarr[5] = 3
 *
 *     tarr[5]
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * ## Transform the entire array
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TArray
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tarr = TArray.new(size = 10, 2)
 *   val result = atomically {
 *     tarr.transform { it + 1 }
 *   }
 *   //sampleEnd
 * }
 * ```
 *
 * ## Folding the array
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TArray
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tarr = TArray.new(size = 10, 2)
 *   val result = atomically {
 *     tarr.fold(0) { acc, v -> acc + v }
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 */
public data class TArray<A>internal constructor(internal val v: Array<TVar<A>>) {

  public fun size(): Int = v.size

  override fun equals(other: Any?): Boolean = this === other
  override fun hashCode(): Int = v.hashCode()

  public companion object {
    public suspend fun <A> new(size: Int, f: (Int) -> A): TArray<A> =
      TArray(Array(size) { i -> TVar(f(i)) })
    public suspend fun <A> new(size: Int, a: A): TArray<A> =
      new(size) { a }
    public suspend fun <A> new(vararg arr: A): TArray<A> =
      TArray(arr.map { TVar.new(it) }.toTypedArray())
    public suspend fun <A> new(xs: Iterable<A>): TArray<A> =
      TArray(xs.map { TVar.new(it) }.toTypedArray())
  }
}
