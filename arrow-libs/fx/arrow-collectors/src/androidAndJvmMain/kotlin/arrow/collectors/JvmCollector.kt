package arrow.collectors

/**
 * Wraps a [java.util.stream.Collector] to use with [collect].
 */
public fun <T, R> java.util.stream.Collector<T, *, R>.asCollector(): NonSuspendCollector<T, R> =
  Collectors.jvm(this)

/**
 * Wraps a [java.util.stream.Collector] to use with [collect].
 */
@Suppress("UnusedReceiverParameter")
public fun <T, R> Collectors.jvm(
  collector: java.util.stream.Collector<T, *, R>,
): NonSuspendCollector<T, R> = Collectors.jvmI(collector)

private typealias JavaCharacteristics = java.util.stream.Collector.Characteristics

@Suppress("UnusedReceiverParameter")
private fun <T, A, R> Collectors.jvmI(
  collector: java.util.stream.Collector<T, A, R>,
): NonSuspendCollector<T, R> = Collector.nonSuspendOf(
  supply = { collector.supplier().get() },
  accumulate = { current, value -> collector.accumulator().accept(current, value) },
  finish = { collector.finisher().apply(it) },
  characteristics =
  collector.characteristics().let { original ->
    setOfNotNull(
      Characteristics.CONCURRENT.takeIf { JavaCharacteristics.CONCURRENT in original },
      Characteristics.IDENTITY_FINISH.takeIf { JavaCharacteristics.IDENTITY_FINISH in original },
      Characteristics.UNORDERED.takeIf { JavaCharacteristics.UNORDERED in original },
    )
  }
)
