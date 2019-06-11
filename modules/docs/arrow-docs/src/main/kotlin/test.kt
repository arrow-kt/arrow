import arrow.data.ListK
import arrow.data.extensions.listk.traverse.traverse
import arrow.data.k
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.asCoroutineContext
import arrow.effects.extensions.IOConcurrent
import arrow.effects.fix
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.Dispatchers
import arrow.effects.typeclasses.milliseconds
import arrow.effects.typeclasses.seconds
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

fun IO.Companion.concurrent(dispatchers: Dispatchers<ForIO>): Concurrent<ForIO> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

private val bound = max(2, Runtime.getRuntime().availableProcessors())
private val global = Executors.newFixedThreadPool(bound, object : ThreadFactory {
  val ctr = AtomicInteger(0)
  override fun newThread(r: Runnable) = Thread(r).apply {
    name = "ioapp-compute-${ctr.getAndIncrement()}"
    isDaemon = true
  }
}).asCoroutineContext()
private val dispatchers = object : Dispatchers<ForIO> {
  override fun default(): CoroutineContext = global
}

fun main() {

  val ids = listOf(3, 2, 1).k()

  fun io(i: Int): IO<Unit> = IO.unit.bracketCase(use = {
    IO.effect { println("Started with $i - on ${Thread.currentThread().name}") }
      .followedBy(IO.concurrent(dispatchers).sleep((2 * i).seconds))
      .followedBy(IO.effect { println("finished processing $i - on ${Thread.currentThread().name}") })
  }, release = { _, ec -> IO.effect { println("exitCase: $ec for $i") } })

  fun fail(i: Int): IO<Unit> = IO.effect { println("I am going to boom @ $i") }
    .followedBy(IO.concurrent(dispatchers).sleep(500.milliseconds))
    .followedBy(IO.raiseError(RuntimeException("Fuck it")))

  val CF = IO.concurrent(dispatchers)

  CF.run {
    ids.parTraverse(ListK.traverse()) { i ->
      if (i == 2) fail(i)
      else io(i)
    }
  }.fix()
    .unsafeRunSync()
    .let(::println)
}
