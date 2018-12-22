import arrow.core.*
import arrow.effects.*
import arrow.effects.instances.io.async.*
import arrow.effects.instances.io.monadDefer.*

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async


object Account
//Some impure API or code
class NetworkService {
  fun getAccounts(
    successCallback: (List<Account>) -> Unit,
    failureCallback: (Throwable) -> Unit) {
    kotlinx.coroutines.GlobalScope.async(Default) {
      println("Making API call")
      kotlinx.coroutines.delay(500)
      successCallback(listOf(Account))
    }
  }
  fun cancel(): Unit = kotlinx.coroutines.runBlocking {
    println("Cancelled, closing NetworkApi")
    kotlinx.coroutines.delay(500)
    println("Closed NetworkApi")
  }
}

fun main(args: Array<String>) {
  //sampleStart
  val getAccounts = Default.shift().flatMap {
    IO.async().cancelable<List<Account>> { cb ->
      val service = NetworkService()
      service.getAccounts(
        successCallback = { accs -> cb(Right(accs)) },
        failureCallback = { e -> cb(Left(e)) })
      delay({ service.cancel() })
    }
  }

  println(getAccounts)
  //sampleEnd
}