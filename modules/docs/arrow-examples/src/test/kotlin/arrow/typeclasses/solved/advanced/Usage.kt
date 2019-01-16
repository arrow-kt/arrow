@file:Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")

package com.pacoworks.typeclasses.basics.solved.advanced

import arrow.Kind
import arrow.effects.coroutines.DeferredK
import arrow.effects.coroutines.ForDeferredK
import arrow.effects.coroutines.extensions.deferredk.async.async
import arrow.effects.coroutines.fix
import arrow.effects.coroutines.unsafeRunSync
import arrow.effects.typeclasses.Async
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

val dependenciesAsTypes: RequestOperationsKAsync<ForDeferredK, NetworkModule, DaoDatabase> =
  object : RequestOperationsKAsync<ForDeferredK, NetworkModule, DaoDatabase>,
    Async<ForDeferredK> by DeferredK.async(),
    NetworkFetcher<NetworkModule> by NetworkModule.networkFetcher(),
    DaoFetcher<DaoDatabase> by DaoDatabase.daoFetcher(),
    CoroutineContext by Dispatchers.Default {}

// Wait, where did DaoDatabase.daoFetcher() NetworkModule.networkFetcher() and come from?
