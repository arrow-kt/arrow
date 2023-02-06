package arrow.core

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.property.PropertyTesting
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KotestConfig : AbstractProjectConfig() {
  override suspend fun beforeProject() {
    PropertyTesting.defaultIterationCount = 250
  }
}
