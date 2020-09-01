package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ConcurrentVarTest : ArrowFxSpec(spec = {

  "empty; put; isNotEmpty; take; put; take" {
    val mvar = ConcurrentVar.empty<Int>()
    mvar.isEmpty() shouldBe true
    mvar.put(1)
    mvar.isNotEmpty() shouldBe true
    mvar.take() shouldBe 1
    mvar.put(2)
    mvar.take() shouldBe 2
  }

  "empty; tryPut; tryPut; isNotEmpty; tryTake; tryTake; put; take" {
    val av = ConcurrentVar.empty<Int>()
    av.isEmpty() shouldBe true
    av.tryPut(1) shouldBe true
    av.tryPut(2) shouldBe false
    av.isNotEmpty() shouldBe true
    av.tryTake() shouldBe 1
    av.tryTake() shouldBe null
    av.put(3)
    av.take() shouldBe 3
  }

  "empty; take; put; take; put" {
    val mvar = ConcurrentVar.empty<Int>()

    val f1 = mvar::take.forkAndForget()
    mvar.put(10)

    val f2 = mvar::take.forkAndForget()
    mvar.put(20)

    val aa = f1.join()
    val bb = f2.join()
    setOf(aa, bb) shouldBe setOf(10, 20)
  }

  "empty; put; put; put; take; take; take" {
    val av = ConcurrentVar.empty<Int>()

    val f1 = ForkAndForget { av.put(10) }
    val f2 = ForkAndForget { av.put(20) }
    val f3 = ForkAndForget { av.put(30) }

    val aa = av.take()
    val bb = av.take()
    val cc = av.take()

    f1.join()
    f2.join()
    f3.join()

    setOf(aa, bb, cc) shouldBe setOf(10, 20, 30)
  }

  "empty; take; take; take; put; put; put" {
    val av = ConcurrentVar.empty<Int>()

    val f1 = av::take.forkAndForget()
    val f2 = av::take.forkAndForget()
    val f3 = av::take.forkAndForget()

    av.put(10)
    av.put(20)
    av.put(30)

    val aa = f1.join()
    val bb = f2.join()
    val cc = f3.join()

    setOf(aa, bb, cc) shouldBe setOf(10, 20, 30)
  }

  "initial; isNotEmpty; take; put; take" {
    val av = ConcurrentVar(1)
    av.isNotEmpty() shouldBe true
    av.take() shouldBe 1
    av.put(2)
    av.take() shouldBe 2
  }

  "initial; take; put; take" {
    val av = ConcurrentVar(1)
    av.isEmpty() shouldBe false
    av.take() shouldBe 1
    av.put(2)
    av.take() shouldBe 2
  }

  "initial; read; take" {
    val av = ConcurrentVar(1)
    av.read() shouldBe 1
    av.take() shouldBe 1
  }

  "empty; read; put" {
    val mvar = ConcurrentVar.empty<Int>()
    val read = mvar::read.forkAndForget()
    mvar.put(10)
    read.join() shouldBe 10
  }

  "put(null) works" {
    val mvar = ConcurrentVar.empty<String?>()
    mvar.put(null)
    mvar.read() shouldBe null
  }

  // Seems to hang
  "take/put test is stack safe" {
    val count = 10000
    val mvar = ConcurrentVar(1)
    takePutTestIsStacksafe(count, 0, mvar) shouldBe count
  }

  "stack overflow test" {
    val count = 10_000L

    suspend fun exec(channel: Channel<Long>): Long {
      val f1 = ForkAndForget {
        (0 until count).parTraverse { i -> channel.put(i) }
        channel.put(null)
      }

      val f2 = ForkAndForget { consumerParallel(channel, 0L) }
      f1.join()
      return f2.join()
    }

    val mvar = ConcurrentVar<Long?>(0L)
    exec(mvar) shouldBe count * (count - 1) / 2
  }

  /*
  "producer-consumer parallel loop" {
    val count = 10000L
    forAll(10) { _: Int ->
      val channel = ConcurrentVar<Long?>(0L)
      val producerFiber = ForkAndForget { producerParallel(channel, (0L until count).toList()) }
      val consumerFiber = ForkAndForget { consumerParallel(channel, 0L) }

      producerFiber.join() shouldBe Unit
      consumerFiber.join() shouldBe count * (count - 1) / 2
      true
    }
  }*/

  "put is stack safe when repeated sequentially" {
    val channel = ConcurrentVar.empty<Int>()
    val (count, reads, writes) = testStackSequential(channel)
    writes.forkAndForget()
    reads.invoke() shouldBe count
  }

  "take is stack safe when repeated sequentially" {
    val channel = ConcurrentVar.empty<Int>()
    val (count, reads, writes) = testStackSequential(channel)
    val fr = reads.forkAndForget()
    writes.invoke()
    fr.join() shouldBe count
  }

  "concurrent take and put" {
    val count = 1_000
    val mVar = ConcurrentVar.empty<Int>()
    val ref = Atomic(0)
    val takes = ForkAndForget {
      (0 until count)
        .parTraverse {
          val x = mVar.read() + mVar.take()
          ref.update { it + x }
        }
    }

    val puts = ForkAndForget { (0 until count).parTraverse { mVar.put(1) } }

    takes.join()
    puts.join()
    ref.get() shouldBe count * 2
  }

  "put is cancellable" {
    val mVar = ConcurrentVar(0)
    ForkAndForget { mVar.put(1) }
    val p2 = ForkAndForget { mVar.put(2) }
    ForkAndForget { mVar.put(3) }
    sleep(10.milliseconds) // Give put callbacks a chance to register
    p2.cancel()
    mVar.take()
    val r1 = mVar.take()
    val r3 = mVar.take()
    setOf(r1, r3) shouldBe setOf(1, 3)
  }

  "take is cancellable" {
    val mVar = ConcurrentVar.empty<Int>()
    val t1 = ForkAndForget { mVar.take() }
    val t2 = ForkAndForget { mVar.take() }
    val t3 = ForkAndForget { mVar.take() }
    sleep(10.milliseconds) // Give take callbacks a chance to register
    t2.cancel()
    mVar.put(1)
    mVar.put(3)
    val r1 = t1.join()
    val r3 = t3.join()
    setOf(r1, r3) shouldBe setOf(1, 3)
  }

  "read is cancellable" {
    val mVar = ConcurrentVar.empty<Int>()
    val finished = Promise<Int>()
    val fiber = ForkAndForget {
      mVar.read()
      finished.complete(1)
    }
    sleep(100.milliseconds) // Give read callback a chance to register
    fiber.cancel()
    mVar.put(10)
    val fallback = suspend { sleep(200.milliseconds); 0 }
    raceN(finished::get, fallback) shouldBe Either.Right(0)
  }
})

