package arrow.fx.coroutines.computations

import arrow.continuations.generic.AtomicRef
import arrow.continuations.generic.update
import arrow.core.NonEmptyList
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.nonFatalOrThrow
import arrow.core.traverseValidated
import arrow.core.valid
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.runReleaseAndRethrow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

public interface ResourceEffect {
  public suspend fun <A> Resource<A>.bind(): A
}

public fun <A> resource(f: suspend ResourceEffect.() -> A): Resource<A> =
  Resource({
    val effect = ResourceEffectImpl()
    val res = try {
      f(effect)
    } catch (e: Throwable) {
      val ex = if (e is CancellationException) ExitCase.Cancelled(e)
      else ExitCase.Failure(e)
      val ee = effect.finalizers.get().traverseValidated { f ->
        catchNel { f(ex) }
      }.fold({
        Platform.composeErrors(NonEmptyList(e, it))
      }, { e })
      throw ee
    }
    Pair(res, effect)
  }) { (_, effect), ex ->
    effect.finalizers.get().cancelAll(ex)
  }.map { it.first }

private class ResourceEffectImpl : ResourceEffect {
  val finalizers: AtomicRef<List<suspend (ExitCase) -> Unit>> = AtomicRef(emptyList())
  override suspend fun <A> Resource<A>.bind(): A =
    allocate { finalizer ->
      finalizers.update { it + finalizer }
    }
}

private suspend fun List<suspend (ExitCase) -> Unit>.cancelAll(
  exitCase: ExitCase,
  last: (suspend () -> Unit)? = null
): Unit {
  val e = traverseValidated { finalizer ->
    catchNel { finalizer(exitCase) }
  }.fold({
    Platform.composeErrors(it)
  }, { null })
  val e2 = runCatching { last?.invoke() }.exceptionOrNull()
  Platform.composeErrors(e, e2)?.let { throw it }
}

// Interpreter that knows how to evaluate a Resource data structure
// Maintains its own stack for dealing with Bind chains
@Suppress("UNCHECKED_CAST")
private tailrec suspend fun useLoop(
  current: Resource<Any?>,
  stack: List<(Any?) -> Resource<Any?>>,
  finalizers: List<suspend (ExitCase) -> Unit>,
  handle: (Pair<Any?, suspend (ExitCase) -> Unit>) -> Unit
): Pair<Any?, suspend (ExitCase) -> Unit> =
  when (current) {
    is Resource.Defer -> useLoop(current.resource.invoke(), stack, finalizers, handle)
    is Resource.Bind<*, *> ->
      useLoop(current.source, listOf(current.f as (Any?) -> Resource<Any?>) + stack, finalizers, handle)
    is Resource.Allocate -> loadResourceAndReleaseHandler(
      acquire = {
        current.acquire().let { a ->
          if (stack.isEmpty()) {
            Pair<Any?, suspend (ExitCase) -> Unit>(a) { ex ->
              finalizers.cancelAll(ex) {
                current.release(a, ex)
              }
            }.also(handle)
          } else {
            a
          }
        }
      },
      use = { a ->
        if (stack.isEmpty()) a as Pair<Any?, suspend (ExitCase) -> Unit>
        else useLoop(stack.first()(a), stack.drop(1), finalizers + { ex -> current.release(a, ex) }, handle)
      },
      release = { a, ex ->
        if (ex != ExitCase.Completed) {
          finalizers.cancelAll(ex) {
            current.release(a, ex)
          }
        }
      }
    )
  }

private suspend fun <A> Resource<A>.allocate(handle: (suspend (ExitCase) -> Unit) -> Unit): A =
  useLoop(this, emptyList(), emptyList()) { (_, finalizer) ->
    handle(finalizer)
  }.first as A

private suspend inline fun loadResourceAndReleaseHandler(
  crossinline acquire: suspend () -> Any?,
  crossinline use: suspend (Any?) -> Pair<Any?, suspend (ExitCase) -> Unit>,
  crossinline release: suspend (Any?, ExitCase) -> Unit
): Pair<Any?, suspend (ExitCase) -> Unit> {
  val acquired = withContext(NonCancellable) {
    acquire()
  }

  return try { // Successfully loaded resource, pass it and its release f down
    val (b, _release) = use(acquired)
    Pair(b) { ex -> _release(ex); release(acquired, ex) }
  } catch (e: CancellationException) { // Release when cancelled
    runReleaseAndRethrow(e) { release(acquired, ExitCase.Cancelled(e)) }
  } catch (t: Throwable) { // Release when failed to load resource
    runReleaseAndRethrow(t.nonFatalOrThrow()) { release(acquired, ExitCase.Failure(t.nonFatalOrThrow())) }
  }
}

private inline fun <A> catchNel(f: () -> A): ValidatedNel<Throwable, A> =
  try {
    f().valid()
  } catch (e: Throwable) {
    e.invalidNel()
  }
