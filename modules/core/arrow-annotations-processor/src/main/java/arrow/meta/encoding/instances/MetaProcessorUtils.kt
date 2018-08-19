package arrow.meta.encoding.instances

import aballano.kotlinmemoization.memoize
import arrow.common.utils.ProcessorUtils

class EncoderUtils(val processorUtils: ProcessorUtils) : ProcessorUtils by processorUtils {
  val typeElementToMeta = ::getClassOrPackageDataWrapper.memoize()
}