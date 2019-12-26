package arrow.aql.tests

import arrow.core.Option
import arrow.fx.IO
import arrow.fx.IOResult
import arrow.fx.flatMapLeft
import arrow.fx.handleErrorWith
import arrow.fx.unsafeRunSync

data class UserId(val value: String)
data class User(val userId: UserId)
data class Task(val value: String)

sealed class UserLookupError : RuntimeException() // assuming you are using exceptions
data class UserNotInLocalStorage(val user: User) : UserLookupError()
data class UserNotInRemoteStorage(val user: User) : UserLookupError()
data class UnknownError(val underlying: Throwable) : UserLookupError()

interface DataSource {
  fun allTasksByUser(user: User): IO<UserLookupError, List<Task>>
}

interface Repository {
  fun allTasksByUser(user: User): IO<Nothing, List<Task>>
}

class LocalDataSource : DataSource {
  private val localCache: Map<User, List<Task>> =
    mapOf(User(UserId("user1")) to listOf(Task("LocalTask asssigned to user1")))

  override fun allTasksByUser(user: User): IO<UserLookupError, List<Task>> =
    Option.fromNullable(localCache[user]).fold(
      { IO.raiseError(UserNotInLocalStorage(user)) },
      { IO.just(it) }
    )
}

class RemoteDataSource : DataSource {
  private val internetStorage: Map<User, List<Task>> =
    mapOf(User(UserId("user2")) to listOf(Task("Remote Task assigned to user2")))

  override fun allTasksByUser(user: User): IO<UserLookupError, List<Task>> =
    IO.async { cb ->
      // allows you to take values from callbacks and place them back in the context of `F`
      Option.fromNullable(internetStorage[user]).fold(
        { cb(IOResult.Error(UserNotInRemoteStorage(user))) },
        { cb(IOResult.Success(it)) }
      )
    }
}

class DefaultRepository(
  val localDS: DataSource,
  val remoteDS: RemoteDataSource
) : Repository {

  override fun allTasksByUser(user: User): IO<Nothing, List<Task>> =
    localDS.allTasksByUser(user).handleErrorWith(
      { t -> IO.raiseException<List<Task>>(UnknownError(t)) },
      {
        remoteDS.allTasksByUser(user).flatMapLeft { IO.raiseException<List<Task>>(it) }
      }
    )
}

class Module {
  val localDataSource: LocalDataSource = LocalDataSource()
  val remoteDataSource: RemoteDataSource = RemoteDataSource()
  val repository: Repository = DefaultRepository(localDataSource, remoteDataSource)
}

object test {
  @JvmStatic
  fun main(args: Array<String>) {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))
    val ioModule = Module()
    ioModule.run {
      println(repository.allTasksByUser(user1).attempt().unsafeRunSync())
      // Right(b=[Task(value=LocalTask asssigned to user1)])

      println(repository.allTasksByUser(user2).attempt().unsafeRunSync())
      // Right(b=[Task(value=Remote Task assigned to user2)])

      println(repository.allTasksByUser(user3).attempt().unsafeRunSync())
      // Left(a=UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user))))
    }
  }
}
