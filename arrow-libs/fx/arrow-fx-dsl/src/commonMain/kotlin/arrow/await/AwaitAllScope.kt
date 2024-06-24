package arrow.await

import arrow.atomic.Atomic
import arrow.atomic.update
import kotlinx.coroutines.async as coroutinesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll as coroutinesAwaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public suspend fun <A> awaitAll(
  block: suspend AwaitAllScope.() -> A
): A = coroutineScope { block(AwaitAllScope(this)) }

public suspend fun <A> CoroutineScope.awaitAll(
  block: suspend AwaitAllScope.() -> A
): A = block(AwaitAllScope(this))

/**
 * Within an [AwaitAllScope], any call to [kotlinx.coroutines.Deferred.await]
 * causes all the other [Deferred] in the same block to be awaited too.
 *
 * Why is this needed at all? Let's look at some examples:
 *
 * ```kotlin
 * suspend fun loadUserInfo(id: UserId): UserInfo = awaitAll {
 *   val name = async { loadUserFromDb(id) }
 *   val avatar = async { loadAvatar(id) }
 *   UserInfo(
 *     name.await(),   // <- at this point every 'async' is 'await'ed
 *     avatar.await()  // <- so when you reach this 'await', the value is already there
 *   )
 * }
 *```
 * Okay, great! But doesn't a `coroutineScope { }` do the same? Let's look at some examples!
 * ```kotlin
 * suspend fun loadUserFromDb(id: UserId): String =
 *   awaitCancellation()
 *
 * suspend fun loadAvatar(id: UserId): String =
 *   throw CancellationException("DataSource got closed!")
 * ```
 * Our `loadUserFromDb` function hangs forever, stub this using `awaitCancellation()`.
 * Which means it will only finish, when our scope is cancelled, or any sibling `Job` cancels us.
 * Our `loadAvatar` functions cancels here, due to our `DataSource` getting closed for whatever reason.
 * We stub this by throwing `CancellationException`, but let's see what happens.
 * ```kotlin
 * suspend fun loadUserInfo(id: UserId): UserInfo = coroutineScope {
 *   val name = async { loadUserFromDb(id) }
 *   val avatar = async { loadAvatar(id) }
 *   UserInfo(
 *     name.await(),   // <- Waiting forever, awaitCancellation
 *     avatar.await()  // <- `CancellationException` ignored until `await` is called.
 *   )
 * }
 * ```
 * Our program hangs forever even though `CoroutineScope` saw our `CancellationException`.
 * Strange, if any exception is used besides `CancellationException` it works as expected,
 * if we call `awaitAll(name, avatar)` instead, it also works as expected,
 * if we'd use `launch` it would also work as expected!
 * So, only in this case where we use `async` for parallel operations it hangs forever.
 * This comes with a **big** downside for Arrow Core's Raise,
 * since it also relies on `CancellationException` for signalling early returning, or short-circuiting.
 *
 * However, if we use `awaitAll`, which is the DSL form of `awaitAll(name, avatar)` everything works as desired.
 *
 * ```kotlin
 * suspend fun loadUserInfoWithoutAwait(id: UserId): UserInfo = awaitAll {
 *   val name = async { loadUserFromDb(id) }
 *   val avatar = async { loadAvatar(id) }
 *   return UserInfo(
 *     name.await(), // <-- Awaits both name, and avatar
 *     avatar.await() // <-- CancellationException cancels the awaitAll, and is rethrown.
 *   )
 * }
 * ```
 *
 * **DISCLAIMER:**
 * Since `AwaitAllScope` is not part of KotlinX Coroutines,
 * you need to be careful to call the correct `async` function.
 * If you accidentally call `kotlinx.coroutines.async` instead,
 * the original `coroutineScope { }` behavior is still respected.
 * Luckily this should not pose an issue often, since async is a member of `AwaitAllScope`.
 */
public class AwaitAllScope(
  private val scope: CoroutineScope
) : CoroutineScope by scope {
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

  // Allow nesting in a custom scope,
  // whilst still adding it to this AwaitAllScope
  // TODO: discuss if this is good idea, or not.
  public fun <T> CoroutineScope.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T
  ): Deferred<T> {
    val scope = this
    val deferred = scope.coroutinesAsync(context, start, block)
    tasks.update { it + deferred }
    return Await(deferred)
  }

  private inner class Await<T>(
    private val deferred: Deferred<T>
  ) : Deferred<T> by deferred {
    override suspend fun await(): T {
      tasks.getAndSet(emptyList()).coroutinesAwaitAll()
      return deferred.await()
    }
  }
}
