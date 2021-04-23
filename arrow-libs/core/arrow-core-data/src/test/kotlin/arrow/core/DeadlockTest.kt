package arrow.core

import arrow.core.test.UnitSpec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DeadlockTest : UnitSpec() {

  init {

    "classloader should not deadlock Validated initialization" {
      runBlocking {
        (0..10).map { i ->
          GlobalScope.launch {
            if (i % 2 == 0) {
              Validated.Invalid(Unit)
            } else {
              Validated.Valid(null)
            }
          }
        }.joinAll()
      }
    }

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
        }.joinAll()
      }
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

    "classloader should not deadlock Eval initialization" {
      runBlocking {
        (0..10).map { i ->
          GlobalScope.launch {
            if (i % 2 == 0) {
              Eval.Now(Unit)
            } else {
              Eval.Later { null }
            }
          }
        }.joinAll()
      }
    }
  }
}
