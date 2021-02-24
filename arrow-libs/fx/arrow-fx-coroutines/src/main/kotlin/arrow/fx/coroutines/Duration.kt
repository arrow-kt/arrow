package arrow.fx.coroutines

import java.util.concurrent.TimeUnit

@Deprecated(DeprecateDuration, ReplaceWith("timeUnit.toNanos(amount).nanoseconds", "kotlin.time.nanoseconds"))
data class Duration(val amount: Long, val timeUnit: TimeUnit) {
  @Deprecated("Redundant property please use `inNanoseconds` in kotlin.time.Duration")
  val nanoseconds: Long by lazy { timeUnit.toNanos(amount) }

  @Deprecated("Redundant property please use `inNanoseconds` in kotlin.time.Duration")
  val millis: Long by lazy { timeUnit.toMillis(amount) }

  companion object {
    // Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
    @Deprecated(DeprecateDuration, ReplaceWith("kotlin.time.Duration.INFINITE", "kotlin.time.Duration"))
    val INFINITE: Duration = Duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)
  }

  @Deprecated(DeprecateDuration)
  operator fun times(i: Int): Duration = Duration(amount * i, timeUnit)

  @Deprecated(DeprecateDuration)
  operator fun plus(d: Duration): Duration = run {
    val comp = timeUnit.compareTo(d.timeUnit)
    when {
      comp == 0 -> Duration(amount + d.amount, timeUnit) // Same unit
      comp < 0 -> this + Duration(timeUnit.convert(d.amount, d.timeUnit), timeUnit) // Convert to same unit then add
      else -> d + this // Swap this and d to add to the smaller unit
    }
  }

  @Deprecated(DeprecateDuration)
  operator fun compareTo(d: Duration): Int = run {
    val comp = timeUnit.compareTo(d.timeUnit)
    when {
      comp == 0 -> amount.compareTo(d.amount)
      comp < 0 -> amount.compareTo(timeUnit.convert(d.amount, d.timeUnit))
      else -> -d.compareTo(this)
    }
  }
}

@Deprecated(DeprecateDuration)
operator fun Int.times(d: Duration) = d * this

@Deprecated(DeprecateDuration, ReplaceWith("days", "kotlin.time.days"))
val Long.days: Duration
  get() = Duration(this, TimeUnit.DAYS)

@Deprecated(DeprecateDuration, ReplaceWith("days", "kotlin.time.days"))
val Int.days: Duration
  get() = Duration(this.toLong(), TimeUnit.DAYS)

@Deprecated(DeprecateDuration, ReplaceWith("hours", "kotlin.time.hours"))
val Long.hours: Duration
  get() = Duration(this, TimeUnit.HOURS)

@Deprecated(DeprecateDuration, ReplaceWith("hours", "kotlin.time.hours"))
val Int.hours: Duration
  get() = Duration(this.toLong(), TimeUnit.HOURS)

@Deprecated(DeprecateDuration, ReplaceWith("microseconds", "kotlin.time.microseconds"))
val Long.microseconds: Duration
  get() = Duration(this, TimeUnit.MICROSECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("microseconds", "kotlin.time.microseconds"))
val Int.microseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MICROSECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("minutes", "kotlin.time.minutes"))
val Long.minutes: Duration
  get() = Duration(this, TimeUnit.MINUTES)

@Deprecated(DeprecateDuration, ReplaceWith("minutes", "kotlin.time.minutes"))
val Int.minutes: Duration
  get() = Duration(this.toLong(), TimeUnit.MINUTES)

@Deprecated(DeprecateDuration, ReplaceWith("milliseconds", "kotlin.time.milliseconds"))
val Long.milliseconds: Duration
  get() = Duration(this, TimeUnit.MILLISECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("milliseconds", "kotlin.time.milliseconds"))
val Int.milliseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MILLISECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("nanoseconds", "kotlin.time.nanoseconds"))
val Long.nanoseconds: Duration
  get() = Duration(this, TimeUnit.NANOSECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("nanoseconds", "kotlin.time.nanoseconds"))
val Int.nanoseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.NANOSECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("seconds", "kotlin.time.seconds"))
val Long.seconds: Duration
  get() = Duration(this, TimeUnit.SECONDS)

@Deprecated(DeprecateDuration, ReplaceWith("seconds", "kotlin.time.seconds"))
val Int.seconds: Duration
  get() = Duration(this.toLong(), TimeUnit.SECONDS)

internal const val DeprecateDuration: String =
  "arrow.fx.coroutines.Duration is deprecated and will be removed in 0.13.0 in favor of kotlin.time.Duration"
