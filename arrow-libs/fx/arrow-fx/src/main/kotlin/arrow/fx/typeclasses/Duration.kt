package arrow.fx.typeclasses

import arrow.fx.IODeprecation
import java.util.concurrent.TimeUnit

@Deprecated(IODeprecation)
data class Duration(val amount: Long, val timeUnit: TimeUnit) {
  val nanoseconds: Long by lazy { timeUnit.toNanos(amount) }

  @Deprecated(IODeprecation)
  companion object {
    // Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
    val INFINITE = Duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)
  }

  operator fun times(i: Int) = Duration(amount * i, timeUnit)

  operator fun plus(d: Duration): Duration = run {
    val comp = timeUnit.compareTo(d.timeUnit)
    when {
      comp == 0 -> Duration(amount + d.amount, timeUnit) // Same unit
      comp < 0 -> this + Duration(timeUnit.convert(d.amount, d.timeUnit), timeUnit) // Convert to same unit then add
      else -> d + this // Swap this and d to add to the smaller unit
    }
  }

  operator fun compareTo(d: Duration): Int = run {
    val comp = timeUnit.compareTo(d.timeUnit)
    when {
      comp == 0 -> amount.compareTo(d.amount)
      comp < 0 -> amount.compareTo(timeUnit.convert(d.amount, d.timeUnit))
      else -> -d.compareTo(this)
    }
  }
}

@Deprecated(IODeprecation)
operator fun Int.times(d: Duration) = d * this

@Deprecated(IODeprecation)
val Long.days: Duration
  get() = Duration(this, TimeUnit.DAYS)

@Deprecated(IODeprecation)
val Int.days: Duration
  get() = Duration(this.toLong(), TimeUnit.DAYS)

@Deprecated(IODeprecation)
val Long.hours: Duration
  get() = Duration(this, TimeUnit.HOURS)

@Deprecated(IODeprecation)
val Int.hours: Duration
  get() = Duration(this.toLong(), TimeUnit.HOURS)

@Deprecated(IODeprecation)
val Long.microseconds: Duration
  get() = Duration(this, TimeUnit.MICROSECONDS)

@Deprecated(IODeprecation)
val Int.microseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MICROSECONDS)

@Deprecated(IODeprecation)
val Long.minutes: Duration
  get() = Duration(this, TimeUnit.MINUTES)

@Deprecated(IODeprecation)
val Int.minutes: Duration
  get() = Duration(this.toLong(), TimeUnit.MINUTES)

@Deprecated(IODeprecation)
val Long.milliseconds: Duration
  get() = Duration(this, TimeUnit.MILLISECONDS)

@Deprecated(IODeprecation)
val Int.milliseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MILLISECONDS)

@Deprecated(IODeprecation)
val Long.nanoseconds: Duration
  get() = Duration(this, TimeUnit.NANOSECONDS)

@Deprecated(IODeprecation)
val Int.nanoseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.NANOSECONDS)

@Deprecated(IODeprecation)
val Long.seconds: Duration
  get() = Duration(this, TimeUnit.SECONDS)

@Deprecated(IODeprecation)
val Int.seconds: Duration
  get() = Duration(this.toLong(), TimeUnit.SECONDS)
