package arrow.effects

import io.reactivex.Maybe
import io.reactivex.MaybeObserver
import io.reactivex.MaybeSource
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

internal class MaybeKBracket<A>(
  private val source: MaybeSource<A>,
  private val onErrorCall: (Throwable) -> Maybe<Unit>,
  private val onCompleteCall: Maybe<Unit>,
  private val onDisposeCall: Maybe<Unit>) : Maybe<A>() {

  override fun subscribeActual(observer: MaybeObserver<in A>) {
    source.subscribe(MaybeKBracketObserver(observer, onErrorCall, onCompleteCall, onDisposeCall))
  }

  private class MaybeKBracketObserver<A>(
    private val downstream: MaybeObserver<in A>,
    private val onErrorCall: (Throwable) -> Maybe<Unit>,
    private val onCompleteCall: Maybe<Unit>,
    private val onDisposeCall: Maybe<Unit>) : MaybeObserver<A>, Disposable {

    var upstream: Disposable? = null

    override fun dispose() {
      onDisposeCall.subscribe({}, { ex ->
        Exceptions.throwIfFatal(ex)
        RxJavaPlugins.onError(ex)
      }, {})

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

      upstream = DisposableHelper.DISPOSED
      downstream.onSuccess(value)
    }

    override fun onError(e: Throwable) {
      if (upstream === DisposableHelper.DISPOSED) {
        RxJavaPlugins.onError(e)
        return
      }

      onErrorInner(e)
    }

    fun onErrorInner(e: Throwable) {
      onErrorCall(e).subscribe({}, { ex ->
        upstream = DisposableHelper.DISPOSED
        downstream.onError(CompositeException(e, ex))
      }, {
        upstream = DisposableHelper.DISPOSED
        downstream.onError(e)
      })
    }

    override fun onComplete() {
      if (this.upstream === DisposableHelper.DISPOSED) {
        return
      }

      onCompleteCall.subscribe({}, { ex ->
        Exceptions.throwIfFatal(ex)
        onErrorInner(ex)
      }, {
        upstream = DisposableHelper.DISPOSED
        downstream.onComplete()
      })
    }

  }
}
