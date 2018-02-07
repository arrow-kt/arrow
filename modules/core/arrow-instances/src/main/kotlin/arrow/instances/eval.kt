package arrow.instances

import arrow.*
import arrow.core.*
import arrow.typeclasses.*

@instance(Eval::class)
interface EvalFunctorInstance : Functor<ForEval> {
    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.reify().map(f)
}

@instance(Eval::class)
interface EvalApplicativeInstance : Applicative<ForEval> {
    override fun <A, B> ap(fa: EvalKind<A>, ff: EvalKind<kotlin.Function1<A, B>>): Eval<B> =
            fa.reify().ap(ff)

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.reify().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalMonadInstance : Monad<ForEval> {
    override fun <A, B> ap(fa: EvalKind<A>, ff: EvalKind<kotlin.Function1<A, B>>): Eval<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: EvalKind<A>, f: kotlin.Function1<A, EvalKind<B>>): Eval<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalKind<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.reify().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalComonadInstance : Comonad<ForEval> {
    override fun <A, B> coflatMap(fa: EvalKind<A>, f: kotlin.Function1<EvalKind<A>, B>): Eval<B> =
            fa.reify().coflatMap(f)

    override fun <A> extract(fa: EvalKind<A>): A =
            fa.reify().extract()

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.reify().map(f)
}

@instance(Eval::class)
interface EvalBimonadInstance : Bimonad<ForEval> {
    override fun <A, B> ap(fa: EvalKind<A>, ff: EvalKind<kotlin.Function1<A, B>>): Eval<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: EvalKind<A>, f: kotlin.Function1<A, EvalKind<B>>): Eval<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalKind<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.reify().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)

    override fun <A, B> coflatMap(fa: EvalKind<A>, f: kotlin.Function1<EvalKind<A>, B>): Eval<B> =
            fa.reify().coflatMap(f)

    override fun <A> extract(fa: EvalKind<A>): A =
            fa.reify().extract()
}