@file:OptIn(ExperimentalContracts::class)

package arrow.fx.coroutines.await

import arrow.atomic.Atomic
import arrow.atomic.update
import kotlinx.coroutines.async as coroutinesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.InternalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitAll as coroutinesAwaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@RequiresOptIn(level = RequiresOptIn.Level.WARNING, message = "This API is work-in-progress and is subject to change.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
public annotation class ExperimentalAwaitAllApi

@ExperimentalAwaitAllApi
public suspend fun <A> awaitAll(
  block: suspend AwaitAllScope.() -> A
): A {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return coroutineScope { block(AwaitAllScope(this)) }
}

@ExperimentalAwaitAllApi
public suspend fun <A> CoroutineScope.awaitAll(
  block: suspend AwaitAllScope.() -> A
): A {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return block(AwaitAllScope(this))
}

/**
 * Within an [AwaitAllScope], any call to [kotlinx.coroutines.Deferred.await]
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
public class AwaitAllScope(
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

  @OptIn(InternalForInheritanceCoroutinesApi::class)
  private inner class Await<T>(
    private val deferred: Deferred<T>
  ): Deferred<T> by deferred {
    override suspend fun await(): T {
      tasks.getAndSet(emptyList()).coroutinesAwaitAll()
      return deferred.await()
    }
  }
}
