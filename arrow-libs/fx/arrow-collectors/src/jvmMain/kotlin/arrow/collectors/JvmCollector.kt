package arrow.collectors

/**
 * Wraps a [java.util.stream.Collector] to use with [collect].
 */
public fun <T, R> java.util.stream.Collector<T, *, R>.asCollector(): Collector<T, R> =
  Collectors.jvm(this)

/**
 * Wraps a [java.util.stream.Collector] to use with [collect].
 */
@Suppress("UnusedReceiverParameter")
public fun <T, R> Collectors.jvm(
  collector: java.util.stream.Collector<T, *, R>,
): Collector<T, R> = Collectors.jvmI(collector)

@Suppress("UnusedReceiverParameter")
private fun <T, A, R> Collectors.jvmI(
  collector: java.util.stream.Collector<T, A, R>,
): Collector<T, R> = Collector.of(
  supply = { collector.supplier().get() },
  accumulate = { current, value -> collector.accumulator().accept(current, value) },
  finish = { collector.finisher().apply(it) },
  characteristics =
    collector.characteristics().let { original ->
      setOfNotNull(
        Characteristics.CONCURRENT.takeIf { java.util.stream.Collector.Characteristics.CONCURRENT in original },
        Characteristics.IDENTITY_FINISH.takeIf { java.util.stream.Collector.Characteristics.IDENTITY_FINISH in original },
        Characteristics.UNORDERED.takeIf { java.util.stream.Collector.Characteristics.UNORDERED in original },
      )
    }
)
