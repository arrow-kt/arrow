@file:Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")

package com.pacoworks.typeclasses.basics.solved.advanced

import arrow.fx.ForIO
import arrow.Kind
import arrow.fx.IO
import arrow.fx.extensions.io.async.async
import arrow.fx.fix
import arrow.fx.typeclasses.Async
import arrow.typeclasses.DaoDatabase
import arrow.typeclasses.Index
import arrow.typeclasses.NetworkModule
import arrow.typeclasses.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

fun <F, N, D> RequestOperationsKAsync<F, N, D>.fetchUser(idx: Index): Kind<F, User> =
  idx.fetchUser()

class MyViewModel<F, N, D>(dep: RequestOperationsKAsync<F, N, D>) : RequestOperationsKAsync<F, N, D> by dep {
  fun onStart() {
    1.fetchUser()
  }
}

class MyActivity {
  fun onStart() {
    dependenciesAsTypes.run { 1.fetchUser() }.fix().unsafeRunSync()

    runBlocking { dependenciesAsTypes.fetchUser(1) }

    runBlocking { MyViewModel(dependenciesAsTypes).fetchUser(1) }
  }
}

val dependenciesAsTypes: RequestOperationsKAsync<ForIO, NetworkModule, DaoDatabase> =
  object : RequestOperationsKAsync<ForIO, NetworkModule, DaoDatabase>,
    Async<ForIO> by IO.async(),
    NetworkFetcher<NetworkModule> by NetworkModule.networkFetcher(),
    DaoFetcher<DaoDatabase> by DaoDatabase.daoFetcher(),
    CoroutineContext by Dispatchers.Default {}

// Wait, where did DaoDatabase.daoFetcher() NetworkModule.networkFetcher() and come from?
