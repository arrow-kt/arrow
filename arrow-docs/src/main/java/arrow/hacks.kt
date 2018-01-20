package arrow

import arrow.effects.DeferredKW
import arrow.effects.Proc
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.DefaultDispatcher

// [showInstances] can't detect the version of runAsync with default parameters we're using
fun <A> DeferredKW.Companion.async(fa: Proc<A>): DeferredKW<A> =
        async(DefaultDispatcher, CoroutineStart.DEFAULT, fa)