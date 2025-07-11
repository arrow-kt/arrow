@file:JvmName("ComposeFlowKt")
@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalForInheritanceCoroutinesApi::class)

package arrow.optics

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.JvmName

/**
 * Exposes the values of [this] through the optic.
 */
public fun <T, A> SharedFlow<T>.optic(g: Lens<T, A>): SharedFlow<A> = object : SharedFlow<A> {
  override suspend fun collect(collector: FlowCollector<A>): Nothing =
    this@optic.collect { collector.emit(g.get(it)) }

  override val replayCache: List<A>
    get() = this@optic.replayCache.map { g.get(it) }
}

/**
 * Exposes the values of [this] through the optic.
 */
public fun <T, A> StateFlow<T>.optic(g: Lens<T, A>): StateFlow<A> = object : StateFlow<A> {
  override val value: A
    get() = g.get(this@optic.value)

  override suspend fun collect(collector: FlowCollector<A>): Nothing =
    this@optic.collect { collector.emit(g.get(it)) }

  override val replayCache: List<A>
    get() = this@optic.replayCache.map { g.get(it) }
}

/**
 * Exposes the values of [this] through the optic.
 * Any change made to [MutableStateFlow.value] is reflected in the original [MutableStateFlow].
 */
public fun <T, A> MutableStateFlow<T>.optic(lens: Lens<T, A>): MutableStateFlow<A> = object : MutableStateFlow<A> {
  override var value: A
    get() = lens.get(this@optic.value)
    set(newValue) {
      this@optic.value = lens.set(this@optic.value, newValue)
    }

  override suspend fun collect(collector: FlowCollector<A>): Nothing =
    this@optic.collect { collector.emit(lens.get(it)) }

  override fun compareAndSet(expect: A, update: A): Boolean {
    val expectT = lens.set(this@optic.value, expect)
    val updateT = lens.set(this@optic.value, update)
    return compareAndSet(expectT, updateT)
  }

  override fun tryEmit(value: A): Boolean =
    this@optic.tryEmit(lens.set(this@optic.value, value))

  override suspend fun emit(value: A): Unit =
    this@optic.emit(lens.set(this@optic.value, value))

  override val subscriptionCount: StateFlow<Int>
    get() = this@optic.subscriptionCount

  override val replayCache: List<A>
    get() = this@optic.replayCache.map { lens.get(it) }

  override fun resetReplayCache() {
    this@optic.resetReplayCache()
  }
}
