package com.pacoworks.typeclasses.basics.solved.advanced

import arrow.core.Either
import arrow.typeclasses.Index
import arrow.typeclasses.User
import com.pacoworks.typeclasses.basics.solved.DomainMapper
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

// Have you already checked the changes to the Framework?

interface RequestOperationsAsync :
  DaoOperationsKAsync, NetworkOperationsKAsync, DomainMapper, CoroutineContext {

  suspend fun Index.fetchUser(): User = withContext(this@RequestOperationsAsync) {
    Either.catch {
      queryUser().toUserFromDatabase()
    }.orNull() ?: requestUser().toUserFromNetwork()
  }
}

// Okay, so Usage should be simple, right?
