package com.pacoworks.typeclasses.basics.solved.advanced

import arrow.Kind
import arrow.core.left
import arrow.core.right
import arrow.effects.typeclasses.Async
import arrow.typeclasses.Index
import arrow.typeclasses.Query
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto

// Step 3 - Extract interfaces with key

interface NetworkFetcher<N> {
  fun fetch(id: Int, headers: Map<String, String>): UserDto
  fun fetchAsync(id: Int, headers: Map<String, String>, fe: (Throwable) -> Unit, f: (UserDto) -> Unit): Unit
}

interface DaoFetcher<D> {
  fun query(s: Query): UserDao
  fun queryAsync(s: Query, fe: (Throwable) -> Unit, f: (UserDao) -> Unit): Unit
}

// Add the key as a requirement

interface NetworkOperationsKAsync<F, N> : Async<F>, NetworkFetcher<N> {

  fun Index.requestUser(): Kind<F, UserDto> =
    async { _, cb -> fetchAsync(this, kotlin.collections.mapOf("1" to "2"), { cb(it.left()) }, { cb(it.right()) }) }
}

interface DaoOperationsKAsync<F, D> : Async<F>, DaoFetcher<D> {

  fun Index.queryUser(): Kind<F, UserDao> =
    async { _, cb -> queryAsync("SELECT * from Users where userId = $this", { cb(it.left()) }, { cb(it.right()) }) }
}

// Let's apply it to the Business Logic
