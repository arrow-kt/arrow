package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.unsafe

interface UnsafeRun<F> {
  suspend fun <A> unsafe.runBlocking(fa: () -> Kind<F, A>): A
  suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<F, A>, cb: (Either<Throwable, A>) -> Unit): Unit
}
