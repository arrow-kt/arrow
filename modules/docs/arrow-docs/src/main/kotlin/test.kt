import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.effects.*
import arrow.effects.deferredk.monad.flatMap
import arrow.effects.singlek.async.async
import arrow.effects.singlek.concurrent.concurrent
import arrow.effects.singlek.concurrent.startF
import arrow.instances.eq
import arrow.test.generators.genEither
import arrow.test.generators.genThrowable
import arrow.test.laws.ConcurrentLaws.racePairCanJoinLeft
import arrow.test.laws.ConcurrentLaws.racePairCancelsBoth
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

fun <T> EQ(): Eq<SingleKOf<T>> = object : Eq<SingleKOf<T>> {
  override fun SingleKOf<T>.eqv(b: SingleKOf<T>): Boolean =
    try {
      this.unsafeRunSync() == b.unsafeRunSync()
    } catch (throwable: Throwable) {
      val errA = try {
        this.unsafeRunSync()
        throw IllegalArgumentException()
      } catch (err: Throwable) {
        err
      }
      val errB = try {
        throw IllegalStateException()
      } catch (err: Throwable) {
        err
      }
      errA == errB
    }
}

fun main(args: Array<String>) = runBlocking {

  DeferredK { delay(1000) }.flatMap { DeferredK.raiseError<Int>(RuntimeException("Boom")) }.startF(Dispatchers.Default).flatMap { (joinA, cancelA) ->
    DeferredK { println("test") }.startF(Dispatchers.Default).flatMap { (joinB, cancelB) ->
      cancelA.flatMap {
        joinB
      }
//      joinA.flatMap { joinB }
    }
  }.unsafeRunSync()

//  SingleK.concurrent().run {
//    val ctx = Dispatchers.Default
//
//    forAll(Gen.int(), Gen.int()) { a, b ->
//      binding {
//        println("a: $a, b: $b")
//        val s = Semaphore.uncancelable(0L, this@run).bind()
//        val pa = Promise.uncancelable<ForSingleK, Int>(this@run).bind()
//        val loserA: Kind<ForSingleK, Int> = s.release().bracket(use = { never<Int>() }, release = { pa.complete(a) })
//        val pb = Promise.uncancelable<ForSingleK, Int>(this@run).bind()
//        val loserB: Kind<ForSingleK, Int> = s.release().bracket(use = { never<Int>() }, release = { pb.complete(b) })
//        val race = racePair(ctx, loserA, loserB).startF(ctx).bind()
//        s.acquireN(2L).flatMap { race.cancel() }.bind()
//        pa.get.bind() + pb.get.bind()
//      }.equalUnderTheLaw(just(a + b), EQ())
//    }
//  }


  Single.ambArray(
    Single.timer(3, TimeUnit.SECONDS).map { Left(5) }.doOnDispose { println("Disposed A") },
    Single.timer(1, TimeUnit.SECONDS).map { Right("Hello") }
  ).k().unsafeRunSync()
    .let(::println)

  SingleK.racePair2(
    Dispatchers.Default,
    Single.timer(3, TimeUnit.SECONDS).map { "Later hello" }.k(),
    Single.timer(1, TimeUnit.SECONDS).map { "Hello" }.k()
  ).flatMap {
    it.fold(
      { throw RuntimeException("Boom!") },
      { (fiber, result) -> println("result: $result"); fiber.join() }
    )
  }.unsafeRunSync()
    .let(::println)

//  SingleK.racePair2(
//    Dispatchers.Default,
//    Single.timer(3, TimeUnit.SECONDS).doOnDispose { println("A was disposed") }.k(),
//    Single.just(1).doOnDispose { println("B was disposed") }.doOnError { println("B received $it") }.k()
//  ).runAsyncCancellable { SingleK { println(it) } }
//    .unsafeRunSync()
//    .invoke()

  delay(5000)
}
