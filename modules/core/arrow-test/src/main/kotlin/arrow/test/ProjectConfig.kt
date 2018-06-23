package arrow.test

import io.kotlintest.AbstractProjectConfig

/**
 * Base class for unit tests
 */
abstract class ProjectConfig : AbstractProjectConfig() {
  override fun parallelism(): Int = 2
}