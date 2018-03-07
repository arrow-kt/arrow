package arrow.effecs

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.deriving
import arrow.effects.Proc
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink

fun <A> Mono<A>.k(): MonoK<A> = MonoK(this)

fun <A> MonoKOf<A>.value(): Mono<A> =
        this.fix().mono

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class
)
data class MonoK<A>(val mono: Mono<A>) : MonoKOf<A>, MonoKKindedJ<A> {
    fun <B> map(f: (A) -> B): MonoK<B> =
            mono.map(f).k()

    fun <B> ap(fa: MonoKOf<(A) -> B>): MonoK<B> =
            flatMap { a -> fa.fix().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> MonoKOf<B>): MonoK<B> =
            mono.flatMap { f(it).fix().mono }.k()

    fun runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
            mono.flatMap { cb(Right(it)).value() }.onErrorResume { cb(Left(it)).value() }.k()

    companion object {
        fun <A> pure(a: A): MonoK<A> =
                Mono.just(a).k()

        fun <A> raiseError(t: Throwable): MonoK<A> =
                Mono.error<A>(t).k()

        operator fun <A> invoke(fa: () -> A): MonoK<A> =
                suspend { pure(fa()) }

        fun <A> suspend(fa: () -> MonoKOf<A>): MonoK<A> =
                Mono.defer { fa().value() }.k()

        fun <A> runAsync(fa: Proc<A>): MonoK<A> =
                Mono.create { emitter: MonoSink<A> ->
                    fa { either: Either<Throwable, A> ->
                        either.fold({
                            emitter.error(it)
                        }, {
                            emitter.success(it)
                        })
                    }
                }.k()

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> MonoKOf<Either<A, B>>): MonoK<B> {
            val either = f(a).fix().value().block()
            return when (either) {
                is Either.Left -> tailRecM(either.a, f)
                is Either.Right -> Mono.just(either.b).k()
            }
        }

        fun monadFlat(): MonoKMonadInstance = MonoKMonadInstanceImplicits.instance()

        fun monadErrorFlat(): MonoKMonadErrorInstance = MonoKMonadErrorInstanceImplicits.instance()

    }

}

fun <A> MonoKOf<A>.handleErrorWith(function: (Throwable) -> MonoK<A>): MonoK<A> =
        this.fix().mono.onErrorResume { t: Throwable -> function(t).mono }.k()
