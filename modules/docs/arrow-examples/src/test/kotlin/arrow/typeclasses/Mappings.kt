package arrow.typeclasses

fun UserDao.toUserFromDatabase(): User = realWorld {
  User(id)
}

fun UserDto.toUserFromNetwork(): User = realWorld {
  User(id)
}
