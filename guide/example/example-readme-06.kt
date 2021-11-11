// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme06

import arrow.*
import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import io.kotest.matchers.collections.*
import io.kotest.assertions.*
import io.kotest.matchers.*
import io.kotest.matchers.types.*
import kotlin.coroutines.cancellation.CancellationException
import io.kotest.property.*
import io.kotest.property.arbitrary.*
import arrow.core.test.generators.*

suspend fun <A> awaitExitCase(exit: CompletableDeferred<ExitCase>): A =
  guaranteeCase(::awaitCancellation) { exitCase -> exit.complete(exitCase) }

suspend fun <A> CompletableDeferred<A>.getOrNull(): A? =
  if (isCompleted) await() else null

suspend fun test() = checkAll(Arb.string()) { error ->
  val exits = (0..3).map { CompletableDeferred<ExitCase>() }
  cont<String, List<Unit>> {
    (0..4).parTraverse { index ->
      if (index == 4) shift(error)
      else awaitExitCase(exits[index])
    }
  }.fold({ msg -> msg shouldBe error }, { fail("Int can never be the result") })
  // It's possible not all parallel task got launched, and in those cases awaitCancellation never ran
  exits.forEach { exit -> exit.getOrNull()?.shouldBeTypeOf<ExitCase.Cancelled>() }
}
