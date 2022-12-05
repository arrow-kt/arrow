package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.continuations.EffectScope
import arrow.core.continuations.either
import arrow.core.flattenOrAccumulate
import arrow.typeclasses.Semigroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public suspend fun <A, B> Iterable<A>.parMap(
  ctx: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  f: suspend CoroutineScope.(A) -> B
): List<B> {
  val semaphore = Semaphore(concurrency)
  return parMap(ctx) {
    semaphore.withPermit { f(it) }
  }
}

public suspend fun <A, B> Iterable<A>.parMap(
  context: CoroutineContext = EmptyCoroutineContext,
  transform: suspend CoroutineScope.(A) -> B
): List<B> = coroutineScope {
  map { async(context) { transform.invoke(this, it) } }.awaitAll()
}

/** Temporary intersection type, until we have context receivers */
public class ScopedRaise<E>(
  raise: EffectScope<E>,
  scope: CoroutineScope
) : CoroutineScope by scope, EffectScope<E> by raise

public suspend fun <E, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  semigroup: Semigroup<E>,
  transform: suspend ScopedRaise<E>.(A) -> B
): Either<E, List<B>> =
  coroutineScope {
    val semaphore = Semaphore(concurrency)
    map {
      async(context) {
        either {
          semaphore.withPermit {
            transform(ScopedRaise(this, this@coroutineScope), it)
          }
        }
      }
    }.awaitAll().flattenOrAccumulate(semigroup)
  }

public suspend fun <E, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>,
  transform: suspend ScopedRaise<E>.(A) -> B
): Either<E, List<B>> =
  coroutineScope {
    map {
      async(context) {
        either {
          transform(ScopedRaise(this, this@coroutineScope), it)
        }
      }
    }.awaitAll().flattenOrAccumulate(semigroup)
  }

public suspend fun <E, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  concurrency: Int,
  transform: suspend ScopedRaise<E>.(A) -> B
): Either<NonEmptyList<E>, List<B>> =
  coroutineScope {
    val semaphore = Semaphore(concurrency)
    map {
      async(context) {
        either {
          semaphore.withPermit {
            transform(ScopedRaise(this, this@coroutineScope), it)
          }
        }
      }
    }.awaitAll().flattenOrAccumulate()
  }

public suspend fun <E, A, B> Iterable<A>.parMapOrAccumulate(
  context: CoroutineContext = EmptyCoroutineContext,
  transform: suspend ScopedRaise<E>.(A) -> B
): Either<NonEmptyList<E>, List<B>> =
  coroutineScope {
    map {
      async(context) {
        either {
          transform(ScopedRaise(this, this@coroutineScope), it)
        }
      }
    }.awaitAll().flattenOrAccumulate()
  }
