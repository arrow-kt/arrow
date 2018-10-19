package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*

@extension
interface Function0FunctorInstance : Functor<ForFunction0> {
  override fun <A, B> Kind<ForFunction0, A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)
}

@extension
interface Function0ApplicativeInstance : Applicative<ForFunction0> {
  override fun <A, B> Kind<ForFunction0, A>.ap(ff: Kind<ForFunction0, (A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFunction0, A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)
}

@extension
interface Function0MonadInstance : Monad<ForFunction0> {
  override fun <A, B> Kind<ForFunction0, A>.ap(ff: Kind<ForFunction0, (A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFunction0, A>.flatMap(f: (A) -> Kind<ForFunction0, B>): Function0<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
    Function0.tailRecM(a, f)

  override fun <A, B> Kind<ForFunction0, A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)
}

@extension
interface Function0ComonadInstance : Comonad<ForFunction0> {
  override fun <A, B> Kind<ForFunction0, A>.coflatMap(f: (Kind<ForFunction0, A>) -> B): Function0<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForFunction0, A>.extract(): A =
    fix().extract()

  override fun <A, B> Kind<ForFunction0, A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)
}

@extension
interface Function0BimonadInstance : Bimonad<ForFunction0> {
  override fun <A, B> Kind<ForFunction0, A>.ap(ff: Kind<ForFunction0, (A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFunction0, A>.flatMap(f: (A) -> Kind<ForFunction0, B>): Function0<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
    Function0.tailRecM(a, f)

  override fun <A, B> Kind<ForFunction0, A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)

  override fun <A, B> Kind<ForFunction0, A>.coflatMap(f: (Kind<ForFunction0, A>) -> B): Function0<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForFunction0, A>.extract(): A =
    fix().extract()
}

object Function0Context : Function0BimonadInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <L> ForFunction0.Companion.extensions(f: Function0Context.() -> L): L =
  f(Function0Context)