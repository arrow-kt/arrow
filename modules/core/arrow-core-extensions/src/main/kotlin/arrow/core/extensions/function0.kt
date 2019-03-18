package arrow.core.extensions

import arrow.Kind
import arrow.core.*
import arrow.core.select as fun0Select
import arrow.core.extensions.function0.monad.monad
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.suspended.monad.Fx

@extension
interface Function0Semigroup<A> : Semigroup<Function0<A>> {
  fun SA(): Semigroup<A>

  override fun Function0<A>.combine(b: Function0<A>): Function0<A> =
    { SA().run { invoke().combine(b.invoke()) } }.k()
}

@extension
interface Function0Monoid<A> : Monoid<Function0<A>>, Function0Semigroup<A> {
  fun MA(): Monoid<A>

  override fun SA() = MA()

  override fun empty(): Function0<A> =
    { MA().run { empty() } }.k()
}

@extension
interface Function0Functor : Functor<ForFunction0> {
  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)
}

@extension
interface Function0Applicative : Applicative<ForFunction0> {
  override fun <A, B> Function0Of<A>.ap(ff: Function0Of<(A) -> B>): Function0<B> =
    fix().ap(ff)

  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)

  override fun <A> just(a: A): Function0<A> =
    Function0.just(a)
}

@extension
interface Function0Selective : Selective<ForFunction0>, Function0Applicative {
  override fun <A, B> Function0Of<Either<A, B>>.select(f: Kind<ForFunction0, (A) -> B>): Kind<ForFunction0, B> =
    fix().fun0Select(f)
}

@extension
interface Function0Monad : Monad<ForFunction0> {
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

  override fun <A, B> Function0Of<Either<A, B>>.select(f: Kind<ForFunction0, (A) -> B>): Kind<ForFunction0, B> =
    fix().fun0Select(f)
}

@extension
interface Function0Comonad : Comonad<ForFunction0> {
  override fun <A, B> Function0Of<A>.coflatMap(f: (Function0Of<A>) -> B): Function0<B> =
    fix().coflatMap(f)

  override fun <A> Function0Of<A>.extract(): A =
    fix().extract()

  override fun <A, B> Function0Of<A>.map(f: (A) -> B): Function0<B> =
    fix().map(f)
}

@extension
interface Function0Bimonad : Bimonad<ForFunction0> {
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

@extension
interface Function0Fx<A> : Fx<ForFunction0> {
  override fun monad(): Monad<ForFunction0> = Function0.monad()
}