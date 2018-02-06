package arrow.dagger.instances.tests

import arrow.core.EitherKindPartial
import arrow.core.ForEval
import arrow.core.ForId
import arrow.core.ForOption
import arrow.dagger.instances.*
import arrow.data.*
import arrow.typeclasses.*
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Module
class LocalCoproductInstances : CoproductInstances<ForOption, ForListKW>()

@Module
class LocalEitherInstances : EitherInstances<Unit>()

@Module
class LocalEitherTInstances : EitherTInstances<ForOption, Unit>()

@Module
class LocalOptionTTInstances : OptionTInstances<ForOption>()

@Module
class LocalFunction1Instances : Function1Instances<ForOption>()

@Module
class LocalKleisliInstances : KleisliInstances<ForOption, Unit>()

@Module
class LocalMapKWInstances : MapKWInstances<String>()

@Module
class LocalSortedMapKWInstances : SortedMapKWInstances<String>()

@Module
class LocalStateTInstances : StateTInstances<ForOption, Unit>()

typealias F = ForOption
typealias G = ForListKW
typealias L = Unit
typealias D = Unit
typealias S = Unit
typealias K = String

/**
 * If the component below compiles it means the `ArrowInstances` was successful resolving
 * all the declared below instances at compile time along with the helper for instances that take type params
 * and need explicit evidence of a @Module such as `LocalStateTInstances`
 */
