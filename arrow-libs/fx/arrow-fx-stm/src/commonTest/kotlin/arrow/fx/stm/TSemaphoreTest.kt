package arrow.fx.stm

import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TSemaphoreTest {

  @Test fun creatingASemaphoreWithANegativeNumberOfPermitsFails() = runTest {
    shouldThrow<IllegalArgumentException> { TSemaphore.new(-1) }
    shouldThrow<IllegalArgumentException> { atomically { newTSem(-1) } }
  }

  @Test fun availableReadsTheCorrectAmount() = runTest {
    val ts = TSemaphore.new(10)
    atomically { ts.available() } shouldBeExactly 10
  }

  @Test fun acquireRemovesOnePermit() = runTest {
    val ts = TSemaphore.new(8)
    atomically { ts.acquire(); ts.available() } shouldBeExactly 7
  }

  @Test fun acquireRetriesIfNoPermitsAreAvailable() = runTest {
    val ts = TSemaphore.new(0)
    atomically {
      stm { ts.acquire().let { true } } orElse { false }
    } shouldBe false
  }

  @Test fun acquireNShouldTakeNPermits() = runTest {
    val ts = TSemaphore.new(10)
    atomically {
      ts.acquire(5); ts.available()
    } shouldBeExactly 5
  }

  @Test fun acquireNShouldRetryIfNotEnoughPermitsAreAvailable() = runTest {
    val ts = TSemaphore.new(10)
    atomically {
      stm { ts.acquire(100).let { true } } orElse { false }
    } shouldBe false
  }

  @Test fun tryAcquireShouldBehaveLikeAcquireIfEnoughPermitsAreAvailable() = runTest {
    val ts = TSemaphore.new(11)
    val ts2 = TSemaphore.new(11)
    atomically { ts.tryAcquire() } shouldBe
      atomically { ts2.acquire().let { true } }
    atomically { ts.available() } shouldBeExactly
      atomically { ts2.available() }
  }

  @Test fun tryAcquireShouldNotRetryIfNotEnoughPermitsAreAvailable() = runTest {
    val ts = TSemaphore.new(0)
    atomically {
      ts.tryAcquire()
    } shouldBe false
  }

  @Test fun tryAcquireNShouldBehaveLikeAcquireNIfEnoughPermitsAreAvailable() = runTest {
    val ts = TSemaphore.new(11)
    val ts2 = TSemaphore.new(11)
    atomically { ts.tryAcquire(4) } shouldBe
      atomically { ts2.acquire(4).let { true } }
    atomically { ts.available() } shouldBeExactly
      atomically { ts2.available() }
  }

  @Test fun tryAcquireNShouldNotRetryIfNotEnoughPermitsAreAvailable() = runTest {
    val ts = TSemaphore.new(3)
    atomically {
      ts.tryAcquire(10)
    } shouldBe false
  }

  @Test fun releaseShouldAddOnePermit() = runTest {
    val ts = TSemaphore.new(0)
    atomically { ts.release(); ts.available() } shouldBeExactly 1
  }

  @Test fun releaseNShouldThrowIfGivenANegativeNumber() = runTest {
    val ts = TSemaphore.new(1)
    shouldThrow<IllegalArgumentException> { atomically { ts.release(-1) } }
  }

  @Test fun releaseNShouldAddNPermits() = runTest {
    val ts = TSemaphore.new(3)
    atomically { ts.release(6); ts.available() } shouldBe 9
  }
}
