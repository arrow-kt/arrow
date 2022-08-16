package arrow.core.continuations

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers

sealed interface MyError
sealed interface SubError : MyError
sealed interface OtherError : MyError {
  object Actual : OtherError
}

context(Shift<SubError>)
  suspend fun subprogram(): Unit =
  println("Hello SubProgram!")

context(Shift<OtherError>)
  suspend fun otherprogram(): Unit =
  println("Hello OtherProgram!")

context(Shift<OtherError>)
  suspend fun fail(): MyResponse =
  shift(OtherError.Actual)

fun main() =
  runBlocking(Dispatchers.Default) {
    effect<MyError, Unit> {
      subprogram()
      otherprogram()
      fail()
    }.fold(::println) { }
    // Hello SubProgram!
    // Hello OtherProgram!
    // OtherError$Actual@7cd62f43
  }

sealed interface MyResponse
object EmptyResponse : MyResponse
data class ErrorResponse(val error: Throwable) : MyResponse
data class BodyResponse(val body: String) : MyResponse

context(Shift<SubError>)
  suspend fun respondWithBody(): BodyResponse =
  BodyResponse("Hello Program!")

context(Shift<OtherError>)
  suspend fun attemptOrError(): MyResponse =
  ErrorResponse(RuntimeException("Oh no!"))

fun respond(): Effect<MyError, MyResponse> =
  effect {
    when (attemptOrError()) {
      is BodyResponse -> respondWithBody()
      EmptyResponse -> EmptyResponse
      is ErrorResponse -> fail()
    }
  }
