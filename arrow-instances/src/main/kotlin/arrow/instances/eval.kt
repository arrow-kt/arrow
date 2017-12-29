package arrow.instances

import arrow.*
import arrow.core.*

@instance(Eval::class)
interface EvalFunctorInstance : Functor<EvalHK> {
    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.ev().map(f)
}

@instance(Eval::class)
interface EvalApplicativeInstance : Applicative<EvalHK> {
    override fun <A, B> ap(fa: EvalKind<A>, ff: EvalKind<kotlin.Function1<A, B>>): Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalMonadInstance : Monad<EvalHK> {
    override fun <A, B> ap(fa: EvalKind<A>, ff: EvalKind<kotlin.Function1<A, B>>): Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: EvalKind<A>, f: kotlin.Function1<A, EvalKind<B>>): Eval<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalKind<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalComonadInstance : Comonad<EvalHK> {
    override fun <A, B> coflatMap(fa: EvalKind<A>, f: kotlin.Function1<EvalKind<A>, B>): Eval<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: EvalKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.ev().map(f)
}

@instance(Eval::class)
interface EvalBimonadInstance : Bimonad<EvalHK> {
    override fun <A, B> ap(fa: EvalKind<A>, ff: EvalKind<kotlin.Function1<A, B>>): Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: EvalKind<A>, f: kotlin.Function1<A, EvalKind<B>>): Eval<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalKind<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalKind<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)

    override fun <A, B> coflatMap(fa: EvalKind<A>, f: kotlin.Function1<EvalKind<A>, B>): Eval<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: EvalKind<A>): A =
            fa.ev().extract()
}