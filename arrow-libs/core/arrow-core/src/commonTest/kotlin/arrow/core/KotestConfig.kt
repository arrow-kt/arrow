package arrow.core

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.property.PropertyTesting
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KotestConfig : AbstractProjectConfig() {
  override suspend fun beforeProject() {
    PropertyTesting.defaultIterationCount = 250
  }

  override val timeout: Duration = 30.seconds
  override val invocationTimeout: Long = 30.seconds.inWholeMilliseconds
}
