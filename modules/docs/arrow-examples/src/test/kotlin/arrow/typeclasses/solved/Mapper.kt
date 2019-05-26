package com.pacoworks.typeclasses.basics.solved

import arrow.Kind
import arrow.core.Try
import arrow.core.typeclasses.MonadError
import arrow.core.typeclasses.User
import arrow.core.typeclasses.UserDao
import arrow.core.typeclasses.UserDto
import arrow.core.typeclasses.realWorld

// Step 0

interface DomainMapper {
  fun Try<UserDto>.toUserFromNetwork(): Try<User> =
    flatMap { user -> Try { realWorld { User(user.id) } } }

  fun Try<UserDao>.toUserFromDatabase(): Try<User> =
    flatMap { user -> Try { realWorld { User(user.id) } } }
}

// See how the Business Logic has to be updated!

// Step 1: lift functions to be usable with Kind

interface DomainMapperSync<F> : MonadError<F, Throwable> {
  fun Kind<F, UserDto>.toUserFromNetwork(): Kind<F, User> =
    flatMap { user -> catch { realWorld { User(user.id) } } }

  fun Kind<F, UserDao>.toUserFromDatabase(): Kind<F, User> =
    flatMap { user -> catch { realWorld { User(user.id) } } }
}

// Another version of the Business Logic is now possible
