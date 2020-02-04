package arrow.fx

import arrow.test.UnitSpec
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import io.kotlintest.Spec

abstract class ReaktiveSpec : UnitSpec() {

  override fun beforeSpec(spec: Spec) {
    super.beforeSpec(spec)
    reaktiveUncaughtErrorHandler = {}
  }

  override fun afterSpec(spec: Spec) {
    super.afterSpec(spec)
    resetReaktiveUncaughtErrorHandler()
  }
}
