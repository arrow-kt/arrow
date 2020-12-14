package arrow.fx

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.test.generators.functionAToB
import arrow.core.test.laws.equalUnderTheLaw
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.test.eq.eqK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class RefTest : ArrowFxSpec() {

  init {
    fun <F> MonadDefer<F>.tests(EQF: EqK<F>, RF: RefFactory<F>): Unit {
      val eq: Eq<Kind<F, Unit>> = EQF.liftEq(Eq.any())
      fun Kind<F, Unit>.test(): Boolean = equalUnderTheLaw(unit(), eq)
      fun Kind<F, Unit>.unsafeRunSync(): Boolean = test()

      "set get - successful" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          RF.just(a).flatMap { ref ->
            ref.set(b).flatMap {
              ref.get().map { it shouldBe b }
            }
          }.test()
        }
      }

      "getAndSet - successful" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx.monad {
            val ref = RF.just(a).invoke()
            val old = ref.getAndSet(b).invoke()
            val new = ref.get().invoke()
            old shouldBe a
            new shouldBe b
          }.test()
        }
      }

      "access - successful" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx.monad {
            val ref = RF.just(a).invoke()
            val (_, setter) = ref.access().invoke()
            val success = setter(b).invoke()
            val result = ref.get().invoke()
            success shouldBe true
            result shouldBe b
          }.test()
        }
      }

      "access - setter should fail if value is modified before setter is called" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          fx.monad {
            val ref = RF.just(a).invoke()
            val (_, setter) = ref.access().invoke()
            ref.set(b).invoke()
            val success = setter(c).invoke()
            val result = ref.get().invoke()
            success shouldBe false
            result shouldBe b
          }.test()
        }
      }

      "access - setter should fail if called twice" {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
          fx.monad {
            val ref = RF.just(a).invoke()
            val (_, setter) = ref.access().invoke()
            val cond1 = setter(b).invoke()
            ref.set(c).invoke()
            val cond2 = setter(d).invoke()
            val result = ref.get().invoke()
            cond1 shouldBe true
            cond2 shouldBe false
            result shouldBe c
          }.test()
        }
      }

      "tryUpdate - modification occurs successfully" {
        forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
          fx.monad {
            val ref = RF.just(a).invoke()
            ref.tryUpdate(f).invoke()
            val res = ref.get().invoke()
            res shouldBe f(a)
          }.test()
        }
      }

      "tryUpdate - should fail to update if modification has occurred" {
        forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
          RF.just(a).flatMap { ref ->
              ref.tryUpdate {
                ref.update(Int::inc).unsafeRunSync()
                f(it)
              }
            }
            .map { it shouldBe false }
            .test()
        }
      }

      "consistent set update" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          val set = RF.just(a).flatMap { ref -> ref.set(b).flatMap { ref.get() } }
          val update = RF.just(a).flatMap { ref -> ref.update { b }.flatMap { ref.get() } }

          set.flatMap { setA ->
            update.map { updateA ->
              setA shouldBe updateA
            }
          }.test()
        }
      }

      "access id" {
        forAll(Gen.int()) { a ->
          RF.just(a).flatMap { ref ->
            ref.access().map { (a, _) -> a }.flatMap {
              ref.get().map { it shouldBe a }
            }
          }.test()
        }
      }

      "consistent access tryUpdate" {
        forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
          val accessMap = RF.just(a).flatMap { ref -> ref.access().map { (a, setter) -> setter(f(a)) } }.flatten()
          val tryUpdate = RF.just(a).flatMap { ref -> ref.tryUpdate(f) }

          mapN(accessMap, tryUpdate) { (a, b) -> a shouldBe b }.test()
        }
      }
    }

    fun <F> Concurrent<F>.concurrentTests(EQF: EqK<F>, RF: RefFactory<F>): Unit {
      "concurrent modifications" {
        val finalValue = 1000
        RF.just(0).flatMap { r ->
          (0 until finalValue)
            .parTraverse { r.update { it + 1 } }
            .flatMap { r.get() }
        }.equalUnderTheLaw(just(finalValue), EQF.liftEq(Int.eq()))
      }
    }

    IO.concurrent().tests(IO.eqK(), Ref.factory(IO.monadDefer()))
    IO.concurrent().concurrentTests(IO.eqK(), Ref.factory(IO.monadDefer()))
  }
}
