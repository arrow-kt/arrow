package arrow.test.concurrency

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.asCoroutineDispatcher
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun newCoroutineDispatcher(name: String): CoroutineDispatcher =
  ThreadPoolExecutor(10, 50, 30, TimeUnit.SECONDS, LinkedBlockingDeque(), ThreadFactory { run ->
    Thread(run).apply {
      this.name = name
    }
  }).asCoroutineDispatcher()