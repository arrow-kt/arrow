package arrow.core.extensions

import arrow.core.*
import arrow.extension
import arrow.typeclasses.*

@extension
interface EvalFunctorInstance : Functor<ForEval> {
  override fun <A, B> EvalOf<A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)
}

@extension
interface EvalApplicativeInstance : Applicative<ForEval> {
  override fun <A, B> EvalOf<A>.ap(ff: EvalOf<(A) -> B>): Eval<B> =
    fix().ap(ff)

  override fun <A, B> EvalOf<A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)

  override fun <A> just(a: A): Eval<A> =
    Eval.just(a)
}

@extension
interface EvalMonadInstance : Monad<ForEval> {
  override fun <A, B> EvalOf<A>.ap(ff: EvalOf<(A) -> B>): Eval<B> =
    fix().ap(ff)

  override fun <A, B> EvalOf<A>.flatMap(f: (A) -> EvalOf<B>): Eval<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
    Eval.tailRecM(a, f)

  override fun <A, B> EvalOf<A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)

  override fun <A> just(a: A): Eval<A> =
    Eval.just(a)
}

@extension
interface EvalComonadInstance : Comonad<ForEval> {
  override fun <A, B> EvalOf<A>.coflatMap(f: (EvalOf<A>) -> B): Eval<B> =
    fix().coflatMap(f)

  override fun <A> EvalOf<A>.extract(): A =
    fix().extract()

  override fun <A, B> EvalOf<A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)
}

@extension
interface EvalBimonadInstance : Bimonad<ForEval> {
  override fun <A, B> EvalOf<A>.ap(ff: EvalOf<(A) -> B>): Eval<B> =
    fix().ap(ff)

  override fun <A, B> EvalOf<A>.flatMap(f: (A) -> EvalOf<B>): Eval<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, EvalOf<Either<A, B>>>): Eval<B> =
    Eval.tailRecM(a, f)

  override fun <A, B> EvalOf<A>.map(f: (A) -> B): Eval<B> =
    fix().map(f)

  override fun <A> just(a: A): Eval<A> =
    Eval.just(a)

  override fun <A, B> EvalOf<A>.coflatMap(f: (EvalOf<A>) -> B): Eval<B> =
    fix().coflatMap(f)

  override fun <A> EvalOf<A>.extract(): A =
    fix().extract()
}
