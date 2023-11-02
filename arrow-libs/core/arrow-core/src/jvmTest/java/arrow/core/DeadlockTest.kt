package arrow.core

import io.kotest.core.spec.style.StringSpec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DeadlockTest : StringSpec({

  "classloader should not deadlock Either initialization" {
    runBlocking {
      (0..10).map { i ->
        GlobalScope.launch {
          if (i % 2 == 0) {
            Either.Left(Unit)
          } else {
            Either.Right(null)
          }
        }
      }
    }.joinAll()
  }

  "classloader should not deadlock Option initialization" {
    runBlocking {
      (0..10).map { i ->
        GlobalScope.launch {
          if (i % 2 == 0) {
            None
          } else {
            Some(null)
          }
        }
      }.joinAll()
    }
  }

  "classloader should not deadlock Ior initialization" {
    runBlocking {
      (0..10).map { i ->
        GlobalScope.launch {
          if (i % 2 == 0) {
            Ior.Left(Unit)
          } else {
            Ior.Right(null)
          }
        }
      }.joinAll()
    }
  }
})
