package com.pacoworks.typeclasses.basics.solved

import arrow.Kind
import arrow.core.Try
import arrow.typeclasses.MonadError
import arrow.typeclasses.User
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto
import arrow.typeclasses.realWorld

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
