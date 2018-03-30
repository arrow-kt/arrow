package arrow.dagger.instances

import arrow.instances.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import dagger.Module
import dagger.Provides

@Module
class NumberInstances {

    @Provides
    fun byteSemigroup(): Semigroup<Byte> = ByteMonoidInstance

    @Provides
    fun byteMonoid(): Monoid<Byte> = ByteMonoidInstance

    @Provides
    fun byteOrder(): Order<Byte> = ByteOrderInstance

    @Provides
    fun byteEq(): Eq<Byte> = ByteEqInstance

    @Provides
    fun doubleSemigroup(): Semigroup<Double> = DoubleMonoid

    @Provides
    fun doubleMonoid(): Monoid<Double> = DoubleMonoid

    @Provides
    fun doubleOrder(): Order<Double> = DoubleOrderInstance

    @Provides
    fun doubleEq(): Eq<@JvmSuppressWildcards Double> = DoubleEqInstance

    @Provides
    fun intSemigroup(): Semigroup<Int> = IntMonoidInstance

    @Provides
    fun intMonoid(): Monoid<Int> = IntMonoidInstance

    @Provides
    fun intOrder(): Order<Int> = IntOrderInstance

    @Provides
    fun intEq(): Eq<@JvmSuppressWildcards Int> = IntEqInstance

    @Provides
    fun longSemigroup(): Semigroup<Long> = LongMonoidInstance

    @Provides
    fun longMonoid(): Monoid<Long> = LongMonoidInstance

    @Provides
    fun longOrder(): Order<Long> = LongOrderInstance

    @Provides
    fun longEq(): Eq<@JvmSuppressWildcards Long> = LongEqInstance

    @Provides
    fun shortSemigroup(): Semigroup<Short> = ShortMonoid

    @Provides
    fun shortMonoid(): Monoid<Short> = ShortMonoid

    @Provides
    fun shortOrder(): Order<Short> = ShortOrderInstance

    @Provides
    fun shortEq(): Eq<@JvmSuppressWildcards Short> = ShortEqInstance

    @Provides
    fun floatSemigroup(): Semigroup<Float> = FloatMonoid

    @Provides
    fun floatMonoid(): Monoid<Float> = FloatMonoid

    @Provides
    fun floatOrder(): Order<Float> = FloatOrderInstance

    @Provides
    fun floatEq(): Eq<@JvmSuppressWildcards Float> = FloatEqInstance

}