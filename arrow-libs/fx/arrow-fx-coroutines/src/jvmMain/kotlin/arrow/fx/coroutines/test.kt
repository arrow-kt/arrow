package arrow.fx.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource


@OptIn(ExperimentalTime::class)
public suspend fun main(): Unit {
//  fixedRate(Duration.milliseconds(100))
//    .map {
//      println("I am here: ${timeInMillis()}")
//    }.collect()

//  (0..10).asFlow()
//    .metered(Duration.nanoseconds(0.3))
//    .toList().let(::println)
//
//  (0..10).asFlow()
//    .metered(Duration.milliseconds(0))
//    .toList().let(::println)
//
//  (10 downTo 0).asFlow()
//    .metered(Duration.milliseconds(200))
//    .scan(timeInMillis()) { now, i ->
//      delay(i.toLong() * 10)
//      val nextNow = timeInMillis()
//      val lapsed = (nextNow - now)
//      println("lapsed $lapsed")
//      nextNow
//    }.toList().let(::println)

//  (0..100)
//    .asFlow()
//    .metered(Duration.milliseconds(500))
//    .map { i ->
//      if(i % 2 == 0) print(".")
//      else print(" ")
//    }.collect()

  (0..20)
    .asFlow()
    .metered(Duration.seconds(1), false)
    .map { i ->
      if (i == 0) delay(4_000)
      else print(" . ")
    }.collect()

//  flow {
//    while (true) {
//      delay(1000)
//      emit(Unit)
//    }
//  }.zip((0..10).asFlow()) { _, a -> a }
//    .scan(Pair(timeInMillis(), 0L)) { (now, elapsed), index ->
//      delay(100L)
//      val nextNow = timeInMillis()
//      Pair(nextNow, elapsed + (nextNow - now))
//    }.map { it.second }
//    .toList()
//    .let(::println)
//
//  (0..10).asFlow()
//    .metered(Duration.seconds(1))
//    .scan(Pair(timeInMillis(), 0L)) { (now, elapsed), index ->
//      delay(100L)
//      val nextNow = timeInMillis()
//      Pair(nextNow, elapsed + (nextNow - now))
//    }.map { it.second }
//    .toList()
//    .let(::println)

}
