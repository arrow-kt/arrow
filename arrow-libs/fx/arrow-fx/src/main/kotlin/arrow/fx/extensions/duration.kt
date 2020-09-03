package arrow.fx.extensions

import arrow.core.Ordering
import arrow.extension
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup

@extension
interface DurationEq : Eq<Duration> {
  override fun Duration.eqv(b: Duration): Boolean = compareTo(b) == 0
}

@extension
interface DurationHash : Hash<Duration> {
  override fun Duration.hash(): Int = hashCode()
}

@extension
interface DurationOrder : Order<Duration> {
  override fun Duration.compare(b: Duration): Ordering =
    Ordering.fromInt(this.compareTo(b))
}

@extension
interface DurationSemigroup : Semigroup<Duration> {
  override fun Duration.combine(b: Duration): Duration = this + b
}

@extension
interface DurationMonoid : Monoid<Duration>, DurationSemigroup {
  override fun empty(): Duration = 0.seconds
}
