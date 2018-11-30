package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.data.EitherT
import arrow.data.EitherTPartialOf
import arrow.data.fix
import arrow.data.value
import arrow.effects.Ref
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.MonadDefer

interface EitherTMonadDeferInstance<F, L> : MonadDefer<EitherTPartialOf<F, L>> {

  fun MDF(): MonadDefer<F>

  override fun <A> just(a: A): EitherT<F, L, A> =
    EitherT.just(MDF(), a)

  override fun <A> Kind<EitherTPartialOf<F, L>, A>.handleErrorWith(f: (Throwable) -> Kind<EitherTPartialOf<F, L>, A>): EitherT<F, L, A> =
    MDF().run {
      EitherT(value().handleErrorWith(f.andThen { it.value() }))
    }

  override fun <A> raiseError(e: Throwable): EitherT<F, L, A> =
    EitherT.liftF(MDF(), MDF().raiseError(e))

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.bracketCase(
    release: (A, ExitCase<L>) -> Kind<EitherTPartialOf<F, L>, Unit>,
    use: (A) -> Kind<EitherTPartialOf<F, L>, B>
  ): EitherT<F, L, B> =
    EitherT.liftF<F, L, Ref<F, Option<L>>>(MDF(), Ref.of<F, Option<L>>(None, MDF())).flatMap(MDF()) { ref ->
      EitherT(value().bracketCase<Either<L, A>, Either<L, B>>(
        use = { a: A ->
          when (a) {
            is Either.Right<*> -> use((a as Either.Right<A>).b).value()
            else -> MDF().just(a as Either.Left<B>)
          }
        },
        release = { a: A, exitCase: ExitCase<L> ->
          when {
            a is Either.Left<*> -> MDF().just(Unit) //Nothing to release
            a is Either.Right<*> && exitCase is ExitCase.Completed ->
              release((a as Either.Right<A>).b, ExitCase.Completed).value().flatMap {
                when (it) {
                  is Either.Left<*> -> ref.set(Some((it as Either.Left<L>).a))
                  else -> MDF().just(Unit)
                }
              }
            else -> release((a as Either.Right<A>).b, exitCase).value().void()
          }
        }
      ).flatMap<Either<L, B>> {
        case r @ Right(_) => ref.get.map(_.fold(r: Either[L, B])(Either.left[L, B]))
        case l @ Left(_) => F.pure(l)
      })
    }
}

fun <F, L> EitherT.Companion.monadDefer(FF: MonadDefer<F>): MonadDefer<EitherTPartialOf<F, L>> =
  object : EitherTMonadDeferInstance<F, L> {
    override fun MDF(): MonadDefer<F> = FF
  }