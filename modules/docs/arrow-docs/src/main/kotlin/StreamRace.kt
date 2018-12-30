import arrow.effects.ObservableK
import arrow.effects.k
import arrow.effects.racePair
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) = runBlocking {

  val base = Observable.just(1, 2, 3, 4, 5)
  ObservableK.racePair(
    Dispatchers.Default,
    base.flatMap { _ -> Observable.timer(2, TimeUnit.SECONDS).map { "A" } }.k(),
    base.flatMap { _ -> Observable.timer(1, TimeUnit.SECONDS).map { "B" } }.k()
  ).flatMap {
    ObservableK {
      it.fold({ (str, fiber) ->
        println("A WON [$str]")
      }, { (fiber, str) ->
        println("B WON [$str]")
      })
    }
  }
    .observable
    .subscribe()

  delay(10000)
}