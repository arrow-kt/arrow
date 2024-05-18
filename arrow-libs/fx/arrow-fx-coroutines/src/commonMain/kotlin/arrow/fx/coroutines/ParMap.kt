package arrow.fx.coroutines

import arrow.core.raise.RaiseAccumulate
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.DelicateRaiseApi
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.recoverReused
import arrow.core.raise.reusableRaise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public suspend fun <A, B> Iterable<A>.parMap(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  f: suspend CoroutineScope.(A) -> B
): List<B> {
  val semaphore = Semaphore(concurrency)
  return parMap(context) {
    semaphore.withPermit { f(it) }
  }
}

public suspend fun <A, B> Iterable<A>.parMap(
  context: CoroutineContext = EmptyCoroutineContext,
  transform: suspend CoroutineScope.(A) -> B
): List<B> = coroutineScope {
  map { async(context) { transform.invoke(this, it) } }.awaitAll()
}

public suspend fun <A, B> Iterable<A>.parMapNotNull(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  transform: suspend CoroutineScope.(A) -> B?
): List<B> =
  parMap(context, concurrency, transform).filterNotNull()

public suspend fun <A, B> Iterable<A>.parMapNotNull(
  context: CoroutineContext = EmptyCoroutineContext,
  transform: suspend CoroutineScope.(A) -> B?
): List<B> =
  parMap(context, transform).filterNotNull()

/** Temporary intersection type, until we have context receivers */
public class ScopedRaiseAccumulate<Error>(
  raise: Raise<NonEmptyList<Error>>,
  scope: CoroutineScope
) : CoroutineScope by scope, RaiseAccumulate<Error>(raise)

@OptIn(DelicateRaiseApi::class)
public suspend fun <Error, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  combine: (Error, Error) -> Error,
  transform: suspend ScopedRaiseAccumulate<Error>.(A) -> B
): Either<Error, List<B>> =
  either {
    coroutineScope {
      reusableRaise<NonEmptyList<Error>, _> {
        val semaphore = Semaphore(concurrency)
        val asyncs = map {
          async(context) {
            semaphore.withPermit {
              transform(ScopedRaiseAccumulate(raise, this@coroutineScope), it)
            }
          }
        }
        mapOrAccumulate(asyncs, combine) {
          withNel {
            recoverReused({ it.await() }, this::raise)
          }
        }
      }
    }
  }

@OptIn(DelicateRaiseApi::class)
public suspend fun <Error, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  combine: (Error, Error) -> Error,
  transform: suspend ScopedRaiseAccumulate<Error>.(A) -> B
): Either<Error, List<B>> =
  either {
    coroutineScope {
      reusableRaise<NonEmptyList<Error>, _> {
        val asyncs = map {
          async(context) {
            transform(ScopedRaiseAccumulate(raise, this@coroutineScope), it)
          }
        }
        mapOrAccumulate(asyncs, combine) {
          withNel {
            recoverReused({ it.await() }, this::raise)
          }
        }
      }
    }
  }

@OptIn(DelicateRaiseApi::class)
public suspend fun <Error, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  transform: suspend ScopedRaiseAccumulate<Error>.(A) -> B
): Either<NonEmptyList<Error>, List<B>> =
  either {
    coroutineScope {
      reusableRaise<NonEmptyList<Error>, _> {
        val semaphore = Semaphore(concurrency)
        val asyncs = map {
          async(context) {
            semaphore.withPermit {
              transform(ScopedRaiseAccumulate(raise, this@coroutineScope), it)
            }
          }
        }
        mapOrAccumulate(asyncs) {
          withNel {
            recoverReused({ it.await() }, this::raise)
          }
        }
      }
    }
  }

@OptIn(DelicateRaiseApi::class)
public suspend fun <Error, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  transform: suspend ScopedRaiseAccumulate<Error>.(A) -> B
): Either<NonEmptyList<Error>, List<B>> =
  either {
    coroutineScope {
      reusableRaise<NonEmptyList<Error>, _> {
        val asyncs = map {
          async(context) {
            transform(ScopedRaiseAccumulate(raise, this@coroutineScope), it)
          }
        }
        mapOrAccumulate(asyncs) {
          withNel {
            recoverReused({ it.await() }, this::raise)
          }
        }
      }
    }
  }
