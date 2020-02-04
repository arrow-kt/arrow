package arrow.fx

import com.badoo.reaktive.test.maybe.TestMaybeObserver
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.single.TestSingleObserver
import io.kotlintest.fail
import java.util.concurrent.TimeUnit

fun TestMaybeObserver<*>.awaitDone(timeout: Long, unit: TimeUnit) {
  val endNanos = System.nanoTime() + unit.toNanos(timeout)
  while (!isSuccess && !isComplete && !isError) {
    if (System.nanoTime() >= endNanos) {
      fail("Timeout waiting for done of $this")
    }
  }
}

fun TestMaybeObserver<*>.awaitTerminated(timeout: Long, unit: TimeUnit) {
  val endNanos = System.nanoTime() + unit.toNanos(timeout)
  while (!isSuccess && !isComplete && !isError && !isDisposed) {
    if (System.nanoTime() >= endNanos) {
      fail("Timeout waiting for termination of $this")
    }
  }
}

fun TestObservableObserver<*>.awaitDone(timeout: Long, unit: TimeUnit) {
  val endNanos = System.nanoTime() + unit.toNanos(timeout)
  while (!isComplete && !isError) {
    if (System.nanoTime() >= endNanos) {
      fail("Timeout waiting for done of $this")
    }
  }
}

fun TestObservableObserver<*>.awaitTerminated(timeout: Long, unit: TimeUnit) {
  val endNanos = System.nanoTime() + unit.toNanos(timeout)
  while (!isComplete && !isError && !isDisposed) {
    if (System.nanoTime() >= endNanos) {
      fail("Timeout waiting for termination of $this")
    }
  }
}

fun TestSingleObserver<*>.awaitDone(timeout: Long, unit: TimeUnit) {
  val endNanos = System.nanoTime() + unit.toNanos(timeout)
  while (!isSuccess && !isError) {
    if (System.nanoTime() >= endNanos) {
      fail("Timeout waiting for done of $this")
    }
  }
}

fun TestSingleObserver<*>.awaitTerminated(timeout: Long, unit: TimeUnit) {
  val endNanos = System.nanoTime() + unit.toNanos(timeout)
  while (!isSuccess && !isError && !isDisposed) {
    if (System.nanoTime() >= endNanos) {
      fail("Timeout waiting for termination of $this")
    }
  }
}
