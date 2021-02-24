@file:Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")

package com.pacoworks.typeclasses.basics.solved

import arrow.typeclasses.DaoDatabase
import arrow.typeclasses.Index
import arrow.typeclasses.NetworkModule
import arrow.typeclasses.Query
import arrow.typeclasses.User
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto
import com.pacoworks.typeclasses.basics.solved.advanced.RequestOperationsAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

suspend fun RequestOperationsAsync.fetchUser(idx: Index): User =
  idx.fetchUser()

class MyViewModel(dep: RequestOperationsAsync) : RequestOperationsAsync by dep {
  val scope = CoroutineScope(dep)

  fun onStart() {
    scope.launch { 1.fetchUser() }
  }

  fun onDestroy() {
    scope.cancel()
  }
}

class MyActivity {
  val scope = CoroutineScope(Dispatchers.Main)

  fun onStart() {
    dependenciesAsValues.run {
      scope.launch { 1.fetchUser() }
    }

    runBlocking { dependenciesAsValues.fetchUser(1) }

    runBlocking { MyViewModel(dependenciesAsValues).fetchUser(1) }
  }

  fun onDestroy() {
    scope.cancel()
  }
}

val dependenciesAsValues: RequestOperationsAsync =
  object : RequestOperationsAsync, CoroutineContext by Dispatchers.Default {

    val network: NetworkModule = NetworkModule()
    val dao: DaoDatabase = DaoDatabase()

    override fun fetch(id: Int, headers: Map<String, String>): UserDto = network.fetch(id, headers)

    override fun fetchAsync(id: Int, headers: Map<String, String>, fe: (Throwable) -> Unit, f: (UserDto) -> Unit) =
      network.fetchAsync(id, headers, fe, f)

    override fun query(s: Query): UserDao =
      dao.query(s)

    override fun queryAsync(s: Query, fe: (Throwable) -> Unit, f: (UserDao) -> Unit) =
      dao.queryAsync(s, fe, f)
  }

// Interlude: Retrofit, Dependency Injection and KEEP 87
