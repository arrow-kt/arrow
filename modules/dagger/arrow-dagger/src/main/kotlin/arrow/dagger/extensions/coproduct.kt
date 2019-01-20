package arrow.dagger.extensions

import arrow.data.CoproductPartialOf
import arrow.data.extensions.CoproductComonad
import arrow.data.extensions.CoproductFoldable
import arrow.data.extensions.CoproductFunctor
import arrow.data.extensions.CoproductTraverse
import arrow.typeclasses.Comonad
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Traverse
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class CoproductInstances<F, G> {

  @Provides
  fun coproductFunctor(ev: DaggerCoproductFunctor<F, G>): Functor<CoproductPartialOf<F, G>> = ev

  @Provides
  fun coproductComonad(ev: DaggerCoproductComonad<F, G>): Comonad<CoproductPartialOf<F, G>> = ev

  @Provides
  fun coproductFoldable(ev: DaggerCoproductFoldable<F, G>): Foldable<CoproductPartialOf<F, G>> = ev

  @Provides
  fun coproductTraverse(ev: DaggerCoproductTraverse<F, G>): Traverse<CoproductPartialOf<F, G>> = ev

}

class DaggerCoproductFunctor<F, G> @Inject constructor(val FF: Functor<F>, val FG: Functor<G>) : CoproductFunctor<F, G> {
  override fun FF(): Functor<F> = FF
  override fun FG(): Functor<G> = FG
}

class DaggerCoproductComonad<F, G> @Inject constructor(val CF: Comonad<F>, val CG: Comonad<G>) : CoproductComonad<F, G> {
  override fun CF(): Comonad<F> = CF
  override fun CG(): Comonad<G> = CG
}

class DaggerCoproductFoldable<F, G> @Inject constructor(val FF: Foldable<F>, val FG: Foldable<G>) : CoproductFoldable<F, G> {
  override fun FF(): Foldable<F> = FF
  override fun FG(): Foldable<G> = FG
}

class DaggerCoproductTraverse<F, G> @Inject constructor(val TF: Traverse<F>, val TG: Traverse<G>) : CoproductTraverse<F, G> {
  override fun TF(): Traverse<F> = TF
  override fun TG(): Traverse<G> = TG
}