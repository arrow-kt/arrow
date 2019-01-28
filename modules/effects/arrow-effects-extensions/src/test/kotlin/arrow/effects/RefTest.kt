package arrow.effects

import arrow.effects.extensions.io.monadDefer.monadDefer
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genGreaterEqual
import arrow.test.generators.genLessThan
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class RefTest : UnitSpec() {

  init {
    with(IO.monadDefer()) {
      val iom = this

      "set get" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          Ref.of(a, iom).flatMap { ref ->
            ref.set(b).flatMap {
              ref.get().map { get ->
                get == b
              }
            }
          }.unsafeRunSync()
        }
      }

      "getAndSet" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          Ref.of(a, iom).flatMap { ref ->
            ref.getAndSet(b).flatMap { old ->
              ref.get().map { new ->
                old == a && new == b
              }
            }
          }.unsafeRunSync()
        }
      }

      "consistent set update" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          val set = Ref.of(a, iom).flatMap { ref -> ref.set(b).flatMap { ref.get() } }
          val update = Ref.of(a, iom).flatMap { ref -> ref.update { b }.flatMap { ref.get() } }

          set.flatMap { setA ->
            update.map { updateA ->
              setA == updateA
            }
          }.unsafeRunSync()
        }
      }

      "access id" {
        forAll(Gen.int()) { a ->
          Ref.of(a, iom).flatMap { ref ->
            ref.access().map { (a, _) -> a }.flatMap {
              ref.get().map { get ->
                get == a
              }
            }
          }.unsafeRunSync()
        }
      }

      "consistent access tryModify" {
        forAll(Gen.int(), genFunctionAToB<Int, Int>(Gen.int())) { a, f ->
          val accessMap = Ref.of(a, iom).flatMap { ref -> ref.access().map { (a, setter) -> setter(f(a)) } }.flatten()
          val tryUpdate = Ref.of(a, iom).flatMap { ref -> ref.tryUpdate(f) }

          accessMap.fix().unsafeRunSync() == tryUpdate.unsafeRunSync()
        }
      }

      "access success" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          fx {
            val ref = Ref.of(a, this@with).bind()
            val (_, setter) = ref.access().bind()
            val success = setter(b).bind()
            val result = ref.get().bind()
            success && result == b
          }.fix().unsafeRunSync()
        }
      }

      "access failure" {
        forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
          fx {
            val ref = Ref.of(a, this@with).bind()
            val (_, setter) = ref.access().bind()
            ref.set(b).bind()
            val success = setter(c).bind()
            val result = ref.get().bind()
            !success && result == b
          }.fix().unsafeRunSync()
        }
      }

      "access fail multiple times" {
        forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
          fx {
            val ref = Ref.of(a, this@with).bind()
            val (_, setter) = ref.access().bind()
            val cond1 = setter(b).bind()
            ref.set(c).bind()
            val cond2 = setter(d).bind()
            val result = ref.get().bind()
            cond1 && !cond2 && result == c
          }.fix().unsafeRunSync()
        }
      }

      "tryUpdate" {
        forAll(Gen.int(), genFunctionAToB<Int, Int>(Gen.int())) { a, f ->
          Ref.of(a, iom).flatMap { ref ->
            ref.tryUpdate(f).flatMap {
              ref.get().map { newA ->
                newA == f(a)
              }
            }
          }.unsafeRunSync()
        }
      }

      "tryUpdate fail concurrent modification" {
        forAll(genGreaterEqual(0), genLessThan(0), genFunctionAToB<Int, Int>(Gen.int())) { a, b, f ->
          Ref.of(a, iom).flatMap { ref ->
            ref.tryUpdate {
              ref.set(b).fix().unsafeRunSync()
              f(it)
            }
          }.unsafeRunSync().not()
        }
      }

    }
  }
}