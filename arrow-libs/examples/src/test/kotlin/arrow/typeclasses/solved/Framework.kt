@file:Suppress("unused")

package arrow.typeclasses.solved

import arrow.core.Either
import arrow.typeclasses.DaoDatabase
import arrow.typeclasses.Index
import arrow.typeclasses.NetworkModule
import arrow.typeclasses.UserDao
import arrow.typeclasses.UserDto
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// Step 0 - extract interface

interface NetworkOperations {
  val network: NetworkModule

  suspend fun Index.requestUser(): UserDto =
    network.fetch(this, mapOf("1" to "2"))
}

interface DaoOperations {
  val dao: DaoDatabase

  suspend fun Index.queryUser(): UserDao =
    dao.query("SELECT * from Users where userId = $this")

  suspend fun Index.queryCompany(): UserDao =
    dao.query("SELECT * from Companies where companyId = $this")
}

// We should probably abstract the Mapper too

// Step 1 - wrap

interface NetworkOperationsUnsafe {
  val network: NetworkModule

  fun Index.requestUser(): UserDto =
    network.fetch(this, mapOf("1" to "2"))
}

interface DaoOperationsUnsafe {
  val dao: DaoDatabase

  fun Index.queryUser(): UserDao =
    dao.query("SELECT * from Users where userId = $this")
}

// Okay, but those calls can throw exceptions 😱

interface NetworkOperationsSync {
  val network: NetworkModule

  fun Index.requestUser(): Either<Throwable, UserDto> =
    Either.catch { network.fetch(this, mapOf("1" to "2")) }
}

interface DaoOperationsSync {
  val dao: DaoDatabase

  fun Index.queryUser(): Either<Throwable, UserDao> =
    Either.catch { dao.query("SELECT * from Users where userId = $this") }
}

// Let's see how these changes can also be applied to the Mapper

// Step 2 - Make operations asynchronous

interface NetworkOperationsAsync {
  val network: NetworkModule

  suspend fun Index.requestUser(): UserDto =
    suspendCoroutine { callback ->
      network.fetchAsync(this, mapOf("1" to "2"),
        { err -> callback.resumeWithException(err) },
        { value -> callback.resume(value) })
    }
}

interface DaoOperationsAsync {
  val dao: DaoDatabase

  suspend fun Index.queryUser(): UserDao =
    suspendCoroutine { callback ->
      dao.queryAsync("SELECT * from Users where userId = $this",
        { err -> callback.resumeWithException(err) },
        { value -> callback.resume(value) })
    }
}

// Awesome, let's go back to our Business Logic
