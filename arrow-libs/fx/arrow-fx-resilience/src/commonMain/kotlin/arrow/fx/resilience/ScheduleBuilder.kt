package arrow.fx.resilience

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration

public class ScheduleBuilder<Delay, Input, Output> {
  private sealed interface State<Delay, Input, Output> {
    data class Done<Delay, Input, Output>(
      val output: Output
    ): State<Delay, Input, Output>
    data class Continue<Delay, Input, Output>(
      val output: Output,
      val delay: Delay,
      val continuation: Continuation<Input>
    ): State<Delay, Input, Output>
  }

  private var current: State<Delay, Input, Output>? = null

  public suspend fun yield(output: Output, delay: Delay): Input = suspendCoroutine {
    current = State.Continue(output, delay, it)
  }

  public fun done(output: Output) {
    current = State.Done(output)
  }

  public fun execute(): Decision2<Delay, Input, Output> = when (val c = current) {
    is State.Continue -> Decision2.Continue(c.output, c.delay) { i ->
      c.continuation.resume(i)
      execute()
    }
    is State.Done -> Decision2.Done(c.output)
    null -> throw IllegalStateException()
  }
}

public fun <Delay, Input, Output> schedule(
  block: suspend ScheduleBuilder<Delay, Input, Output>.(initialInput: Input) -> Output
): Schedule2<Delay, Input, Output> = Next2 { i ->
  val machine = ScheduleBuilder<Delay, Input, Output>()
  val function: suspend ScheduleBuilder<Delay, Input, Output>.() -> Output = { block(i) }
  function.startCoroutine(
    machine,
    Continuation(EmptyCoroutineContext) {
      machine.done(it.getOrThrow())
    }
  )
  machine.execute()
}

public fun <Delay, Input, Output> continuousSchedule(
  block: suspend ScheduleBuilder<Delay, Input, Output>.(initialInput: Input) -> Unit
): Schedule2<Delay, Input, Output> = Next2 { i ->
  val machine = ScheduleBuilder<Delay, Input, Output>()
  val function: suspend ScheduleBuilder<Delay, Input, Output>.() -> Unit = { block(i) }
  function.startCoroutine(
    machine,
    Continuation(EmptyCoroutineContext) {
      throw IllegalStateException()
    }
  )
  machine.execute()
}

public fun <Input> recurs2(n: Int): Schedule2<Duration, Input, Long> = schedule {
  var counter: Long = 0
  while (counter < n) {
    yield(counter, Duration.ZERO)
    counter++
  }
  counter
}

public fun <Input> spaced2(duration: Duration): Schedule2<Duration, Input, Duration> = continuousSchedule {
  while(true) {
    yield(duration, duration)
  }
}

public fun <Input> linear2(base: Duration): Schedule2<Duration, Input, Duration> = continuousSchedule {
  var duration = base
  while (true) {
    yield(duration, duration)
    duration += base
  }
}
