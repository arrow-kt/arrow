package arrow.dagger.extensions

import arrow.core.extensions.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides

@Module
class NumberInstances {

  @Provides
  fun byteSemigroup(): Semigroup<Byte> = Byte.semigroup()

  @Provides
  fun byteMonoid(): Monoid<Byte> = Byte.monoid()

  @Provides
  fun byteOrder(): Order<Byte> = Byte.order()

  @Provides
  fun byteEq(): Eq<Byte> = Byte.eq()

  @Provides
  fun byteHash(): Hash<Byte> = Byte.hash()

  @Provides
  fun doubleSemigroup(): Semigroup<Double> = Double.semigroup()

  @Provides
  fun doubleMonoid(): Monoid<Double> = Double.monoid()

  @Provides
  fun doubleOrder(): Order<Double> = Double.order()

  @Provides
  fun doubleEq(): Eq<@JvmSuppressWildcards Double> = Double.eq()

  @Provides
  fun doubleHash(): Hash<Double> = Double.hash()

  @Provides
  fun intSemigroup(): Semigroup<Int> = Int.semigroup()

  @Provides
  fun intMonoid(): Monoid<Int> = Int.monoid()

  @Provides
  fun intOrder(): Order<Int> = Int.order()

  @Provides
  fun intEq(): Eq<@JvmSuppressWildcards Int> = Int.eq()

  @Provides
  fun intHash(): Hash<Int> = Int.hash()

  @Provides
  fun longSemigroup(): Semigroup<Long> = Long.semigroup()

  @Provides
  fun longMonoid(): Monoid<Long> = Long.monoid()

  @Provides
  fun longOrder(): Order<Long> = Long.order()

  @Provides
  fun longEq(): Eq<@JvmSuppressWildcards Long> = Long.eq()

  @Provides
  fun longHash(): Hash<Long> = Long.hash()

  @Provides
  fun shortSemigroup(): Semigroup<Short> = Short.semigroup()

  @Provides
  fun shortMonoid(): Monoid<Short> = Short.monoid()

  @Provides
  fun shortOrder(): Order<Short> = Short.order()

  @Provides
  fun shortEq(): Eq<@JvmSuppressWildcards Short> = Short.eq()

  @Provides
  fun shortHash(): Hash<Short> = Short.hash()

  @Provides
  fun floatSemigroup(): Semigroup<Float> = Float.semigroup()

  @Provides
  fun floatMonoid(): Monoid<Float> = Float.monoid()

  @Provides
  fun floatOrder(): Order<Float> = Float.order()

  @Provides
  fun floatEq(): Eq<@JvmSuppressWildcards Float> = Float.eq()

  @Provides
  fun floatHash(): Hash<Float> = Float.hash()

}