---
layout: docs-fx
title: "Parallel combinators"
permalink: /fx/parallel/
---

# Parallel functions

Arrow Fx benefits from the `suspend` syntax for very succinct programs without callbacks.
This allows us to use direct style syntax with asynchronous, concurrent and parallel operations while preserving effect control in the types and runtime.
The resulting expressions enjoy the same syntax that most OOP and Java programmers are already accustomed to—direct blocking imperative style.

This page discuss the semantics of parallel operations in Arrow Fx, which follows the system of Structured Concurrency.

You can find all functions and extension functions for Kotlin's Std in the API docs [here](/docs/apidocs/arrow-fx-coroutines/arrow.fx.coroutines/index.html#functions).

## Parallelization & Concurrency

Arrow Fx comes with a set of built-in concurrent and parallel functions and data types that acompany what you find in KotlinX Coroutines. This allows users to write complex parallel and concurrent code in suspend style, in a elegant and imperative way without wrappers.

To follow [Structured Concurrency](https://kotlinlang.org/docs/composing-suspending-functions.html#structured-concurrency-with-async), all parallel suspend operators in Arrow Fx behave in the following way:

#### Parallel exceptions

All parallel functions guarantee that when one of their children fails that all other children will be cancelled before the parallel function returns. This allows the other paralell running tasks to gracefully exit and close their resources before the parallel function returns.

```
import arrow.fx.coroutines.*
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val fiber = async {
        parZip({
            delay(100)
            throw RuntimeException("Boom")
        }, {
            guaranteeCase({
                never<Unit>()
            }) { exitCase -> println("I never complete: $exitCase") }
        }) { _, _ -> println("I am never called!") }
    }

    delay(200) // Wait until after task 1 threw
    fiber.await()
}

//sampleEnd
I never complete: Cancelled(exception=kotlinx.coroutines.JobCancellationException: Parent job is Cancelling; job=ScopeCoroutine{Cancelling}@157f5e97)

Exception in thread "main" java.lang.RuntimeException: Boom
  at TestKt$main$2$fiber$1$invokeSuspend$$inlined$parZip$1$1.invokeSuspend(ParZip.kt:679)
  at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
  at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
  at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:571)
  at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:750)
  at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:678)
  at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:665)
```

 In the snippet above we can see that the second task _never_ completes, and it gets cancelled with `ExitCase.Cancelled` by the `RuntimeException` that occurs in the first task.
 After `parZip` has gracefully finished, it will rethrow the original `RuntimeException`.
 
 All parallel operators you find in Arrow Fx Coroutines follow this same pattern when exceptions occur.

#### Parallel cancellation

All parallel functions guarantee that if they get cancelled, that all children will also get cancelled before the parallel function returns. This allows the other paralell running tasks to gracefully exit and close their resources before returning.

```
import arrow.fx.coroutines.*
import kotlinx.coroutines.*

//sampleStart
suspend fun main(): Unit = coroutineScope {
    val fiber = async {
        parZip({
            guaranteeCase({
                // Finish immediately
            }) { exitCase -> println("I completed immediately: $exitCase") }
        }, {
            guaranteeCase({
                never<Unit>()
            }) { exitCase -> println("I never complete: $exitCase") }
        }) { _, _ -> println("I am never called!") }
    }

    delay(50)
    fiber.cancel()
    fiber.await()
}

//sampleEnd
I completed immediately: ExitCase.Completed

I never complete: Cancelled(exception=kotlinx.coroutines.JobCancellationException: DeferredCoroutine was cancelled; job=DeferredCoroutine{Cancelling}@56783f97)

Exception in thread "main" kotlinx.coroutines.JobCancellationException: DeferredCoroutine was cancelled; job=DeferredCoroutine{Cancelled}@56783f97
```

 In the snippet above we can see that first task finished immediately and prints `ExitCase.Completed`, the second task _never_ completes, and it gets cancelled with `ExitCase.Cancelled` by the parent scope.
 After `parZip` has gracefully cancelled all it's children, `async` will throw the `JobCancellationException`.
 
 All parallel operators you find in Arrow Fx Coroutines follow this same pattern when exceptions occur.

### Co-operative cancellation / Semi-Automatic cancellation

The cancellation system is inherited from KotlinX Coroutines and works the same. See [KotlinX Coroutines documentation on cancellation](https://kotlinlang.org/docs/reference/coroutines/cancellation-and-timeouts.html).

All operators found in Arrow Fx check for cancellation, and make you `suspend` code cancellable.

A small example of an infinite loop below, where `parZip` checks for cancellation and this the `sleeper` function is cancellable even though it runs in an infinite recursion loop.

```kotlin:ank
import arrow.fx.coroutines.*
import kotlinx.coroutines.*

//sampleStart
tailrec suspend fun sleeper(): Unit {
    println("I am sleepy. I'm going to nap")
    parZip(                                   // <-- cancellation check-point
        { Thread.currentThread().name },
        { Thread.currentThread().name }
    ) { a, b -> println("A on $a, B on $b") }
    println("Too short nap.. Going to sleep some more")
    sleeper()                                // <-- cancellation check-point
}

suspend fun main(): Unit = coroutineScope {
    val fiber = async {
        guaranteeCase({ sleeper() }) { exitCase ->
            println("Somebody woke me up with $exitCase")
        }
    }
    delay(100)
    fiber.cancel()
}
//sampleEnd
```

### Racing parallel operations

Racing parallel task has an additional propety to the ones described above.

When racing multiple parallel tasks against each-other, the winning task cancels the others. When the winner of a race is known, we can cancel all other tasks because their results will get discarded anyway.
This allows the other paralell running tasks to gracefully exit and close their resources as soon as possible.

```kotlin
import arrow.fx.coroutines.*

//sampleStart
suspend fun main(): Unit {
    val winner = raceN({
        guaranteeCase({ never<Unit>() }) { exitcase ->
            println("I can never win the race. Finished with $exitcase")
        }
    }, {
        "I am going to win, immediately"
    })
    println(winner)
}
//sampleEnd
I can never win the race. Finished with Cancelled(exception=kotlinx.coroutines.JobCancellationException: DeferredCoroutine was cancelled; job=DeferredCoroutine{Cancelling}@89897d5)

Either.Right(I am going to win, immediately)
```

Here we see that if we race a first task that is never going to finish, with a second task that wins immediately that the first losing task gets cancelled before we declare the second task the winner.

So this also follows the pattern of [Structured Concurrency](https://kotlinlang.org/docs/composing-suspending-functions.html#structured-concurrency-with-async), where all children are completed before continuing. 
