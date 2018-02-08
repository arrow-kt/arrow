package arrow.instances

import arrow.*
import arrow.core.*
import arrow.typeclasses.*

@instance(Eval::class)
interface EvalFunctorInstance : Functor<ForEval> {
    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.extract().map(f)
}

@instance(Eval::class)
interface EvalApplicativeInstance : Applicative<ForEval> {
    override fun <A, B> ap(fa: EvalOf<A>, ff: EvalOf<kotlin.Function1<A, B>>): Eval<B> =
            fa.extract().ap(ff)

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalMonadInstance : Monad<ForEval> {
    override fun <A, B> ap(fa: EvalOf<A>, ff: EvalOf<kotlin.Function1<A, B>>): Eval<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: EvalOf<A>, f: kotlin.Function1<A, EvalOf<B>>): Eval<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)
}

@instance(Eval::class)
interface EvalComonadInstance : Comonad<ForEval> {
    override fun <A, B> coflatMap(fa: EvalOf<A>, f: kotlin.Function1<EvalOf<A>, B>): Eval<B> =
            fa.extract().coflatMap(f)

    override fun <A> extract(fa: EvalOf<A>): A =
            fa.extract().extract()

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.extract().map(f)
}

@instance(Eval::class)
interface EvalBimonadInstance : Bimonad<ForEval> {
    override fun <A, B> ap(fa: EvalOf<A>, ff: EvalOf<kotlin.Function1<A, B>>): Eval<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: EvalOf<A>, f: kotlin.Function1<A, EvalOf<B>>): Eval<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
            Eval.tailRecM(a, f)

    override fun <A, B> map(fa: EvalOf<A>, f: kotlin.Function1<A, B>): Eval<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): Eval<A> =
            Eval.pure(a)

    override fun <A, B> coflatMap(fa: EvalOf<A>, f: kotlin.Function1<EvalOf<A>, B>): Eval<B> =
            fa.extract().coflatMap(f)

    override fun <A> extract(fa: EvalOf<A>): A =
            fa.extract().extract()
}