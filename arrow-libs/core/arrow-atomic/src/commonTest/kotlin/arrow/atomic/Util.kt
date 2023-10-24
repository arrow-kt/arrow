package arrow.atomic

import io.kotest.common.runBlocking
import kotlinx.coroutines.test.runTest
import io.kotest.common.Platform
import io.kotest.common.platform

fun runBlockingOnNative(testBody: suspend () -> Unit) {
  if (platform == Platform.Native)
    runBlocking(testBody)
  else
    runTest { testBody() }
}

