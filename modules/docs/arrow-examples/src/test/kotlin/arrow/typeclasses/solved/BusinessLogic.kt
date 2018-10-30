package com.pacoworks.typeclasses.basics.solved

import arrow.Kind
import arrow.core.Try
import arrow.core.recoverWith
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.Index
import arrow.typeclasses.User
import kotlin.coroutines.CoroutineContext

// Step 0 - Compose operations

interface RequestOperations : DaoOperations, NetworkOperations, DomainMapper {
  fun Index.fetchUser(): Try<User> =
    queryUser().toUserFromDatabase()
      .recoverWith { requestUser().toUserFromNetwork() }
}

// So far so good. Let's get rid of Try! Back to the Framework

// Step 1 - Now with generic containers

interface RequestOperationsSync<F> : DaoOperationsSync<F>, NetworkOperationsSync<F>, DomainMapperSync<F> {
  fun Index.fetchUser(): Kind<F, User> =
    queryUser().toUserFromDatabase()
      .handleErrorWith { requestUser().toUserFromNetwork() }
}

// We don't want to execute them right now, silly!

interface RequestOperationsLazy<F> : DaoOperationsSync<F>, NetworkOperationsSync<F>, DomainMapperSync<F>, MonadDefer<F> {
  fun Index.fetchUser(): Kind<F, User> = defer {
    queryUser().toUserFromDatabase()
      .handleErrorWith { requestUser().toUserFromNetwork() }
  }
}

// That makes the whole operation lazy, but the Framework is still oblivious to threading

// Step 2 - Compose the asynchronous elements

interface RequestOperationsAsync<F> : DaoOperationsAsync<F>, NetworkOperationsAsync<F>, DomainMapperSync<F>, Async<F> {
  val ctx: CoroutineContext

  fun Index.fetchUser(): Kind<F, User> = defer(ctx) {
    queryUser().toUserFromDatabase()
      .handleErrorWith { requestUser().toUserFromNetwork() }
  }
}

// Okay, now what is that mysterious F value and how do you Use it?
