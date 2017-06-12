/**
 * Copyright [2016] [sksamuel]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

import java.util.concurrent.TimeUnit

data class Duration(val amount: Long, val timeUnit: TimeUnit) {
    val nanoseconds: Long by lazy { timeUnit.toNanos(amount) }

    companion object {
        // Actually limited to 9223372036854775807 days, so unless you are very patient, it is unlimited ;-)
        @JvmStatic val INFINITE = Duration(amount = Long.MAX_VALUE, timeUnit = TimeUnit.DAYS)
    }
}

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
