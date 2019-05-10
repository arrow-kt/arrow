package arrow.effects

import arrow.Kind
import arrow.data.extensions.list.traverse.sequence
import arrow.effects.extensions.fx.concurrent.concurrent
import arrow.effects.extensions.fx.fx.fx
import arrow.effects.extensions.fx.monad.flatMap
import arrow.effects.extensions.fx.monad.flatten
import arrow.effects.extensions.fx.monad.map
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.extensions.io.fx.fx
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.fix
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class RefTest : UnitSpec() {

  init {

    fun <F> arrow.effects.typeclasses.suspended.concurrent.Fx<F>.tests(
      label: String,
      EQ: Eq<Kind<F, Boolean>>,
      updateRefUnsafely: (Ref<F, Int>) -> Unit
    ) = concurrent().run {
      val ctx = dispatchers().default()

      "$label - concurrent modifications" {
        val finalValue = 100
        val r = Ref.unsafe(0, this@run)
        (0 until finalValue)
          .map { _ -> unit().continueOn(ctx).flatMap { _ -> r.update { it + 1 } } }
          .sequence(this@run)
          .flatMap { r.get() }
          .flatMap { res -> delay { res == finalValue } }
          .equalUnderTheLaw(just(true), EQ)
      }

      "$label - set get - successful" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          Ref.of(a, this@run).flatMap { ref ->
            ref.set(b).flatMap {
              ref.get().map { it == b }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - getAndSet - successful" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          Ref.of(a, this@run).flatMap { ref ->
            ref.getAndSet(b).flatMap { old ->
              ref.get().map { new ->
                old == a && new == b
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - access - successful" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val ref = !Ref.of(a, this@run)
            val (_, setter) = !ref.access()
            val success = !setter(b)
            val result = !ref.get()
            success && result == b
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - access - setter should fail if value is modified before setter is called" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          fx {
            val ref = !Ref.of(a, this@run)
            val (_, setter) = !ref.access()
            !ref.set(b)
            val success = !setter(c)
            val result = !ref.get()
            !success && result == b
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - access - setter should fail if called twice" {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
          fx {
            val ref = !Ref.of(a, this@run)
            val (_, setter) = !ref.access()
            val cond1 = !setter(b)
            !ref.set(c)
            val cond2 = !setter(d)
            val result = !ref.get()
            cond1 && !cond2 && result == c
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - tryUpdate - modification occurs successfully" {
        forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
          Ref.of(a, this@run).flatMap { ref ->
            ref.tryUpdate(f).flatMap {
              ref.get().map { res ->
                res == f(a)
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - tryUpdate - should fail to update if modification has occurred" {
        forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
          Ref.of(a, this@run).flatMap { ref ->
            ref.tryUpdate {
              updateRefUnsafely(ref)
              f(it)
            }
          }.equalUnderTheLaw(just(false), EQ)
        }
      }

      "$label - consistent set update" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          val set = Ref.of(a, this@run).flatMap { ref -> ref.set(b).flatMap { ref.get() } }
          val update = Ref.of(a, this@run).flatMap { ref -> ref.update { b }.flatMap { ref.get() } }

          set.flatMap { setA ->
            update.map { updateA ->
              setA == updateA
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - access id" {
        forAll(Gen.int()) { a ->
          Ref.of(a, this@run).flatMap { ref ->
            ref.access().map { (a, _) -> a }.flatMap {
              ref.get().map { res ->
                res == a
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - consistent access tryModify" {
        forAll(Gen.int(), Gen.functionAToB<Int, Int>(Gen.int())) { a, f ->
          val accessMap = Ref.of(a, this@run).flatMap { ref -> ref.access().map { (a, setter) -> setter(f(a)) } }.flatten()
          val tryUpdate = Ref.of(a, this@run).flatMap { ref -> ref.tryUpdate(f) }
          accessMap.flatMap { res ->
            tryUpdate.map {
              res == it
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }
    }

    IO.fx().tests("IO", IO_EQ()) { it.update(Int::inc).fix().unsafeRunSync() }
    Fx.fx().tests("Fx", EQ()) { Fx.unsafeRunBlocking(it.update(Int::inc)) }
  }
}
