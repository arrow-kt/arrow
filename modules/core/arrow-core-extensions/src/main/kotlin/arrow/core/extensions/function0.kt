package arrow.core.extensions

import arrow.core.*
import arrow.extension
import arrow.typeclasses.*

@extension
interface Function0SemigroupInstance<A> : Semigroup<Function0<A>> {
  fun SA(): Semigroup<A>

  override fun Function0<A>.combine(b: Function0<A>): Function0<A> =
    { SA().run { invoke().combine(b.invoke()) } }.k()
}

@extension
interface Function0MonoidInstance<A> : Monoid<Function0<A>>, Function0SemigroupInstance<A> {
  fun MA(): Monoid<A>

  override fun SA() = MA()

  override fun empty(): Function0<A> =
    { MA().run { empty() } }.k()
}

@extension
interface Function0FunctorInstance : Functor<ForFunction0> {
  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)
}

@extension
interface Function0ApplicativeInstance : Applicative<ForFunction0> {
  override fun <A, B> Function0Of<A>.ap(ff: Function0Of<(A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)
}

@extension
interface Function0MonadInstance : Monad<ForFunction0> {
  override fun <A, B> Function0Of<A>.ap(ff: Function0Of<(A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Function0Of<A>.flatMap(f: (A) -> Function0Of<B>): Function0<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
    Function0.tailRecM(a, f)

  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)
}

@extension
interface Function0ComonadInstance : Comonad<ForFunction0> {
  override fun <A, B> Function0Of<A>.coflatMap(f: (Function0Of<A>) -> B): Function0<B> =
    fix().coflatMap(f)

  override fun <A> Function0Of<A>.extract(): A =
    fix().extract()

  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)
}

@extension
interface Function0BimonadInstance : Bimonad<ForFunction0> {
  override fun <A, B> Function0Of<A>.ap(ff: Function0Of<(A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Function0Of<A>.flatMap(f: (A) -> Function0Of<B>): Function0<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, Function0Of<Either<A, B>>>): Function0<B> =
    Function0.tailRecM(a, f)

  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)

  override fun <A, B> Function0Of<A>.coflatMap(f: (Function0Of<A>) -> B): Function0<B> =
    fix().coflatMap(f)

  override fun <A> Function0Of<A>.extract(): A =
    fix().extract()
}
