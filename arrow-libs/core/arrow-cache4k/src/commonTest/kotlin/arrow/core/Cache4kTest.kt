package arrow.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration

class Cache4kTest {

  @Test fun cacheShouldReturnSavedValueByKeyCorrectly() = runTest {
    val cache = Cache4kMemoizationCache<String, Int>(buildCache4K { expireAfterAccess(Duration.INFINITE) })
    val expectedKey = "user:age:userId:5"
    val expectedValue = 32

    cache.set(expectedKey, expectedValue)
    cache.get(expectedKey) shouldBe expectedValue
  }

  @Test fun cacheShouldReturnNullCauseTryGetByUnsavedKey() = runTest {
    val cache = Cache4kMemoizationCache<String, Int>(buildCache4K { expireAfterAccess(Duration.INFINITE) })
    val expectedKey = "user:name:userId:5"

    cache.get(expectedKey) shouldBe null
  }
}
