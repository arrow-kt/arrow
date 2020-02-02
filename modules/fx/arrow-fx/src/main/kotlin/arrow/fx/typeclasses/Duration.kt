package arrow.fx.typeclasses

import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@Deprecated("Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("kotlin.time.Duration")
)
data class Duration(val amount: Long, val timeUnit: TimeUnit) {

  @ExperimentalTime
  val duration: kotlin.time.Duration by lazy { amount.toDuration(timeUnit) }

  val nanoseconds: Long by lazy { timeUnit.toNanos(amount) }

  companion object {
    // Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
    @Deprecated(
      "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
      ReplaceWith("kotlin.time.Duration.INFINITE")
    )
    val INFINITE = Duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)
  }

  @Deprecated(
    "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
    ReplaceWith("this.amount.toDuration(this.timeUnit).times(i)", imports = arrayOf("kotlin.time.toDuration"))
  )
  operator fun times(i: Int) = Duration(amount * i, timeUnit)

  @Deprecated(
    "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
    ReplaceWith("plus", imports = arrayOf("kotlin.time.Duration"))
  )
  operator fun plus(d: Duration): Duration = run {
    val comp = timeUnit.compareTo(d.timeUnit)
    when {
      comp == 0 -> Duration(amount + d.amount, timeUnit) // Same unit
      comp < 0 -> this + Duration(timeUnit.convert(d.amount, d.timeUnit), timeUnit) // Convert to same unit then add
      else -> d + this // Swap this and d to add to the smaller unit
    }
  }
}

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("d.amount.toDuration(d.timeUnit).times(this)")
)
operator fun Int.times(d: Duration): Duration = d * this

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("days", imports = arrayOf("kotlin.time.days"))
)
val Long.days: Duration
  get() = Duration(this, TimeUnit.DAYS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("days", imports = arrayOf("kotlin.time.days"))
)
val Int.days: Duration
  get() = Duration(this.toLong(), TimeUnit.DAYS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("hours", imports = arrayOf("kotlin.time.hours"))
)
val Long.hours: Duration
  get() = Duration(this, TimeUnit.HOURS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("hours", imports = arrayOf("kotlin.time.hours"))
)
val Int.hours: Duration
  get() = Duration(this.toLong(), TimeUnit.HOURS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("microseconds", imports = arrayOf("kotlin.time.microseconds"))
)
val Long.microseconds: Duration
  get() = Duration(this, TimeUnit.MICROSECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("microseconds", imports = arrayOf("kotlin.time.microseconds"))
)
val Int.microseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MICROSECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("minutes", imports = arrayOf("kotlin.time.minutes"))
)
val Long.minutes: Duration
  get() = Duration(this, TimeUnit.MINUTES)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("minutes", imports = arrayOf("kotlin.time.minutes"))
)
val Int.minutes: Duration
  get() = Duration(this.toLong(), TimeUnit.MINUTES)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("milliseconds", imports = arrayOf("kotlin.time.milliseconds"))
)
val Long.milliseconds: Duration
  get() = Duration(this, TimeUnit.MILLISECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("milliseconds", imports = arrayOf("kotlin.time.milliseconds"))
)
val Int.milliseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MILLISECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("nanoseconds", imports = arrayOf("kotlin.time.nanoseconds"))
)
val Long.nanoseconds: Duration
  get() = Duration(this, TimeUnit.NANOSECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("nanoseconds", imports = arrayOf("kotlin.time.nanoseconds"))
)
val Int.nanoseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.NANOSECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("seconds", imports = arrayOf("kotlin.time.seconds"))
)
val Long.seconds: Duration
  get() = Duration(this, TimeUnit.SECONDS)

@Deprecated(
  "Duration will be removed after 0.10.5 in favor of kotlin.time.Duration to support MPP",
  ReplaceWith("seconds", imports = arrayOf("kotlin.time.seconds"))
)
val Int.seconds: Duration
  get() = Duration(this.toLong(), TimeUnit.SECONDS)
