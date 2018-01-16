package arrow.debug

import arrow.TC
import arrow.data.Try
import arrow.effects.AsyncContext
import arrow.effects.asyncContext
import arrow.mtl.*
import arrow.typeclasses.*
import kotlin.reflect.KClass

inline fun <reified F, reified E> debugInstanceLookups(): Map<KClass<out TC>, () -> TC> = mapOf(
        Alternative::class to { alternative<F>() },
        Applicative::class to { applicative<F>() },
        ApplicativeError::class to { applicativeError<F, E>() },
        Bifoldable::class to { bifoldable<F>() },
        Bimonad::class to { bimonad<F>() },
        Comonad::class to { comonad<F>() },
        Eq::class to { eq<F>() },
        Foldable::class to { foldable<F>() },
        Functor::class to { functor<F>() },
        FunctorFilter::class to { functorFilter<F>() },
        Monad::class to { monad<F>() },
        MonadCombine::class to { monadCombine<F>() },
        MonadError::class to { monadError<F, E>() },
        MonadFilter::class to { monadFilter<F>() },
        MonadReader::class to { monadReader<F, E>() },
        MonadState::class to { monadState<F, E>() },
        MonadWriter::class to { monadWriter<F, E>() },
        Monoid::class to { monoid<F>() },
        MonoidK::class to { monoidK<F>() },
        Reducible::class to { reducible<F>() },
        Semigroup::class to { semigroup<F>() },
        SemigroupK::class to { semigroupK<F>() },
        Traverse::class to { traverse<F>() },
        TraverseFilter::class to { traverse<F>() },
        AsyncContext::class to { asyncContext<F>() }
)

inline fun <reified F, reified E> showInstances(
        debugLookupTable: Map<KClass<out TC>, () -> TC> = debugInstanceLookups<F, E>()): List<String?> =
        debugLookupTable.entries
                .filter { Try { it.value() }.fold({ false }, { true }) }
                .map { it.key.simpleName }
