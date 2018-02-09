package arrow.effects

import arrow.Kind
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

fun <A> Flowable<A>.k(): FlowableK<A> = FlowableK(this)

fun <A> FlowableKOf<A>.value(): Flowable<A> = this.extract().flowable

@higherkind
@deriving(
        Functor::class,
        Applicative::class,
        Monad::class,
        Foldable::class,
        Traverse::class
)
data class FlowableK<A>(val flowable: Flowable<A>) : FlowableKOf<A>, FlowableKKindedJ<A> {

    fun <B> map(f: (A) -> B): FlowableK<B> =
            flowable.map(f).k()

    fun <B> ap(fa: FlowableKOf<(A) -> B>): FlowableK<B> =
            flatMap { a -> fa.extract().map { ff -> ff(a) } }

    fun <B> flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
            flowable.flatMap { f(it).extract().flowable }.k()

    fun <B> concatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
            flowable.concatMap { f(it).extract().flowable }.k()

    fun <B> switchMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
            flowable.switchMap { f(it).extract().flowable }.k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = flowable.reduce(b, f).blockingGet()

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: FlowableK<A>): Eval<B> = when {
            fa_p.flowable.isEmpty.blockingGet() -> lb
            else -> f(fa_p.flowable.blockingFirst(), Eval.defer { loop(fa_p.flowable.skip(1).k()) })
        }

        return Eval.defer { loop(this) }
    }

    fun <G, B> traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, FlowableK<B>> =
            foldRight(Eval.always { GA.pure(Flowable.empty<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { Flowable.concat(Flowable.just<B>(it.a), it.b.flowable).k() }
            }.value()

    fun runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
            flowable.flatMap { cb(Right(it)).value() }.onErrorResumeNext(io.reactivex.functions.Function { cb(Left(it)).value() }).k()

    companion object {
        fun <A> pure(a: A): FlowableK<A> =
                Flowable.just(a).k()

        fun <A> raiseError(t: Throwable): FlowableK<A> =
                Flowable.error<A>(t).k()

        operator fun <A> invoke(fa: () -> A): FlowableK<A> =
                suspend { pure(fa()) }

        fun <A> suspend(fa: () -> FlowableKOf<A>): FlowableK<A> =
                Flowable.defer { fa().value() }.k()

        fun <A> async(fa: Proc<A>, mode: BackpressureStrategy = BackpressureStrategy.BUFFER): FlowableK<A> =
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

        tailrec fun <A, B> tailRecM(a: A, f: (A) -> FlowableKOf<Either<A, B>>): FlowableK<B> {
            val either = f(a).extract().value().blockingFirst()
            return when (either) {
                is Either.Left -> tailRecM(either.a, f)
                is Either.Right -> Flowable.just(either.b).k()
            }
        }

        fun monadFlat(): FlowableKMonadInstance = FlowableKMonadInstanceImplicits.instance()

        fun monadConcat(): FlowableKMonadInstance = object : FlowableKMonadInstance {
            override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
                    fa.extract().concatMap { f(it).extract() }
        }

        fun monadSwitch(): FlowableKMonadInstance = object : FlowableKMonadInstance {
            override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
                    fa.extract().switchMap { f(it).extract() }
        }

        fun monadErrorFlat(): FlowableKMonadErrorInstance = FlowableKMonadErrorInstanceImplicits.instance()

        fun monadErrorConcat(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
            override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
                    fa.extract().concatMap { f(it).extract() }
        }

        fun monadErrorSwitch(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
            override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
                    fa.extract().switchMap { f(it).extract() }
        }

        fun syncBuffer(): FlowableKMonadSuspendInstance = FlowableKMonadSuspendInstanceImplicits.instance()

        fun syncDrop(): FlowableKMonadSuspendInstance = object : FlowableKMonadSuspendInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun syncError(): FlowableKMonadSuspendInstance = object : FlowableKMonadSuspendInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun syncLatest(): FlowableKMonadSuspendInstance = object : FlowableKMonadSuspendInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun syncMissing(): FlowableKMonadSuspendInstance = object : FlowableKMonadSuspendInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }

        fun asyncBuffer(): FlowableKAsyncInstance = FlowableKAsyncInstanceImplicits.instance()

        fun asyncDrop(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun asyncError(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun asyncLatest(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun asyncMissing(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }

        fun effectBuffer(): FlowableKEffectInstance = FlowableKEffectInstanceImplicits.instance()

        fun effectDrop(): FlowableKEffectInstance = object : FlowableKEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
        }

        fun effectError(): FlowableKEffectInstance = object : FlowableKEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
        }

        fun effectLatest(): FlowableKEffectInstance = object : FlowableKEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
        }

        fun effectMissing(): FlowableKEffectInstance = object : FlowableKEffectInstance {
            override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
        }
    }
}

fun <A> FlowableKOf<A>.handleErrorWith(function: (Throwable) -> FlowableK<A>): FlowableK<A> =
        this.extract().flowable.onErrorResumeNext { t: Throwable -> function(t).flowable }.k()