package arrow.effects.dispatchers

import java.util.concurrent.ExecutorService
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

val Default : CoroutineContext = TODO()
val IO : CoroutineContext = TODO()

internal interface DefaultCoroutineContext : ExecutorService {

}