package arrow.core

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class DeadlockTest {

  @Test fun classLoaderShouldNotDeadlockEither() = runTest {
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

  @Test fun classLoaderShouldNotDeadlockOption() = runTest {
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

  @Test fun classLoaderShouldNotDeadlockIor() = runTest {
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
}
