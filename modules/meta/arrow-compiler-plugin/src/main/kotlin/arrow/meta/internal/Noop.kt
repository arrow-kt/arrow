package arrow.meta.internal

object Noop {
  val effect1: (Any?) -> Unit = { _ -> Unit }
  val effect2: (Any?, Any?) -> Unit = { _, _ -> Unit }
  val effect3: (Any?, Any?, Any?) -> Unit = { _, _, _ -> Unit }
  val effect4: (Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _ -> Unit }
  val effect5: (Any?, Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _, _ -> Unit }
  val effect6: (Any?, Any?, Any?, Any?, Any?, Any?) -> Unit = { _, _, _, _, _, _ -> Unit }
  fun <A> nullable1(): (Any?) -> A? = { null }
  fun <A> nullable2(): (Any?, Any?) -> A? = { _, _ -> null }
  fun <A> nullable3(): (Any?, Any?, Any?) -> A? = { _, _, _ -> null }
  fun <A> nullable4(): (Any?, Any?, Any?, Any?) -> A? = { _, _, _, _ -> null }
  fun <A> nullable5(): (Any?, Any?, Any?, Any?, Any?) -> A? = { _, _, _, _, _ -> null }
  fun <A> emptyCollection1(): (Any?) -> Collection<A> = { emptyList() }
  fun <A> emptyCollection2(): (Any?, Any?) -> Collection<A> = { _, _ -> emptyList() }
  fun <A> emptyCollection3(): (Any?, Any?, Any?) -> Collection<A> = { _, _, _ -> emptyList() }
  fun <A> emptyCollection4(): (Any?, Any?, Any?, Any?) -> Collection<A> = { _, _, _, _ -> emptyList() }
  fun <A> emptyCollection5(): (Any?, Any?, Any?, Any?, Any?) -> Collection<A> = { _, _, _, _, _ -> emptyList() }
  val boolean1True: (Any?) -> Boolean = { _ -> true }
  val boolean2True: (Any?, Any?) -> Boolean = { _, _ -> true }
}