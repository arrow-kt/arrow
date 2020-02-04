package arrow.fx.reaktive

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.defaultIfEmpty
import com.badoo.reaktive.maybe.map
import com.badoo.reaktive.observable.ConnectableObservable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.firstOrDefault
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.single.Single

internal fun <T> ConnectableObservable<T>.connectAndGetDisposable(): Disposable {
  lateinit var disposable: Disposable
  connect { disposable = it }

  return disposable
}

internal fun <T> Maybe<T>.isEmpty(): Single<Boolean> =
  map { true }.defaultIfEmpty(false)

internal fun <T> Observable<T>.isEmpty(): Single<Boolean> =
  map { true }.firstOrDefault(false)
