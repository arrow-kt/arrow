package arrow.effects.extensions

import arrow.Kind
import arrow.data.Kleisli
import arrow.data.KleisliOf
import arrow.data.KleisliPartialOf
import arrow.data.extensions.KleisliMonadError
import arrow.data.run
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface KleisliBracket<F, R, E> : Bracket<KleisliPartialOf<F, R>, E>, KleisliMonadError<F, R, E> {

  fun BF(): Bracket<F, E>

  override fun ME(): MonadError<F, E> = BF()

  override fun <A, B> Kind<KleisliPartialOf<F, R>, A>.bracketCase(
    release: (A, ExitCase<E>) -> Kind<KleisliPartialOf<F, R>, Unit>,
    use: (A) -> Kind<KleisliPartialOf<F, R>, B>
  ): Kleisli<F, R, B> =
    BF().run {
      Kleisli { r ->
        this@bracketCase.run(r).bracketCase({ a, br ->
          release(a, br).run(r)
        }) { a ->
          use(a).run(r)
        }
      }
    }

  override fun <A> Kind<KleisliPartialOf<F, R>, A>.uncancelable(): Kleisli<F, R, A> =
    Kleisli { r -> BF().run { this@uncancelable.run(r).uncancelable() } }
}

//TODO fix stack safety issue. AsyncLaws#stack safety over repeated attempts fails.
internal interface KleisliMonadDefer<F, R> : MonadDefer<KleisliPartialOf<F, R>>, KleisliBracket<F, R, Throwable> {

  fun MDF(): MonadDefer<F>

  override fun BF(): Bracket<F, Throwable> = MDF()

  override fun <A> defer(fa: () -> KleisliOf<F, R, A>): Kleisli<F, R, A> = MDF().run {
    Kleisli { r -> defer { fa().run(r) } }
  }

}

internal interface KleisliAsync<F, R> : Async<KleisliPartialOf<F, R>>, KleisliMonadDefer<F, R> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): Kleisli<F, R, A> =
    Kleisli.liftF(ASF().async(fa))

  override fun <A> asyncF(k: ProcF<KleisliPartialOf<F, R>, A>): Kleisli<F, R, A> =
    Kleisli { r -> ASF().asyncF { cb -> k(cb).run(r) } }

  override fun <A> KleisliOf<F, R, A>.continueOn(ctx: CoroutineContext): Kleisli<F, R, A> = ASF().run {
    Kleisli { r -> run(r).continueOn(ctx) }
  }

}
