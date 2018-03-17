package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.effects.continuations.AsyncContinuation
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.continuations.BindingCatchContinuation
import arrow.typeclasses.continuations.BindingContinuation
import io.reactivex.BackpressureStrategy
import kotlin.coroutines.experimental.CoroutineContext

@instance(FlowableK::class)
interface FlowableKApplicativeErrorInstance :
        FlowableKApplicativeInstance,
        ApplicativeError<ForFlowableK, Throwable> {
    override fun <A> raiseError(e: Throwable): FlowableK<A> =
            FlowableK.raiseError(e)

    override fun <A> handleErrorWith(fa: FlowableKOf<A>, f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
            fa.handleErrorWith { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKMonadInstance : Monad<ForFlowableK> {
    override fun <A, B> ap(fa: FlowableKOf<A>, ff: FlowableKOf<kotlin.Function1<A, B>>): FlowableK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: kotlin.Function1<A, FlowableKOf<B>>): FlowableK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: Kind<ForFlowableK, A>, f: (A) -> Kind<ForFlowableK, B>): Kind<ForFlowableK, B> =
            fa.fix().flatMapIn(context, f)

    override fun <A, B> map(fa: FlowableKOf<A>, f: kotlin.Function1<A, B>): FlowableK<B> =
            fa.fix().map(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FlowableKOf<arrow.core.Either<A, B>>>): FlowableK<B> =
            FlowableK.tailRecM(a, f)

    override fun <A> pure(a: A): FlowableK<A> =
            FlowableK.pure(a)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForFlowableK, *>.() -> B): FlowableK<B> =
            AsyncContinuation.binding(::identity, FlowableK.async(), context, c).fix()
}

fun FlowableK.Companion.monadFlat(): FlowableKMonadInstance = FlowableKMonadInstanceImplicits.instance()

fun FlowableK.Companion.monadConcat(): FlowableKMonadInstance = object : FlowableKMonadInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMapIn(context) { f(it).fix() }
}

fun FlowableK.Companion.monadSwitch(): FlowableKMonadInstance = object : FlowableKMonadErrorInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMapIn(context) { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKMonadErrorInstance :
        FlowableKApplicativeErrorInstance,
        FlowableKMonadInstance,
        MonadError<ForFlowableK, Throwable> {
    override fun <A, B> ap(fa: FlowableKOf<A>, ff: FlowableKOf<(A) -> B>): FlowableK<B> =
            super<FlowableKMonadInstance>.ap(fa, ff)

    override fun <A, B> map(fa: FlowableKOf<A>, f: (A) -> B): FlowableK<B> =
            super<FlowableKMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): FlowableK<A> =
            super<FlowableKMonadInstance>.pure(a)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForFlowableK, Throwable, *>.() -> B): FlowableK<B> =
            AsyncContinuation.binding(::identity, FlowableK.async(), context, c).fix()
}

fun FlowableK.Companion.monadErrorFlat(): FlowableKMonadErrorInstance = FlowableKMonadErrorInstanceImplicits.instance()

fun FlowableK.Companion.monadErrorConcat(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMapIn(context) { f(it).fix() }
}

fun FlowableK.Companion.monadErrorSwitch(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMapIn(context) { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKMonadSuspendInstance :
        FlowableKMonadErrorInstance,
        MonadSuspend<ForFlowableK, Throwable> {
    fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER

    override fun catch(catch: Throwable): Throwable =
            catch

    override fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
            FlowableK.defer(fa)
}

fun FlowableK.Companion.monadSuspendFlat(): FlowableKMonadSuspendInstance = FlowableKMonadSuspendInstanceImplicits.instance()

fun FlowableK.Companion.monadSuspendConcat(): FlowableKMonadSuspendInstance = object : FlowableKMonadSuspendInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMapIn(context) { f(it).fix() }
}

fun FlowableK.Companion.monadSuspendSwitch(): FlowableKMonadSuspendInstance = object : FlowableKMonadSuspendInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMapIn(context) { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKAsyncInstance :
        FlowableKMonadSuspendInstance,
        Async<ForFlowableK, Throwable> {
    override fun <A> async(fa: Proc<A>): FlowableK<A> =
            FlowableK.async(fa, BS())

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForFlowableK, *>.() -> B): FlowableK<B> =
            super<FlowableKMonadSuspendInstance>.binding(context, c)

    override fun <B> bindingCatch(context: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForFlowableK, Throwable, *>.() -> B): FlowableK<B> =
            super<FlowableKMonadSuspendInstance>.bindingCatch(context, catch, c)
}

fun FlowableK.Companion.asyncFlat(): FlowableKAsyncInstance = FlowableKAsyncInstanceImplicits.instance()

fun FlowableK.Companion.asyncConcat(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMapIn(context) { f(it).fix() }
}

fun FlowableK.Companion.asyncSwitch(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMapIn(context) { f(it).fix() }
}

@instance(FlowableK::class)
interface FlowableKEffectInstance :
        FlowableKAsyncInstance,
        Effect<ForFlowableK, Throwable> {
    override fun <A> runAsync(fa: FlowableKOf<A>, cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
            fa.fix().runAsync(cb)
}

fun FlowableK.Companion.effectFlat(): FlowableKEffectInstance = FlowableKEffectInstanceImplicits.instance()

fun FlowableK.Companion.effectConcat(): FlowableKEffectInstance = object : FlowableKEffectInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().concatMapIn(context) { f(it).fix() }
}

fun FlowableK.Companion.effectSwitch(): FlowableKEffectInstance = object : FlowableKEffectInstance {
    override fun <A, B> flatMap(fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMap { f(it).fix() }

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: FlowableKOf<A>, f: (A) -> FlowableKOf<B>): FlowableK<B> =
            fa.fix().switchMapIn(context) { f(it).fix() }
}
