package arrow.fx.coroutines.stream

import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.CancelToken
import arrow.fx.coroutines.CancellableContinuation
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.Schedule
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.UnsafePromise
import arrow.fx.coroutines.assertThrowable
import arrow.fx.coroutines.cancelBoundary
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.parTupledN
import arrow.fx.coroutines.sleep
import arrow.fx.coroutines.startCoroutineCancellable
import arrow.fx.coroutines.suspend
import arrow.fx.coroutines.throwable
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

class CallbackTest : StreamSpec(iterations = 250, spec = {

  "should be lazy" {
    checkAll(Arb.int()) {
      val effect = Atomic(false)
      val s = Stream.callback<Int> {
        effect.set(true)
        end()
      }

      effect.get() shouldBe false
      s.drain()
      effect.get() shouldBe true
    }
  }

  "emits values" {
    checkAll(Arb.list(Arb.int())) { list ->
      Stream.callback {
        list.forEach { emit(it) }
        end()
      }
        .toList() shouldBe list
    }
  }

  "emits varargs" {
    checkAll(Arb.list(Arb.int()).map { it.toTypedArray() }) { list ->
      Stream.callback {
        emit(*list)
        end()
      }
        .chunks()
        .toList() shouldBe listOf(Chunk(*list))
    }
  }

  "emits iterable" {
    checkAll(Arb.list(Arb.int())) { list ->
      Stream.callback<Int> {
        emit(list)
        end()
      }
        .chunks()
        .toList() shouldBe listOf(Chunk.iterable(list))
    }
  }

  "emits chunks" {
    checkAll(Arb.chunk(Arb.int()), Arb.chunk(Arb.int())) { ch, ch2 ->
      Stream.callback<Int> {
        emit(ch)
        emit(ch2)
        end()
      }
        .chunks()
        .toList() shouldBe listOf(ch, ch2)
    }
  }

  "long running emission" {
    Stream.callback {
      ForkAndForget {
        countToCallback(4, { it }, { emit(it) }) { end() }
      }
    }
      .toList() shouldBe listOf(1, 2, 3, 4, 5)
  }

  "parallel emission/pulling" {
    val ref = Atomic(false)

    Stream.callback {
      emit(1)
      sleep(500.milliseconds)
      emit(2)
      ref.set(true)
      end()
    }
      .effectMap {
        if (it == 1) ref.get() shouldBe false
        else Unit
      }
      .drain()
  }

  "forwards errors" {
    checkAll(Arb.throwable()) { e ->
      val s = Stream.cancellable<Int> {
        throw e
      }

      assertThrowable {
        s.drain()
      } shouldBe e
    }
  }

  "forwards suspended errors" {
    checkAll(Arb.throwable()) { e ->
      val s = Stream.cancellable<Int> {
        e.suspend()
      }

      assertThrowable {
        s.drain()
      } shouldBe e
    }
  }

  "runs cancel token" {
    checkAll(Arb.int()) {
      val latch = Promise<Unit>()
      val effect = Atomic(false)

      val s = Stream.cancellable<Int> {
        CancelToken { effect.set(true) }
      }

      val f = ForkAndForget {
        parTupledN(
          { s.drain() },
          { latch.complete(Unit) }
        )
      }

      parTupledN({ latch.get() }, { sleep(20.milliseconds) })

      f.cancel()

      effect.get() shouldBe true
    }
  }

  "cancelableF executes generated task uninterruptedly" {
    checkAll(Arb.int()) { i ->
      val latch = Promise<Unit>()
      val start = Promise<Unit>()
      val done = Promise<Int>()

      val task = suspend {
        Stream.cancellable {
          latch.complete(Unit)
          start.get()
          cancelBoundary()
          emit(Unit)
          done.complete(i)
          CancelToken.unit
        }
          .lastOrError()
      }

      val p = UnsafePromise<Unit>()

      val cancel = task.startCoroutineCancellable(CancellableContinuation { r -> p.complete(r) })

      latch.get()

      ForkConnected { cancel.invoke() }

      // Let cancel schedule
      sleep(10.milliseconds)

      start.complete(Unit) // Continue cancellableF

      done.get() shouldBe i
      p.tryGet() shouldBe null
    }
  }

  "doesn't run cancel token without cancellation" {
    val effect = Atomic(false)

    Stream.cancellable<Int> {
      end()
      CancelToken { effect.set(true) }
    }

      .drain()

    effect.get() shouldBe false
  }
})

private fun <A> countToCallback(
  iterations: Int,
  map: (Int) -> A,
  cb: suspend (A) -> Unit,
  onEnd: suspend () -> Unit = { }
): Unit = suspend {
  var i = 0
  arrow.fx.coroutines.repeat(Schedule.recurs(iterations)) {
    i += 1
    cb(map(i))
    sleep(500.milliseconds)
  }
  onEnd()
}.startCoroutine(Continuation(EmptyCoroutineContext) { })
