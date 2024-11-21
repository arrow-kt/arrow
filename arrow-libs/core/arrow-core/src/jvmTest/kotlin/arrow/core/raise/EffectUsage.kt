package arrow.core.raise

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers

sealed interface MyError
sealed interface SubError : MyError
sealed interface OtherError : MyError {
  object Actual : OtherError
}

suspend fun Raise<SubError>.subprogram(): Unit =
  println("Hello SubProgram!")

suspend fun Raise<OtherError>.otherprogram(): Unit =
  println("Hello OtherProgram!")

suspend fun Raise<OtherError>.fail(): MyResponse =
  raise(OtherError.Actual)

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

suspend fun Raise<SubError>.respondWithBody(): BodyResponse =
  BodyResponse("Hello Program!")

suspend fun Raise<OtherError>.attemptOrError(): MyResponse =
  ErrorResponse(RuntimeException("Oh no!"))

fun respond(): Effect<MyError, MyResponse> =
  effect {
    when (attemptOrError()) {
      is BodyResponse -> respondWithBody()
      EmptyResponse -> EmptyResponse
      is ErrorResponse -> fail()
    }
  }
