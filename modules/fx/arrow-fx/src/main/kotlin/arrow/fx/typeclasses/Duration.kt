package arrow.fx.typeclasses

import java.util.concurrent.TimeUnit

data class Duration(val amount: Long, val timeUnit: TimeUnit) {
  val nanoseconds: Long by lazy { timeUnit.toNanos(amount) }

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
}

operator fun Int.times(d: Duration) = d * this

val Long.days: Duration
  get() = Duration(this, TimeUnit.DAYS)

val Int.days: Duration
  get() = Duration(this.toLong(), TimeUnit.DAYS)

val Long.hours: Duration
  get() = Duration(this, TimeUnit.HOURS)

val Int.hours: Duration
  get() = Duration(this.toLong(), TimeUnit.HOURS)

val Long.microseconds: Duration
  get() = Duration(this, TimeUnit.MICROSECONDS)

val Int.microseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MICROSECONDS)

val Long.minutes: Duration
  get() = Duration(this, TimeUnit.MINUTES)

val Int.minutes: Duration
  get() = Duration(this.toLong(), TimeUnit.MINUTES)

val Long.milliseconds: Duration
  get() = Duration(this, TimeUnit.MILLISECONDS)

val Int.milliseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.MILLISECONDS)

val Long.nanoseconds: Duration
  get() = Duration(this, TimeUnit.NANOSECONDS)

val Int.nanoseconds: Duration
  get() = Duration(this.toLong(), TimeUnit.NANOSECONDS)

val Long.seconds: Duration
  get() = Duration(this, TimeUnit.SECONDS)

val Int.seconds: Duration
  get() = Duration(this.toLong(), TimeUnit.SECONDS)
