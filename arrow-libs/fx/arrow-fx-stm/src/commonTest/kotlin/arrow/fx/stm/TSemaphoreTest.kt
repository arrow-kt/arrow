package arrow.fx.stm

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class TSemaphoreTest : StringSpec({
    "creating a semaphore with a negative number of permits fails" {
      shouldThrow<IllegalArgumentException> { TSemaphore.new(-1) }
      shouldThrow<IllegalArgumentException> { atomically { newTSem(-1) } }
    }
    "available reads the correct amount" {
      val ts = TSemaphore.new(10)
      atomically { ts.available() } shouldBeExactly 10
    }
    "acquire removes one permit" {
      val ts = TSemaphore.new(8)
      atomically { ts.acquire(); ts.available() } shouldBeExactly 7
    }
    "acquire retries if no permits are available" {
      val ts = TSemaphore.new(0)
      atomically {
        stm { ts.acquire().let { true } } orElse { false }
      } shouldBe false
    }
    "acquire(n) should take n permits" {
      val ts = TSemaphore.new(10)
      atomically {
        ts.acquire(5); ts.available()
      } shouldBeExactly 5
    }
    "acquire(n) should retry if not enough permits are available" {
      val ts = TSemaphore.new(10)
      atomically {
        stm { ts.acquire(100).let { true } } orElse { false }
      } shouldBe false
    }
    "tryAcquire should behave like acquire if enough permits are available" {
      val ts = TSemaphore.new(11)
      val ts2 = TSemaphore.new(11)
      atomically { ts.tryAcquire() } shouldBe
        atomically { ts2.acquire().let { true } }
      atomically { ts.available() } shouldBeExactly
        atomically { ts2.available() }
    }
    "tryAcquire should not retry if not enough permits are available" {
      val ts = TSemaphore.new(0)
      atomically {
        ts.tryAcquire()
      } shouldBe false
    }
    "tryAcquire(n) should behave like acquire(n) if enough permits are available" {
      val ts = TSemaphore.new(11)
      val ts2 = TSemaphore.new(11)
      atomically { ts.tryAcquire(4) } shouldBe
        atomically { ts2.acquire(4).let { true } }
      atomically { ts.available() } shouldBeExactly
        atomically { ts2.available() }
    }
    "tryAcquire(n) should not retry if not enough permits are available" {
      val ts = TSemaphore.new(3)
      atomically {
        ts.tryAcquire(10)
      } shouldBe false
    }
    "release should add one permit" {
      val ts = TSemaphore.new(0)
      atomically { ts.release(); ts.available() } shouldBeExactly 1
    }
    "release(n) should throw if given a negative number" {
      val ts = TSemaphore.new(1)
      shouldThrow<IllegalArgumentException> { atomically { ts.release(-1) } }
    }
    "release(n) should add n permits" {
      val ts = TSemaphore.new(3)
      atomically { ts.release(6); ts.available() } shouldBe 9
    }
  }
)
