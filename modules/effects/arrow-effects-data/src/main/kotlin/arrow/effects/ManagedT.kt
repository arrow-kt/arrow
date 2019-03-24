package arrow.effects

import arrow.Kind
import arrow.core.andThen
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.higherkind
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@higherkind
interface ManagedT<F, E, R> : ManagedTOf<F, E, R> {

  val BR: Bracket<F, E>

  operator fun <A> invoke(use: (R) -> Kind<F, A>): Kind<F, A>

  fun <R2> map(f: (R) -> R2): ManagedT<F, E, R2> = flatMap(f andThen { just(it, BR) })

  fun <R2> ap(ff: ManagedT<F, E, (R) -> R2>): ManagedT<F, E, R2> = flatMap { res -> ff.map { it(res) } }

  fun <R2> flatMap(f: (R) -> ManagedT<F, E, R2>): ManagedT<F, E, R2> = object : ManagedT<F, E, R2> {
    override fun <A> invoke(use: (R2) -> Kind<F, A>): Kind<F, A> = this@ManagedT { r ->
      f(r).invoke { r2 ->
        use(r2)
      }
    }

    override val BR: Bracket<F, E> = this@ManagedT.BR
  }

  fun combine(other: ManagedT<F, E, R>, SR: Semigroup<R>): ManagedT<F, E, R> = flatMap { r ->
    other.map { r2 -> SR.run { r.combine(r2) } }
  }

  companion object {
    fun <F, E, R> Kind<F, R>.liftF(BR: Bracket<F, E>): ManagedT<F, E, R> = ManagedT({ this }, { _, _ -> BR.unit() }, BR)

    fun <F, E, R> empty(MR: Monoid<R>, BR: Bracket<F, E>): ManagedT<F, E, R> = just(MR.empty(), BR)

    fun <F, E, R> just(r: R, BR: Bracket<F, E>) = ManagedT({ BR.just(r) }, { _, _ -> BR.just(Unit) }, BR)

    operator fun <F, E, R> invoke(
      acquire: () -> Kind<F, R>,
      release: (R, ExitCase<E>) -> Kind<F, Unit>,
      BR: Bracket<F, E>
    ): ManagedT<F, E, R> = object : ManagedT<F, E, R> {
      override operator fun <A> invoke(use: (R) -> Kind<F, A>): Kind<F, A> =
        BR.run { acquire().bracketCase(release, use) }
      
      override val BR: Bracket<F, E> = BR
    }

    operator fun <F, E, R> invoke(
      acquire: () -> Kind<F, R>,
      release: (R) -> Kind<F, Unit>,
      BR: Bracket<F, E>
    ): ManagedT<F, E, R> = invoke(acquire, { r, _ -> release(r) }, BR)
  }
}