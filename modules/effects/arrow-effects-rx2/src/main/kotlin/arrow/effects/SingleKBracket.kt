package arrow.effects

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

internal class SingleKBracket<A>(private val source: SingleSource<A>,
                                 private val onErrorCall: (Throwable) -> Single<Unit>,
                                 private val onCompleteCall: Single<Unit>,
                                 private val onDisposeCall: Single<Unit>) : Single<A>() {

  override fun subscribeActual(observer: SingleObserver<in A>) {
    source.subscribe(SingleKBracketObserver(observer, onErrorCall, onCompleteCall, onDisposeCall))
  }

  private class SingleKBracketObserver<A>(
    private val downstream: SingleObserver<in A>,
    private val onErrorCall: (Throwable) -> Single<Unit>,
    private val onCompleteCall: Single<Unit>,
    private val onDisposeCall: Single<Unit>) : SingleObserver<A>, Disposable {

    var upstream: Disposable? = null

    override fun dispose() {
      onDisposeCall.subscribe({}, { ex ->
        Exceptions.throwIfFatal(ex)
        RxJavaPlugins.onError(ex)
      })

      upstream?.dispose()
      upstream = DisposableHelper.DISPOSED
    }

    override fun isDisposed(): Boolean =
      upstream?.isDisposed == true

    override fun onSubscribe(d: Disposable) {
      if (DisposableHelper.validate(upstream, d)) {
        upstream = d
        downstream.onSubscribe(this)
      }
    }

    override fun onSuccess(value: A) {
      if (this.upstream === DisposableHelper.DISPOSED) {
        return
      }

      onCompleteCall.subscribe({
        upstream = DisposableHelper.DISPOSED
        downstream.onSuccess(value)
      }, { ex ->
        Exceptions.throwIfFatal(ex)
        onErrorInner(ex)
      })
    }

    override fun onError(e: Throwable) {
      if (upstream === DisposableHelper.DISPOSED) {
        RxJavaPlugins.onError(e)
        return
      }

      onErrorInner(e)
    }

    fun onErrorInner(e: Throwable) {
      onErrorCall(e).subscribe({
        upstream = DisposableHelper.DISPOSED
        downstream.onError(e)
      }, { ex ->
        upstream = DisposableHelper.DISPOSED
        downstream.onError(CompositeException(e, ex))
      })
    }

  }
}
