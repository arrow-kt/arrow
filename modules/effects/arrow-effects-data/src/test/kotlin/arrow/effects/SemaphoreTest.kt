package arrow.effects

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.toT
import arrow.data.extensions.list.traverse.traverse
import arrow.effects.extensions.fx.async.async
import arrow.effects.extensions.fx.concurrent.concurrent
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.suspended.fx.Fx
import arrow.effects.typeclasses.Concurrent
import arrow.test.UnitSpec
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class SemaphoreTest : UnitSpec() {

  init {

    fun <F> Concurrent<F>.tests(
      label: String,
      EQ: Eq<Kind<F, Boolean>>,
      semaphore: (Long) -> Kind<F, Semaphore<F>>
    ) {
      "$label - acquire n synchronously" {
        val n = 20L
        semaphore(n).flatMap { s ->
          (0 until n).toList().traverse(this@tests) { s.acquire() }.flatMap {
            s.available().flatMap { available ->
              delay { available == 0L }
            }
          }
        }.equalUnderTheLaw(just(true), EQ)
      }

      "$label - tryAcquire with available permits" {
        val n = 20
        semaphore(20).flatMap { s ->
          (0 until n).toList().traverse(this@tests) { s.acquire() }.flatMap {
            s.tryAcquire()
          }
        }.equalUnderTheLaw(just(true), EQ)
      }

      "$label - tryAcquire with no available permits" {
        val n = 20
        semaphore(n.toLong()).flatMap { s ->
          (0 until n).toList().traverse(this@tests) { s.acquire() }.flatMap {
            s.tryAcquire()
          }
        }.equalUnderTheLaw(just(false), EQ)
      }

      "$label - available with available permits" {
        semaphore(20).flatMap { s ->
          s.acquireN(19).flatMap {
            s.available().flatMap { available ->
              delay { available == 1L }
            }
          }
        }.equalUnderTheLaw(just(true), EQ)
      }

      "$label - available with no available permits" {
        semaphore(20).flatMap { s ->
          s.acquireN(20).flatMap {
            s.available().flatMap { available ->
              delay { available == 0L }
            }
          }
        }.equalUnderTheLaw(just(true), EQ)
      }

      "$label - tryAcquireN with no available permits" {
        semaphore(20).flatMap { s ->
          s.acquireN(20).flatMap {
            s.tryAcquireN(1)
          }
        }.equalUnderTheLaw(just(false), EQ)
      }

      "$label - count with available permits" {
        val n = 18
        semaphore(20).flatMap { s ->
          (0 until n).toList().traverse(this@tests) { s.acquire() }.flatMap {
            s.available().flatMap { available ->
              s.count().map { count -> available toT count }
            }
          }
        }.map { (available, count) -> available == count }
          .equalUnderTheLaw(just(true), EQ)
      }

      "$label - count with no available permits" {
        semaphore(20).flatMap { s ->
          s.acquireN(20).flatMap {
            s.count().flatMap { count ->
              delay { count == 0L }
            }
          }
        }.equalUnderTheLaw(just(true), EQ)
      }

      "$label - negative number of permits" {
        forAll(Gen.negativeIntegers().map(Int::toLong)) { i ->
          semaphore(i)
            .map { false }
            .handleError { true }
            .equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - withPermit" {
        forAll(Gen.positiveIntegers().map(Int::toLong)) { i ->
          semaphore(i).flatMap { s ->
            s.available().flatMap { current ->
              s.withPermit(defer {
                s.available().map { it == current - 1L }
              }).flatMap { didAcquire ->
                defer {
                  s.available().map { it == current && didAcquire }
                }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - offsetting acquires/releases - acquires parallel with releases" {
        val permits: List<Long> = listOf(1, 0, 20, 4, 0, 5, 2, 1, 1, 3)
        semaphore(0).flatMap { s ->
          Dispatchers.Default.parMapN(
            permits.traverse(this@tests) { s.acquireN(it) }.unit(),
            permits.reversed().traverse(this@tests) { s.releaseN(it) }.unit()
          ) { _, _ -> Unit }
            .flatMap {
              s.count()
            }
        }.map { count -> count.equalUnderTheLaw(0L, Long.eq()) }
          .equalUnderTheLaw(just(true), EQ)
      }
    }

    IO.concurrent().tests("IO - UncancelableSemaphore", IO_EQ()) { Semaphore.uncancelable(it, IO.async()) }
    IO.concurrent().tests("IO - CancelableSemaphore", IO_EQ()) { Semaphore(it, IO.concurrent()) }

    Fx.concurrent().tests("Fx - UncancelableSemaphore", EQ()) { Semaphore.uncancelable(it, Fx.async()) }
    Fx.concurrent().tests("Fx - CancelableSemaphore", EQ()) { Semaphore(it, Fx.concurrent()) }
  }
}
