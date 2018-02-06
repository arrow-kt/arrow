package arrow.dagger.instances.tests

import arrow.core.EitherKindPartial
import arrow.core.EvalHK
import arrow.core.IdHK
import arrow.core.OptionHK
import arrow.dagger.instances.*
import arrow.data.*
import arrow.typeclasses.*
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Module
class LocalCoproductInstances : CoproductInstances<OptionHK, ListKWHK>()

@Module
class LocalEitherInstances : EitherInstances<Unit>()

@Module
class LocalEitherTInstances : EitherTInstances<OptionHK, Unit>()

@Module
class LocalOptionTTInstances : OptionTInstances<OptionHK>()

@Module
class LocalFunction1Instances : Function1Instances<OptionHK>()

@Module
class LocalKleisliInstances : KleisliInstances<OptionHK, Unit>()

@Module
class LocalMapKWInstances : MapKWInstances<String>()

@Module
class LocalSortedMapKWInstances : SortedMapKWInstances<String>()

@Module
class LocalStateTInstances : StateTInstances<OptionHK, Unit>()

typealias F = OptionHK
typealias G = ListKWHK
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
    fun evalFunctor(): Functor<EvalHK>
    fun evalApplicative(): Applicative<EvalHK>
    fun evalMonad(): Monad<EvalHK>
    fun evalComonad(): Comonad<EvalHK>
    fun evalBimonad(): Bimonad<EvalHK>
    fun function0Functor(): Functor<Function0HK>
    fun function0Applicative(): Applicative<Function0HK>
    fun function0Monad(): Monad<Function0HK>
    fun function0Comonad(): Comonad<Function0HK>
    fun function0Bimonad(): Bimonad<Function0HK>
    fun function1Functor(): Functor<Function1KindPartial<F>>
    fun function1Applicative(): Applicative<Function1KindPartial<F>>
    fun function1Monad(): Monad<Function1KindPartial<F>>
    fun idFunctor(): Functor<IdHK>
    fun idApplicative(): Applicative<IdHK>
    fun idMonad(): Monad<IdHK>
    fun idComonad(): Comonad<IdHK>
    fun idBimonad(): Bimonad<IdHK>
    fun kleisliFunctor(): Functor<KleisliKindPartial<F, D>>
    fun kleisliApplicative(): Applicative<KleisliKindPartial<F, D>>
    fun kleisliMonad(): Monad<KleisliKindPartial<F, D>>
    fun kleisliApplicativeError(): ApplicativeError<KleisliKindPartial<F, D>, D>
    fun kleisliMonadError(): MonadError<KleisliKindPartial<F, D>, D>
    fun listKWFunctor(): Functor<ListKWHK>
    fun listKWApplicative(): Applicative<ListKWHK>
    fun listKWMonad(): Monad<ListKWHK>
    fun listKWFoldable(): Foldable<ListKWHK>
    fun listKWTraverse(): Traverse<ListKWHK>
    fun listKWSemigroupK(): SemigroupK<ListKWHK>
    fun listKWMonoidK(): MonoidK<ListKWHK>
    fun mapKWFunctor(): Functor<MapKWKindPartial<K>>
    fun mapKWFoldable(): Foldable<MapKWKindPartial<K>>
    fun mapKWTraverse(): Traverse<MapKWKindPartial<K>>
    fun nonEmptyListFunctor(): Functor<NonEmptyListHK>
    fun nonEmptyListApplicative(): Applicative<NonEmptyListHK>
    fun nonEmptyListMonad(): Monad<NonEmptyListHK>
    fun nonEmptyListFoldable(): Foldable<NonEmptyListHK>
    fun nonEmptyListTraverse(): Traverse<NonEmptyListHK>
    fun nonEmptyListSemigroupK(): SemigroupK<NonEmptyListHK>
    fun nonEmptyListComonad(): Comonad<NonEmptyListHK>
    fun nonEmptyListBimonad(): Bimonad<NonEmptyListHK>
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
    fun optionFunctor(): Functor<OptionHK>
    fun optionApplicative(): Applicative<OptionHK>
    fun optionMonad(): Monad<OptionHK>
    fun optionMonadError(): MonadError<OptionHK, Unit>
    fun optionFoldable(): Foldable<OptionHK>
    fun optionTraverse(): Traverse<OptionHK>
    fun optionTFunctor(): Functor<OptionTKindPartial<F>>
    fun optionTApplicative(): Applicative<OptionTKindPartial<F>>
    fun optionTMonad(): Monad<OptionTKindPartial<F>>
    fun optionTFoldable(): Foldable<OptionTKindPartial<F>>
    fun optionTTraverse(): Traverse<OptionTKindPartial<F>>
    fun optionTSemigroupK(): SemigroupK<OptionTKindPartial<F>>
    fun optionTMonoidK(): MonoidK<OptionTKindPartial<F>>
    fun sequenceKWFunctor(): Functor<SequenceKWHK>
    fun sequenceKWApplicative(): Applicative<SequenceKWHK>
    fun sequenceKWMonad(): Monad<SequenceKWHK>
    fun sequenceKWFoldable(): Foldable<SequenceKWHK>
    fun sequenceKWTraverse(): Traverse<SequenceKWHK>
    fun sequenceKWMonoidK(): MonoidK<SequenceKWHK>
    fun sequenceKWSemigroupK(): SemigroupK<SequenceKWHK>
    fun setKWFoldable(): Foldable<SetKWHK>
    fun setKWMonoidK(): MonoidK<SetKWHK>
    fun setKWSemigroupK(): SemigroupK<SetKWHK>
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
    fun tryFunctor(): Functor<TryHK>
    fun tryApplicative(): Applicative<TryHK>
    fun tryMonad(): Monad<TryHK>
    fun tryMonadError(): MonadError<TryHK, Throwable>
    fun tryFoldable(): Foldable<TryHK>
    fun tryTraverse(): Traverse<TryHK>
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




