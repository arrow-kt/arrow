/**
 * The following Gist demonstrates usage of `EnvFx`.
 * EnvFx is a data type that packs the Reader, Error and IO capabilities over the Kotlin suspended environment.
 * This models `Deps -> Either<Error, SuccessValue>` allowing to track algebras and effects as subtype
 * contrains of a phantom type `R` that is carried around programs to provide syntactic access to the service defined functions.
 */

package arrow.effects

import arrow.core.Either
import arrow.effects.extensions.envfx.applicativeError.raiseError
import arrow.effects.extensions.envfx.fx.fx
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.env.EnvFx
import arrow.effects.suspended.env.env
import arrow.effects.suspended.env.toFx
import arrow.unsafe

/** user algebra **/
interface Service1 {
  suspend fun foo(): Unit
}

/** user algebra **/
interface Service2 {
  suspend fun bar(): Unit
}

/** swapable interpreter **/
object Service1Impl : Service1 {
  override suspend fun foo(): Unit =
    println("foo")
}

/** swapable interpreter **/
object Service2Impl : Service2 {
  override suspend fun bar(): Unit =
    println("bar")
}

/** Module with services dependencies that fulfills the program constrains **/
class Module(
  service1: Service1 = Service1Impl,
  service2: Service2Impl = Service2Impl
) : Service1 by service1, Service2 by service2 {
  companion object {
    fun impl(): Module = Module()
  }
}

/** custom exceptionless error handling **/
sealed class CustomError

object Stop : CustomError()

/**
 * Programs declare the service they need by constraining the `R` type arg
 * Each Service declaration over R needs to be fullfilled by an object that implements
 * the service interfaces.
 *
 * `R` is exposed as this so the user has direct access to the syntax of the functions directly declared
 * in the services without the need to prefix access with a service name. That is why you can invoke `foo` and `bar` direcly.
 * `env` gives you this support placing `R` in the `this` scope
 **/
fun <R> program(): EnvFx<R, CustomError, Unit>
  where R : Service1, R : Service2 = //R has Service1 and Service2 capabilities so we can invoke `foo` and `bar` polymorphically
  env {
    fx {
      !effect { foo() }
      !effect { bar() }
      !Stop.raiseError<R, CustomError, Unit>()
    }
  }

/**
 * You can go from `EnvFx` to `Fx` at the edge providing the dependency that fulfills your program
 * constrains.
 */
fun main() {
  val result: Either<CustomError, Unit> =
    unsafe { runBlocking { program<Module>().toFx(Module.impl()) } }
  println(result)
  //foo
  //bar
  //Left(a=arrow.effects.Stop@4c98385c)
}
