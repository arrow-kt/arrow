package arrow.effects.rx2.extensions

import arrow.core.Either
import arrow.effects.rx2.ForSingleK
import arrow.effects.rx2.SingleK
import arrow.effects.rx2.SingleKOf
import arrow.effects.rx2.fix
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@extension
interface SingleKFunctor : Functor<ForSingleK> {
  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)
}

@extension
interface SingleKApplicative : Applicative<ForSingleK> {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)
}

@extension
interface SingleKMonad : Monad<ForSingleK> {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    fix().flatMap(f)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: Function1<A, SingleKOf<Either<A, B>>>): SingleK<B> =
    SingleK.tailRecM(a, f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)
}

@extension
interface SingleKApplicativeError :
  ApplicativeError<ForSingleK, Throwable>,
  SingleKApplicative{
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadError :
  MonadError<ForSingleK, Throwable>,
  SingleKMonad{
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadThrow: MonadThrow<ForSingleK>, SingleKMonadError

@extension
interface SingleKBracket: Bracket<ForSingleK, Throwable>, SingleKMonadThrow {
  override fun <A, B> SingleKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> SingleKOf<Unit>, use: (A) -> SingleKOf<B>): SingleK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface SingleKMonadDefer: MonadDefer<ForSingleK>, SingleKBracket {
  override fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
    SingleK.defer(fa)
}

@extension
interface SingleKAsync :
  Async<ForSingleK>,
  SingleKMonadDefer{
  override fun <A> async(fa: Proc<A>): SingleK<A> =
    SingleK.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForSingleK, A>): SingleK<A> =
    SingleK.asyncF { _, cb -> k(cb) }

  override fun <A> SingleKOf<A>.continueOn(ctx: CoroutineContext): SingleK<A> =
    fix().continueOn(ctx)
}

@extension
interface SingleKEffect :
  Effect<ForSingleK>,
  SingleKAsync{
  override fun <A> SingleKOf<A>.runAsync(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Unit> =
    fix().runAsync(cb)
}

@extension
interface SingleKConcurrentEffect: ConcurrentEffect<ForSingleK>, SingleKEffect {
  override fun <A> SingleKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Disposable> =
    fix().runAsyncCancellable(cb)
}
