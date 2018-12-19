import arrow.core.Try
import arrow.effects.*
import arrow.effects.deferredk.async.async
import arrow.effects.deferredk.bracket.uncancelable
import arrow.effects.deferredk.monad.flatMap
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.monad.flatMap
import io.kotlintest.matchers.should
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.AssertionError

//Two options
//Disable and ignore MonadDefer laws and make DeferredK a Lazy version of Deferred but keep it memoized.
//Pros: this keeps it closer to the original `Deferred`, you could argue that this can be seen as a plus to make the step in lower.
//Cons: we make it unlawful by ignoring the MonadDefer laws, but we also ignore the stack-safety laws for RxJava.

//Keep it as is which in my opinion is also faulty because interaction with the original API makes it unlawful.
//Pros: ???
//Cons: With the current implementation we make it harder to reason about code, which makes it much less appealing as a poster child to come and use Arrow.
//Cons: We alter the lifecycle of `Deferred` which almost makes it a different data type. Since the API also behaves differently
// i.e. once a `Deferred` `isCompleted` it can never be `isActive == true` again. If you wrap it in `DeferredK` and call `start` it might
//given that nested within the `Deferred` is an `DeferredK` operation which runs again on `start` which is not the case when you're dealing with a regular `Deferred`.

//Third option??
//Write IO inter-op layer with Deferred??

//Fourth ??

fun main(args: Array<String>) = runBlocking<Unit> {

  val async = async { println("Hello") }.k().flatMap {
    DeferredK { println("World!") }
  }
  async.await()
  //Hello
  //World!
  async.await()
  //World!
  async.await()
  //World!

  println("#############################################################################################################")

  val def = DeferredK {
    println("I am running")
    delay(100)
  }

  println("isActive: ${def.isActive}, isCancelled: ${def.isCancelled}, isCompleted: ${def.isCompleted}")
  //isActive: false, isCancelled: false, isCompleted: false
  def.start() //I am running
  println("isActive: ${def.isActive}, isCancelled: ${def.isCancelled}, isCompleted: ${def.isCompleted}")
  //isActive: true, isCancelled: false, isCompleted: false
  def.join()
  println("isActive: ${def.isActive}, isCancelled: ${def.isCancelled}, isCompleted: ${def.isCompleted}")
  //isActive: false, isCancelled: false, isCompleted: true
  def.start() //I am running
  println("isActive: ${def.isActive}, isCancelled: ${def.isCancelled}, isCompleted: ${def.isCompleted}")
  //isActive: true, isCancelled: false, isCompleted: false
  def.cancelAndJoin()
  println("isActive: ${def.isActive}, isCancelled: ${def.isCancelled}, isCompleted: ${def.isCompleted}")
  //isActive: false, isCancelled: true, isCompleted: true

  Try {
    val io = IO.async<Unit> { conn, _ ->
      conn.push(IO { println("On cancel: Within async") })
      runBlocking {
        delay(1000)
        conn.cancel().fix().unsafeRunAsync {
          println("I finished cancelling $it")
        }
      }
    }
    println("ehhlo")
    io.unsafeRunSync()
  }.fold({ e ->
    println("I am here $e")
    e should { it is arrow.effects.ConnectionCancellationException }
  },
    { throw AssertionError("Expected exception of type arrow.effects.ConnectionCancellationException but caught no exception") })

//  Promise.uncancelable<ForIO, Unit>(IO.async()).flatMap { latch ->
//    IO.async<Unit> { conn, _ ->
//      conn.push(IO { println("cancelling") })
//    }
////      .uncancelable()
//      .runAsyncCancellable {
//        IO { println("runAsyncCancellable $it") }.flatMap {
//          latch.complete(Unit)
//        }
//      }
//      .map {
//        println("Hello")
//        it.invoke()
//      }
//      .flatMap { latch.get }
//  }.fix().unsafeRunSync()

}
