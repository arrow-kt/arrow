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
): Collector<T, R> = JvmCollector(collector)

@JvmInline
private value class JvmCollector<A, T, R>(
  private val collector: java.util.stream.Collector<T, A, R>,
) : CollectorI<A, T, R> {
  override val characteristics: Set<Characteristics>
    get() {
      val original = collector.characteristics()
      return setOfNotNull(
        Characteristics.CONCURRENT.takeIf { java.util.stream.Collector.Characteristics.CONCURRENT in original },
        Characteristics.IDENTITY_FINISH.takeIf { java.util.stream.Collector.Characteristics.IDENTITY_FINISH in original },
        Characteristics.UNORDERED.takeIf { java.util.stream.Collector.Characteristics.UNORDERED in original },
      )
    }

  override suspend fun supply(): A = collector.supplier().get()
  override suspend fun accumulate(current: A, value: T) {
    collector.accumulator().accept(current, value)
  }

  override suspend fun finish(current: A): R = collector.finisher().apply(current)
}
