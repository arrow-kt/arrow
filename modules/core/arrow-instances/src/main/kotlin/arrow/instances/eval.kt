package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*
import arrow.typeclasses.continuations.BindingContinuation
import arrow.typeclasses.continuations.EvalContinuation
import kotlin.coroutines.experimental.CoroutineContext

@instance(Eval::class)
interface EvalFunctorInstance : Functor<ForEval> {
    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.fix().map(f)
}

@instance(Eval::class)
interface EvalApplicativeInstance : Applicative<ForEval> {
    override fun <A, B> ap(fa: EvalOf<A>, ff: EvalOf<kotlin.Function1<A, B>>): Eval<B> =
            fa.fix().ap(ff)

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalMonadInstance : Monad<ForEval> {
    override fun <A, B> ap(fa: EvalOf<A>, ff: EvalOf<kotlin.Function1<A, B>>): Eval<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: EvalOf<A>, f: kotlin.Function1<A, EvalOf<B>>): Eval<B> =
            fa.fix().flatMap(f)

    override fun <A, B> flatMapIn(context: CoroutineContext, fa: Kind<ForEval, A>, f: (A) -> Kind<ForEval, B>): Eval<B> =
            fa.fix().flatMapIn(context, f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)

    override fun <B> binding(context: CoroutineContext, c: suspend BindingContinuation<ForEval, *>.() -> B): Kind<ForEval, B> =
            EvalContinuation.binding(this, context, c)
}

@instance(Eval::class)
interface EvalComonadInstance : Comonad<ForEval> {
    override fun <A, B> coflatMap(fa: EvalOf<A>, f: kotlin.Function1<EvalOf<A>, B>): Eval<B> =
            fa.fix().coflatMap(f)

    override fun <A> extract(fa: EvalOf<A>): A =
            fa.fix().extract()

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.fix().map(f)
}

@instance(Eval::class)
interface EvalBimonadInstance : Bimonad<ForEval> {
    override fun <A, B> ap(fa: EvalOf<A>, ff: EvalOf<kotlin.Function1<A, B>>): Eval<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: EvalOf<A>, f: kotlin.Function1<A, EvalOf<B>>): Eval<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)

    override fun <A, B> coflatMap(fa: EvalOf<A>, f: kotlin.Function1<EvalOf<A>, B>): Eval<B> =
            fa.fix().coflatMap(f)

    override fun <A> extract(fa: EvalOf<A>): A =
            fa.fix().extract()
}