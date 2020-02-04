// Temporary, will be removed with the next release of Reaktive
@file:UseExperimental(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package arrow.fx.reaktive.extensions

import com.badoo.reaktive.coroutinesinterop.asCoroutineDispatcher
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.ioScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.coroutines.CoroutineContext

internal val computationSchedulerContext: CoroutineContext =
  computationScheduler.asCoroutineDispatcher()

internal val ioSchedulerContext: CoroutineContext =
  ioScheduler.asCoroutineDispatcher()
