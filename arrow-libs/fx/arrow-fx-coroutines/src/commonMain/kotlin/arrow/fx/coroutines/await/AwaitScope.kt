package arrow.fx.coroutines.await

import arrow.atomic.Atomic
import arrow.atomic.update
import kotlinx.coroutines.async as coroutinesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public suspend fun <A> await(
  block: suspend AwaitScope.() -> A
): A = coroutineScope { block(AwaitScope(this)) }

public suspend fun <A> CoroutineScope.await(
  block: suspend AwaitScope.() -> A
): A = block(AwaitScope(this))

/**
 * Within an [AwaitScope], any call to [kotlinx.coroutines.Deferred.await]
 * causes all the other [Deferred] in the same block to be awaited too.
 * That way you can get more concurrency without having to sacrifice
 * readability.
 *
 * ```kotlin
 * suspend fun loadUserInfo(id: UserId): UserInfo = await {
 *   val name = async { loadUserFromDb(id) }
 *   val avatar = async { loadAvatar(id) }
 *   UserInfo(
 *     name.await(),   // <- at this point every 'async' is 'await'ed
 *     avatar.await()  // <- so when you reach this 'await', the value is already there
 *   )
 * }
 *
 * suspend fun loadUserInfoWithoutAwait(id: UserId): UserInfo {
 *   val name = async { loadUserFromDb(id) }
 *   val avatar = async { loadAvatar(id) }
 *   awaitAll(name, avatar)  // <- this is required otherwise
 *   return UserInfo(
 *     name.await(),
 *     avatar.await()
 *   )
 * }
 * ```
 */
public class AwaitScope(
  private val scope: CoroutineScope
): CoroutineScope by scope {
  private val tasks: Atomic<List<Deferred<*>>> = Atomic(emptyList())

  public fun <T> async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
  ): Deferred<T> {
    val deferred = coroutinesAsync(context, start, block)
    tasks.update { it + deferred }
    return Await(deferred)
  }

  private inner class Await<T>(
    private val deferred: Deferred<T>
  ): Deferred<T> by deferred {
    override suspend fun await(): T {
      tasks.getAndSet(emptyList()).awaitAll()
      return deferred.await()
    }
  }
}
