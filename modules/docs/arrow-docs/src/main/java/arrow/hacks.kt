package arrow

import arrow.effects.DeferredK
import arrow.effects.Proc
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.DefaultDispatcher

// [showInstances] can't detect the version of async with default parameters we're using
fun <A> DeferredK.Companion.async(fa: Proc<A>): DeferredK<A> =
        async(DefaultDispatcher, CoroutineStart.DEFAULT, fa)