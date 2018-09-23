package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.ExitCase.Error
import arrow.typeclasses.MonadError

sealed class ExitCase<out E> {

    object Completed : ExitCase<Nothing>()

    object Cancelled : ExitCase<Nothing>()

    data class Error<out E>(val e: E) : ExitCase<E>()
}

fun <E> Either<E, *>.toExitCase() =
    fold(::Error) { ExitCase.Completed }

interface Bracket<F, E> : MonadError<F, E> {

    fun <A, B> Kind<F, A>.bracketCase(release: (A, ExitCase<E>) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>

    fun <A, B> Kind<F, A>.bracket(release: (A) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B> =
        bracketCase({ a, _ -> release(a) }, use)

    fun <A> Kind<F, A>.uncancelable(): Kind<F, A> =
        bracket({ just<Unit>(Unit) }, { just(it) })

    fun <A> Kind<F, A>.guarantee(finalizer: Kind<F, Unit>): Kind<F, A> =
        bracket({ _ -> finalizer }, { _ -> this })

    fun <A> Kind<F, A>.guaranteeCase(finalizer: (ExitCase<E>) -> Kind<F, Unit>): Kind<F, A> =
        bracketCase({ _, e -> finalizer(e) }, { _ -> this })

    companion object {

        fun <E, A> attempt(value: Either<E, A>): ExitCase<E> = when (value) {
            is Either.Left -> Error(value.a)
            is Either.Right -> ExitCase.Completed
        }
    }
}

