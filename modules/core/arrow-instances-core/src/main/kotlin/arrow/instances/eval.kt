package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance
interface EvalFunctorInstance : Functor<ForEval> {
  override fun <A, B> Kind<ForEval, A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)
}

@instance
interface EvalApplicativeInstance : Applicative<ForEval> {
  override fun <A, B> Kind<ForEval, A>.ap(ff: Kind<ForEval, (A) -> B>): Eval<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForEval, A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)

  override fun <A> just(a: A): Eval<A> =
    Eval.just(a)
}

@instance
interface EvalMonadInstance : Monad<ForEval> {
  override fun <A, B> Kind<ForEval, A>.ap(ff: Kind<ForEval, (A) -> B>): Eval<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForEval, A>.flatMap(f: (A) -> Kind<ForEval, B>): Eval<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
    Eval.tailRecM(a, f)

  override fun <A, B> Kind<ForEval, A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)

  override fun <A> just(a: A): Eval<A> =
    Eval.just(a)
}

@instance
interface EvalComonadInstance : Comonad<ForEval> {
  override fun <A, B> Kind<ForEval, A>.coflatMap(f: (Kind<ForEval, A>) -> B): Eval<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForEval, A>.extract(): A =
    fix().extract()

  override fun <A, B> Kind<ForEval, A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)
}

@instance
interface EvalBimonadInstance : Bimonad<ForEval> {
  override fun <A, B> Kind<ForEval, A>.ap(ff: Kind<ForEval, (A) -> B>): Eval<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForEval, A>.flatMap(f: (A) -> Kind<ForEval, B>): Eval<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
    Eval.tailRecM(a, f)

  override fun <A, B> Kind<ForEval, A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)

  override fun <A> just(a: A): Eval<A> =
    Eval.just(a)

  override fun <A, B> Kind<ForEval, A>.coflatMap(f: (Kind<ForEval, A>) -> B): Eval<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForEval, A>.extract(): A =
    fix().extract()
}

object EvalContext : EvalBimonadInstance

infix fun <L> ForEval.Companion.extensions(f: EvalContext.() -> L): L =
  f(EvalContext)