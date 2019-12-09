package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.fx.OnCancel
import arrow.unsafe

interface UnsafeRun<F> {
  suspend fun <A> unsafe.runBlocking(fa: () -> Kind<F, A>): A
  suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<F, A>, cb: (Either<Throwable, A>) -> Unit): Unit
}

interface UnsafeCancellableRun<F> : UnsafeRun<F> {
  suspend fun <A> unsafe.runNonBlockingCancellable(onCancel: OnCancel, fa: () -> Kind<F, A>, cb: (Either<Throwable, A>) -> Unit): Disposable
}
