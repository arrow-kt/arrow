package arrow

import arrow.effects.DeferredKW
import arrow.effects.Proc
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.DefaultDispatcher

// [showInstances] can't detect the version of runAsync with default parameters we're using
fun <A> DeferredKW.Companion.runAsync(fa: Proc<A>): DeferredKW<A> =
        runAsync(DefaultDispatcher, CoroutineStart.DEFAULT, fa)