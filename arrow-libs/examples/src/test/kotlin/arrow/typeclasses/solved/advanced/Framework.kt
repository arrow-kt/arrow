package com.pacoworks.typeclasses.basics.solved.advanced

import arrow.typeclasses.Index
import arrow.typeclasses.Query
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Step 3 - Extract interfaces with key

interface NetworkFetcher {
  fun fetch(id: Int, headers: Map<String, String>): UserDto
  fun fetchAsync(id: Int, headers: Map<String, String>, fe: (Throwable) -> Unit, f: (UserDto) -> Unit): Unit
}

interface DaoFetcher {
  fun query(s: Query): UserDao
  fun queryAsync(s: Query, fe: (Throwable) -> Unit, f: (UserDao) -> Unit): Unit
}

// Add the key as a requirement

interface NetworkOperationsKAsync : NetworkFetcher {

  suspend fun Index.requestUser(): UserDto =
    suspendCoroutine { cb ->
      fetchAsync(this, mapOf("1" to "2"), { cb.resumeWithException(it) }, { cb.resume(it) })
    }
}

interface DaoOperationsKAsync : DaoFetcher {

  suspend fun Index.queryUser(): UserDao =
    suspendCoroutine { cb ->
      queryAsync("SELECT * from Users where userId = $this", { cb.resumeWithException(it) }, { cb.resume(it) })
    }
}

// Let's apply it to the Business Logic
