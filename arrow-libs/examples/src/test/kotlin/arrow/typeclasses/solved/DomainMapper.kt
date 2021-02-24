package com.pacoworks.typeclasses.basics.solved

import arrow.typeclasses.User
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto
import arrow.typeclasses.realWorld

// Step 0

interface DomainMapper {
  suspend fun UserDto.toUserFromNetwork(): User =
    realWorld { User(id) }

  suspend fun UserDao.toUserFromDatabase(): User =
    realWorld { User(id) }
}

// See how the Business Logic has to be updated!