@Singleton
@Component(modules = [
    ArrowInstances::class,
    LocalCoproductInstances::class,
    LocalEitherInstances::class,
    LocalEitherTInstances::class,
    LocalFunction1Instances::class,
    LocalKleisliInstances::class,
    LocalMapKWInstances::class,
    LocalOptionTTInstances::class,
    LocalSortedMapKWInstances::class,
    LocalStateTInstances::class
])
interface Runtime {
    fun coproductFunctor(): Functor<CoproductKindPartial<F, G>>
    fun coproductComonad(): Functor<CoproductKindPartial<F, G>>
    fun coproductFoldable(): Foldable<CoproductKindPartial<F, G>>
    fun coproductTraverse(): Traverse<CoproductKindPartial<F, G>>
    fun eitherFunctor(): Functor<EitherKindPartial<L>>
    fun eitherApplicative(): Applicative<EitherKindPartial<L>>
    fun eitherMonad(): Monad<EitherKindPartial<L>>
    fun eitherFoldable(): Foldable<EitherKindPartial<L>>
    fun eitherTraverse(): Traverse<EitherKindPartial<L>>
    fun eitherSemigroupK(): SemigroupK<EitherKindPartial<L>>
    fun eitherTFunctor(): Functor<EitherTKindPartial<F, L>>
    fun eitherTApplicative(): Applicative<EitherTKindPartial<F, L>>
    fun eitherTMonad(): Monad<EitherTKindPartial<F, L>>
    fun eitherTApplicativeError(): ApplicativeError<EitherTKindPartial<F, L>, L>
    fun eitherTMonadError(): MonadError<EitherTKindPartial<F, L>, L>
    fun eitherTFoldable(): Foldable<EitherTKindPartial<F, L>>
    fun eitherTTraverse(): Traverse<EitherTKindPartial<F, L>>
    fun eitherTSemigroupK(): SemigroupK<EitherTKindPartial<F, L>>
    fun evalFunctor(): Functor<ForEval>
    fun evalApplicative(): Applicative<ForEval>
    fun evalMonad(): Monad<ForEval>
    fun evalComonad(): Comonad<ForEval>
    fun evalBimonad(): Bimonad<ForEval>
    fun function0Functor(): Functor<ForFunction0>
    fun function0Applicative(): Applicative<ForFunction0>
    fun function0Monad(): Monad<ForFunction0>
    fun function0Comonad(): Comonad<ForFunction0>
    fun function0Bimonad(): Bimonad<ForFunction0>
    fun function1Functor(): Functor<Function1KindPartial<F>>
    fun function1Applicative(): Applicative<Function1KindPartial<F>>
    fun function1Monad(): Monad<Function1KindPartial<F>>
    fun idFunctor(): Functor<ForId>
    fun idApplicative(): Applicative<ForId>
    fun idMonad(): Monad<ForId>
    fun idComonad(): Comonad<ForId>
    fun idBimonad(): Bimonad<ForId>
    fun kleisliFunctor(): Functor<KleisliKindPartial<F, D>>
    fun kleisliApplicative(): Applicative<KleisliKindPartial<F, D>>
    fun kleisliMonad(): Monad<KleisliKindPartial<F, D>>
    fun kleisliApplicativeError(): ApplicativeError<KleisliKindPartial<F, D>, D>
    fun kleisliMonadError(): MonadError<KleisliKindPartial<F, D>, D>
    fun listKWFunctor(): Functor<ForListKW>
    fun listKWApplicative(): Applicative<ForListKW>
    fun listKWMonad(): Monad<ForListKW>
    fun listKWFoldable(): Foldable<ForListKW>
    fun listKWTraverse(): Traverse<ForListKW>
    fun listKWSemigroupK(): SemigroupK<ForListKW>
    fun listKWMonoidK(): MonoidK<ForListKW>
    fun mapKWFunctor(): Functor<MapKWKindPartial<K>>
    fun mapKWFoldable(): Foldable<MapKWKindPartial<K>>
    fun mapKWTraverse(): Traverse<MapKWKindPartial<K>>
    fun nonEmptyListFunctor(): Functor<ForNonEmptyList>
    fun nonEmptyListApplicative(): Applicative<ForNonEmptyList>
    fun nonEmptyListMonad(): Monad<ForNonEmptyList>
    fun nonEmptyListFoldable(): Foldable<ForNonEmptyList>
    fun nonEmptyListTraverse(): Traverse<ForNonEmptyList>
    fun nonEmptyListSemigroupK(): SemigroupK<ForNonEmptyList>
    fun nonEmptyListComonad(): Comonad<ForNonEmptyList>
    fun nonEmptyListBimonad(): Bimonad<ForNonEmptyList>
    fun byteSemigroup(): Semigroup<Byte>
    fun byteMonoid(): Monoid<Byte>
    fun byteOrder(): Order<Byte>
    fun byteEq(): Eq<@JvmSuppressWildcards Byte>
    fun doubleSemigroup(): Semigroup<Double>
    fun doubleMonoid(): Monoid<Double>
    fun doubleOrder(): Order<Double>
    fun doubleEq(): Eq<@JvmSuppressWildcards Double>
    fun intSemigroup(): Semigroup<Int>
    fun intMonoid(): Monoid<Int>
    fun intOrder(): Order<Int>
    fun intEq(): Eq<@JvmSuppressWildcards Int>
    fun longSemigroup(): Semigroup<Long>
    fun longMonoid(): Monoid<Long>
    fun longOrder(): Order<Long>
    fun longEq(): Eq<@JvmSuppressWildcards Long>
    fun shortSemigroup(): Semigroup<Short>
    fun shortMonoid(): Monoid<Short>
    fun shortOrder(): Order<Short>
    fun shortEq(): Eq<@JvmSuppressWildcards Short>
    fun floatSemigroup(): Semigroup<Float>
    fun floatMonoid(): Monoid<Float>
    fun floatOrder(): Order<Float>
    fun floatEq(): Eq<@JvmSuppressWildcards Float>
    fun optionFunctor(): Functor<ForOption>
    fun optionApplicative(): Applicative<ForOption>
    fun optionMonad(): Monad<ForOption>
    fun optionMonadError(): MonadError<ForOption, Unit>
    fun optionFoldable(): Foldable<ForOption>
    fun optionTraverse(): Traverse<ForOption>
    fun optionTFunctor(): Functor<OptionTKindPartial<F>>
    fun optionTApplicative(): Applicative<OptionTKindPartial<F>>
    fun optionTMonad(): Monad<OptionTKindPartial<F>>
    fun optionTFoldable(): Foldable<OptionTKindPartial<F>>
    fun optionTTraverse(): Traverse<OptionTKindPartial<F>>
    fun optionTSemigroupK(): SemigroupK<OptionTKindPartial<F>>
    fun optionTMonoidK(): MonoidK<OptionTKindPartial<F>>
    fun sequenceKWFunctor(): Functor<ForSequenceKW>
    fun sequenceKWApplicative(): Applicative<ForSequenceKW>
    fun sequenceKWMonad(): Monad<ForSequenceKW>
    fun sequenceKWFoldable(): Foldable<ForSequenceKW>
    fun sequenceKWTraverse(): Traverse<ForSequenceKW>
    fun sequenceKWMonoidK(): MonoidK<ForSequenceKW>
    fun sequenceKWSemigroupK(): SemigroupK<ForSequenceKW>
    fun setKWFoldable(): Foldable<ForSetKW>
    fun setKWMonoidK(): MonoidK<ForSetKW>
    fun setKWSemigroupK(): SemigroupK<ForSetKW>
    fun sortedMapKWFunctor(): Functor<SortedMapKWKindPartial<K>>
    fun sortedMapKWFoldable(): Foldable<SortedMapKWKindPartial<K>>
    fun sortedMapKWTraverse(): Traverse<SortedMapKWKindPartial<K>>
    fun stateTFunctor(): Functor<StateTKindPartial<F, S>>
    fun stateTApplicative(): Applicative<StateTKindPartial<F, S>>
    fun stateTMonad(): Monad<StateTKindPartial<F, S>>
    fun stateTApplicativeError(): ApplicativeError<StateTKindPartial<F, S>, S>
    fun stateTMonadError(): MonadError<StateTKindPartial<F, S>, S>
    fun stringSemigroup(): Semigroup<String>
    fun stringMonoid(): Monoid<String>
    fun stringEq(): Eq<@JvmSuppressWildcards String>
    fun tryFunctor(): Functor<ForTry>
    fun tryApplicative(): Applicative<ForTry>
    fun tryMonad(): Monad<ForTry>
    fun tryMonadError(): MonadError<ForTry, Throwable>
    fun tryFoldable(): Foldable<ForTry>
    fun tryTraverse(): Traverse<ForTry>
}

object Arrow {
    val instances = DaggerRuntime.builder().build()
}

object test {
    @JvmStatic
    fun main(args: Array<String>) {
        println(Arrow.instances.optionApplicative().pure(1))
    }
}




