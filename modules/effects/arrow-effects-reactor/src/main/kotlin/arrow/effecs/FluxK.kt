package arrow.effecs

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Right
import arrow.core.Left
import arrow.deriving
import arrow.effects.Proc
import arrow.higherkind
import arrow.typeclasses.*
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

fun <A> Flux<A>.k(): FluxK<A> = FluxK(this)

fun <A> FluxKOf<A>.value(): Flux<A> =
        this.fix().flux

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Foldable::class,
        Traverse::class
)
data class FluxK<A>(val flux: Flux<A>) : FluxKOf<A>, FluxKKindedJ<A> {

    fun <B> map(f: (A) -> B): FluxK<B> =
            flux.map(f).k()

    fun <B> ap(fa: FluxKOf<(A) -> B>): FluxK<B> =
            flatMap { a -> fa.fix().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
            flux.flatMap { f(it).fix().flux }.k()

    fun <B> concatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
            flux.concatMap { f(it).fix().flux }.k()

    fun <B> switchMap(f: (A) -> FluxKOf<B>): FluxK<B> =
            flux.switchMap { f(it).fix().flux }.k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = flux.reduce(b, f).block()

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: FluxK<A>): Eval<B> = when {
            fa_p.flux.hasElements().block() -> lb
            else -> f(fa_p.flux.blockFirst(), Eval.defer { loop(fa_p.flux.skip(1).k()) })
        }

        return Eval.defer { loop(this) }
    }

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, FluxK<B>> =
            foldRight(Eval.always { GA.pure(Flux.empty<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { Flux.concat(Flux.just<B>(it.a), it.b.flux).k() }
            }.value()

    fun runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
            flux.flatMap { cb(Right(it)).value() }.onErrorResume { cb(Left(it)).value() }.k()

    companion object {
        fun <A> pure(a: A): FluxK<A> =
                Flux.just(a).k()

        fun <A> raiseError(t: Throwable): FluxK<A> =
                Flux.error<A>(t).k()

        operator fun <A> invoke(fa: () -> A): FluxK<A> =
                suspend { pure(fa()) }

        fun <A> suspend(fa: () -> FluxKOf<A>): FluxK<A> =
                Flux.defer { fa().value() }.k()

        fun <A> runAsync(fa: Proc<A>): FluxK<A> =
                Flux.create { emitter: FluxSink<A> ->
                    fa { either: Either<Throwable, A> ->
                        either.fold({
                            emitter.error(it)
                        }, {
                            emitter.next(it)
                            emitter.complete()
                        })
                    }
                }.k()

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> FluxKOf<Either<A, B>>): FluxK<B> {
            val either = f(a).fix().value().blockFirst()
            return when (either) {
                is Either.Left -> tailRecM(either.a, f)
                is Either.Right -> Flux.just(either.b).k()
            }
        }

        fun monadFlat(): FluxKMonadInstance = FluxKMonadInstanceImplicits.instance()

        fun monadConcat(): FluxKMonadInstance = object : FluxKMonadInstance {
            override fun <A, B> flatMap(fa: FluxKOf<A>, f: (A) -> FluxKOf<B>): FluxK<B> =
                    fa.fix().concatMap { f(it).fix() }
        }

        fun monadSwitch(): FluxKMonadInstance = object : FluxKMonadErrorInstance {
            override fun <A, B> flatMap(fa: FluxKOf<A>, f: (A) -> FluxKOf<B>): FluxK<B> =
                    fa.fix().switchMap { f(it).fix() }
        }

        fun monadErrorFlat(): FluxKMonadErrorInstance = FluxKMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): FluxKMonadErrorInstance = object : FluxKMonadErrorInstance {
            override fun <A, B> flatMap(fa: FluxKOf<A>, f: (A) -> FluxKOf<B>): FluxK<B> =
                    fa.fix().concatMap { f(it).fix() }
        }

        fun monadErrorSwitch(): FluxKMonadErrorInstance = object : FluxKMonadErrorInstance {
            override fun <A, B> flatMap(fa: FluxKOf<A>, f: (A) -> FluxKOf<B>): FluxK<B> =
                    fa.fix().switchMap { f(it).fix() }
        }
    }
}

fun <A> FluxKOf<A>.handleErrorWith(function: (Throwable) -> FluxK<A>): FluxK<A> =
        this.fix().flux.onErrorResume { t: Throwable -> function(t).flux }.k()
