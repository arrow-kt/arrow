import arrow.Kind
import arrow.core.NonFatal
import arrow.effects.extensions.fx.bracket.bracket
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.extensions.runNonBlockingCancellable
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.ExitCase
import arrow.test.generators.applicativeError
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicReference


fun main() {

//  fa.bracketCase(release = { _, _ -> just<Unit>(Unit) }, use = { just(it) }).equalUnderTheLaw(fa.uncancelable().flatMap { just(it) }, EQ)

//  Fx.just(1)
//    .bracketCase(release = { _, _ -> Fx.just(Unit) }, use = { Fx.just(it) })
//    .equalUnderTheLaw(Fx.just(1).uncancelable().flatMap { Fx.just(it) }, FX_EQ())

  Fx.just(1)
    .unsafeRunBlocking()
    .let(::println)

//  Fx.FlatMap(
////    Fx.async<Int> { cb ->
////      throw RuntimeException("Hello World!")
////    },
////    FxFrame.any(),
////    0
////  ).unsafeRunBlocking()
////    .let(::println)
////
////  Fx.FlatMap(
////    FxBracket<Int, Int>(
////      Fx.Pure(1),
////      { i, _ -> Fx.Pure(Unit) },
////      { throw Throwable("Boom!") }
////    ),
////    FxFrame.any(),
////    0
////  ).unsafeRunBlocking()
////    .let(::println)

  forAll(Gen.int()) { i ->
    val msg: AtomicReference<Int> = AtomicReference(0)
    Fx.bracket().run {
      val lh = just(i).bracket<Int, Int>(
        release = { ii -> unit().map { msg.set(ii) } },
        use = { throw Throwable("Boom!") }
      )
        .attempt()
        .map { msg.get() }

      lh.equalUnderTheLaw(Fx.just(i), FX_EQ())
    }
  }

}

fun <A> FX_EQ(): Eq<FxOf<A>> = Eq { a, b ->
  unsafe {
    runBlocking {
      Fx {
        try {
          !a == !b
        } catch (e: Throwable) {
          val errA = try {
            !a
            throw IllegalArgumentException()
          } catch (err: Throwable) {
            err
          }
          val errB = try {
            !b
            throw IllegalStateException()
          } catch (err: Throwable) {
            err
          }
          println("Found errors: $errA and $errB")
          errA == errB
        }
      }
    }
  }
}
