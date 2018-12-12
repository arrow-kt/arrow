package arrow.effects

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.plugins.RxJavaPlugins

internal class ObservableKRunOnDispose<A>(
  private val source: Observable<A>,
  private val onError: (Throwable) -> Observable<Unit>,
  private val onDispose: Observable<Unit>,
  private val onComplete: Observable<Unit>
) : Observable<A>() {

  override fun subscribeActual(observer: Observer<in A>) {
    source.subscribe(OnDisposeObserver<A>(observer, onError, onDispose, onComplete))
  }

  private class OnDisposeObserver<A>(
    private val downstream: Observer<in A>,
    private val onErrorCall: (Throwable) -> Observable<Unit>,
    private val onDispose: Observable<Unit>,
    private val onComplete: Observable<Unit>
  ) : Observer<A>, Disposable {

    var upstream: Disposable? = null

    override fun onSubscribe(d: Disposable) {
      if (DisposableHelper.validate(upstream, d)) {
        upstream = d
        downstream.onSubscribe(this)
      }
    }

    override fun onNext(t: A) =
      downstream.onNext(t)

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
      if (upstream !== DisposableHelper.DISPOSED) {
        onComplete.subscribe(
          {},
          this::onErrorInner,
          {
            upstream = DisposableHelper.DISPOSED
            downstream.onComplete()
          }
        )
      }
    }

    override fun dispose() {
      onDispose.subscribe({}, { ex ->
        Exceptions.throwIfFatal(ex)
        RxJavaPlugins.onError(ex)
      }, {})

      upstream?.dispose()
      upstream = DisposableHelper.DISPOSED
    }

    override fun isDisposed(): Boolean =
      upstream?.isDisposed == true

  }

}
