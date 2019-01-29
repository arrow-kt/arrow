package arrow.effects

import arrow.data.extensions.list.traverse.sequence
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.monad.binding
import arrow.effects.extensions.io.monad.flatMap
import arrow.effects.extensions.io.monad.flatten
import arrow.effects.extensions.io.monad.map
import arrow.effects.extensions.io.monadDefer.monadDefer
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class RefTest : UnitSpec() {

  init {

    "concurrent modifications" {
      val finalValue = 100
      val r = Ref.unsafe(0, IO.monadDefer())
      (0 until finalValue)
        .map { _ -> IO.unit.continueOn(Dispatchers.Default).flatMap { _ -> r.update { it + 1 } } }
        .sequence(IO.applicative())
        .flatMap { r.get() }
        .fix()
        .unsafeRunSync() shouldBe finalValue
    }

    "set get - successful" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        Ref.of(a, IO.monadDefer()).flatMap { ref ->
          ref.set(b).flatMap {
            ref.get()
          }
        }.equalUnderTheLaw(IO.just(b), EQ())
      }
    }

    "getAndSet - successful" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        Ref.of(a, IO.monadDefer()).flatMap { ref ->
          ref.getAndSet(b).flatMap { old ->
            ref.get().map { new ->
              old == a && new == b
            }
          }
        }.equalUnderTheLaw(IO.just(true), EQ())
      }
    }

    "access - successful" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        binding {
          val ref = Ref.of(a, IO.monadDefer()).bind()
          val (_, setter) = ref.access().bind()
          val success = setter(b).bind()
          val result = ref.get().bind()
          success && result == b
        }.equalUnderTheLaw(IO.just(true), EQ())
      }
    }

    "access - setter should fail if value is modified before setter is called" {
      forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
        binding {
          val ref = Ref.of(a, IO.monadDefer()).bind()
          val (_, setter) = ref.access().bind()
          ref.set(b).bind()
          val success = setter(c).bind()
          val result = ref.get().bind()
          !success && result == b
        }.equalUnderTheLaw(IO.just(true), EQ())
      }
    }

    "access - setter should fail if called twice" {
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        binding {
          val ref = Ref.of(a, IO.monadDefer()).bind()
          val (_, setter) = ref.access().bind()
          val cond1 = setter(b).bind()
          ref.set(c).bind()
          val cond2 = setter(d).bind()
          val result = ref.get().bind()
          cond1 && !cond2 && result == c
        }.equalUnderTheLaw(IO.just(true), EQ())
      }
    }

    "tryUpdate - modification occurs successfully" {
      forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
        Ref.of(a, IO.monadDefer()).flatMap { ref ->
          ref.tryUpdate(f).flatMap {
            ref.get()
          }
        }.equalUnderTheLaw(IO.just(f(a)), EQ())
      }
    }

    "tryUpdate - should fail to update if modification has occurred" {
      forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
        Ref.of(a, IO.monadDefer()).flatMap { ref ->
          ref.tryUpdate {
            ref.update(Int::inc).fix().unsafeRunSync()
            f(it)
          }
        }.equalUnderTheLaw(IO.just(false), EQ())
      }
    }

    "consistent set update" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        val set = Ref.of(a, IO.monadDefer()).flatMap { ref -> ref.set(b).flatMap { _ -> ref.get() } }
        val update = Ref.of(a, IO.monadDefer()).flatMap { ref -> ref.update { _ -> b }.flatMap { _ -> ref.get() } }

        set.flatMap { setA ->
          update.map { updateA ->
            setA == updateA
          }
        }.equalUnderTheLaw(IO.just(true), EQ())
      }
    }

    "access id" {
      forAll(Gen.int()) { a ->
        Ref.of(a, IO.monadDefer()).flatMap { ref ->
          ref.access().map { (a, _) -> a }.flatMap { _ ->
            ref.get()
          }
        }.equalUnderTheLaw(IO.just(a), EQ())
      }
    }

    "consistent access tryModify" {
      forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
        val accessMap = Ref.of(a, IO.monadDefer()).flatMap { ref -> ref.access().map { (a, setter) -> setter(f(a)) } }.flatten()
        val tryUpdate = Ref.of(a, IO.monadDefer()).flatMap { ref -> ref.tryUpdate(f) }

        accessMap.equalUnderTheLaw(tryUpdate, EQ())
      }
    }

  }
}