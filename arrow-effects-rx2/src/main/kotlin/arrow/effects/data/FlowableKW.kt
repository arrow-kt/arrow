package arrow.effects

import arrow.HK
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Right
import arrow.deriving
import arrow.higherkind
import arrow.typeclasses.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

fun <A> Flowable<A>.k(): FlowableKW<A> = FlowableKW(this)

fun <A> FlowableKWKind<A>.value(): Flowable<A> = this.ev().flowable

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Foldable::class,
        Traverse::class
)
data class FlowableKW<A>(val flowable: Flowable<A>) : FlowableKWKind<A>, FlowableKWKindedJ<A> {

    fun <B> map(f: (A) -> B): FlowableKW<B> =
            flowable.map(f).k()

    fun <B> ap(fa: FlowableKWKind<(A) -> B>): FlowableKW<B> =
            flatMap { a -> fa.ev().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            flowable.flatMap { f(it).ev().flowable }.k()

    fun <B> concatMap(f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            flowable.concatMap { f(it).ev().flowable }.k()

    fun <B> switchMap(f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
            flowable.switchMap { f(it).ev().flowable }.k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = flowable.reduce(b, f).blockingGet()

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: FlowableKW<A>): Eval<B> = when {
            fa_p.flowable.isEmpty.blockingGet() -> lb
            else -> f(fa_p.flowable.blockingFirst(), Eval.defer { loop(fa_p.flowable.skip(1).k()) })
        }

        return Eval.defer { loop(this) }
    }

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, FlowableKW<B>> =
            foldRight(Eval.always { GA.pure(Flowable.empty<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { Flowable.concat(Flowable.just<B>(it.a), it.b.flowable).k() }
            }.value()

    fun runAsync(cb: (Either<Throwable, A>) -> FlowableKWKind<Unit>): FlowableKW<Unit> =
            FlowableKW { flowable.subscribe({ cb(Right(it)) }, { cb(Left(it)) }).let { } }

    companion object {
        fun <A> pure(a: A): FlowableKW<A> =
                Flowable.just(a).k()

        fun <A> raiseError(t: Throwable): FlowableKW<A> =
                Flowable.error<A>(t).k()

        operator fun <A> invoke(fa: () -> A): FlowableKW<A> =
                suspend { pure(fa()) }

        fun <A> suspend(fa: () -> FlowableKWKind<A>): FlowableKW<A> =
                Flowable.defer { fa().value() }.k()

        fun <A> async(fa: Proc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableKW<A> =
                Flowable.create({ emitter: FlowableEmitter<A> ->
                    fa { either: Either<Throwable, A> ->
                        either.fold({
                            emitter.onError(it)
                        }, {
                            emitter.onNext(it)
                            emitter.onComplete()
                        })

                    }
                }, mode).k()

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> FlowableKWKind<Either<A, B>>): FlowableKW<B> {
            val either = f(a).ev().value().blockingFirst()
            return when (either) {
                is Either.Left -> tailRecM(either.a, f)
                is Either.Right -> Flowable.just(either.b).k()
            }
        }

        fun monadFlat(): FlowableKWMonadInstance = FlowableKWMonadInstanceImplicits.instance()

        fun monadConcat(): FlowableKWMonadInstance = object : FlowableKWMonadInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().concatMap { f(it).ev() }
        }

        fun monadSwitch(): FlowableKWMonadInstance = object : FlowableKWMonadInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().switchMap { f(it).ev() }
        }

        fun monadErrorFlat(): FlowableKWMonadErrorInstance = FlowableKWMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): FlowableKWMonadErrorInstance = object : FlowableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().concatMap { f(it).ev() }
        }

        fun monadErrorSwitch(): FlowableKWMonadErrorInstance = object : FlowableKWMonadErrorInstance {
            override fun <A, B> flatMap(fa: FlowableKWKind<A>, f: (A) -> FlowableKWKind<B>): FlowableKW<B> =
                    fa.ev().switchMap { f(it).ev() }
        }

        fun syncBuffer(): FlowableKWSyncInstance = FlowableKWSyncInstanceImplicits.instance()

        fun syncDrop(): FlowableKWSyncInstance = object : FlowableKWSyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun syncError(): FlowableKWSyncInstance = object : FlowableKWSyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun syncLatest(): FlowableKWSyncInstance = object : FlowableKWSyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun syncMissing(): FlowableKWSyncInstance = object : FlowableKWSyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }

        fun asyncBuffer(): FlowableKWAsyncInstance = FlowableKWAsyncInstanceImplicits.instance()

        fun asyncDrop(): FlowableKWAsyncInstance = object : FlowableKWAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun asyncError(): FlowableKWAsyncInstance = object : FlowableKWAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun asyncLatest(): FlowableKWAsyncInstance = object : FlowableKWAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun asyncMissing(): FlowableKWAsyncInstance = object : FlowableKWAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }

        fun effectBuffer(): FlowableKWEffectInstance = FlowableKWEffectInstanceImplicits.instance()

        fun effectDrop(): FlowableKWEffectInstance = object : FlowableKWEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun effectError(): FlowableKWEffectInstance = object : FlowableKWEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun effectLatest(): FlowableKWEffectInstance = object : FlowableKWEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun effectMissing(): FlowableKWEffectInstance = object : FlowableKWEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }
    }
}

fun <A> FlowableKWKind<A>.handleErrorWith(function: (Throwable) -> FlowableKW<A>): FlowableKW<A> =
        this.ev().flowable.onErrorResumeNext { t: Throwable -> function(t).flowable }.k()