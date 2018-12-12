package arrow.effects

import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Function
import io.reactivex.internal.functions.ObjectHelper
import io.reactivex.internal.subscriptions.SubscriptionArbiter
import io.reactivex.plugins.RxJavaPlugins
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class FlowableKBracket<A>(private val source: Flowable<A>,
                          private val onError: (Throwable) -> Flowable<Unit>,
                          private val onComplete: Flowable<Unit>,
                          private val onDispose: Flowable<Unit>) : Flowable<A>() {

  override fun subscribeActual(s: Subscriber<in A>) {
    val parent = OnErrorNextSubscriber(s, onError, onComplete, onDispose)
    s.onSubscribe(parent)
    source.subscribe(parent)
  }

  private class OnErrorNextSubscriber<T>(
    private val downstream: Subscriber<in T>,
    private val onErrorCall: (Throwable) -> Flowable<Unit>,
    private val onComplete: Flowable<Unit>,
    private val onDispose: Flowable<Unit>) : io.reactivex.internal.subscriptions.SubscriptionArbiter(), FlowableSubscriber<T> {

    var once: Boolean = false

    var done: Boolean = false

    var produced: Long = 0

    override fun onSubscribe(s: Subscription) =
      setSubscription(s)

    override fun cancel() {
      super.cancel()
      onDispose.subscribe({}, { ex ->
        Exceptions.throwIfFatal(ex)
        RxJavaPlugins.onError(ex)
      }, {})
    }

    override fun onNext(t: T) {
      if (done) {
        return
      }
      if (!once) {
        produced++
      }

      downstream.onNext(t)
    }

    override fun onError(t: Throwable) {
      if (once) {
        if (done) {
          RxJavaPlugins.onError(t)
          return
        }
        downstream.onError(t)
        return
      }
      once = true

      onErrorInner(t)
    }

    private fun onErrorInner(t: Throwable) {
      onErrorCall(t).subscribe({},
        { e ->
          Exceptions.throwIfFatal(e)
          downstream.onError(CompositeException(t, e))

        }, { downstream.onError(t) })
    }

    override fun onComplete() {
      if (done) {
        return
      }
      done = true
      once = true

      onComplete.subscribe({},
        this::onErrorInner, downstream::onComplete)
    }

    override fun toByte(): Byte = get().toByte()

    override fun toChar(): Char = get().toChar()

    override fun toShort(): Short = get().toShort()

  }
}