// Signaling using null, because we need to detect completion
private typealias Channel<A> = ConcurrentVar<A?>

private tailrec suspend fun takePutTestIsStacksafe(n: Int, acc: Int, ch: ConcurrentVar<Int>): Int =
  if (n <= 0) acc
  else {
    val x = ch.take()
    ch.put(1)
    takePutTestIsStacksafe(n = n - 1, acc = acc + x, ch = ch)
  }

suspend fun producerParallel(ch: Channel<Long>, list: List<Long>): Unit {
  list.map { ch.put(it) }
  ch.put(null) // we are done!
}

tailrec suspend fun consumerParallel(ch: Channel<Long>, sum: Long): Long {
  val x = ch.take()
  return if (x != null) consumerParallel(ch, sum + x) // next please
  else sum
}

//  java.lang.UnsupportedOperationException: This class is an internal synthetic class generated by
//  the Kotlin compiler, such as an anonymous class for a lambda, a SAM wrapper, a callable reference, etc.
//  It's not a Kotlin class or interface, so the reflection library has no idea what declarations does it have.
//  Please use Java reflection to inspect this class: class arrow.fx.coroutines.MVarTest$1$14$1
// TODO CREATE TICKET KOTLIN BUG TRACKER. HAVING THIS IN THE CLASS BLEW UP WITH THIS @ RUNTIME
fun testStackSequential(channel: ConcurrentVar<Int>): Triple<Int, suspend () -> Int, suspend () -> Unit> {
  val count = 10000
  return Triple(
    count,
    suspend { readLoop(count, 0, channel) },
    suspend { writeLoop(count, channel) }
  )
}

tailrec suspend fun readLoop(n: Int, acc: Int, channel: ConcurrentVar<Int>): Int =
  if (n > 0) {
    channel.read()
    channel.take()
    readLoop(n - 1, acc + 1, channel)
  } else acc

tailrec suspend fun writeLoop(n: Int, channel: ConcurrentVar<Int>): Unit =
  if (n > 0) {
    channel.put(1)
    writeLoop(n - 1, channel)
  } else Unit
