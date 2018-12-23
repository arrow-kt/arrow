package arrow.effects

import io.reactivex.disposables.Disposable
import org.reactivestreams.Subscription

internal fun Disposable.onDispose(f: () -> Unit): Disposable = object : Disposable by this {
  override fun dispose() {
    this@onDispose.dispose()
    f()
  }
}

internal fun Subscription.onCancel(f: () -> Unit): Subscription = object : Subscription by this {
  override fun cancel() {
    this@onCancel.cancel()
    f()
  }
}
