// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl08

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.catch
import io.kotest.matchers.shouldBe

fun test() {
  catch({ throw RuntimeException("BOOM") }) { t ->
    "fallback"
  } shouldBe "fallback"

  fun fetchId(): Int = throw RuntimeException("BOOM")

  either {
    catch({ fetchId() }) { t ->
      raise("something went wrong: $t.message")
    }
  } shouldBe Either.Left("something went wrong: BOOM")
}
