package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Function1
import arrow.core.Function1PartialOf
import arrow.core.extensions.Function1Monad
import arrow.core.fix
import arrow.extension
import arrow.mtl.typeclasses.MonadReader

@extension
interface Function1MonadReader<I> : MonadReader<Function1PartialOf<I>, I>, Function1Monad<I> {

  override fun ask(): Function1<I, I> = Function1.ask()

  override fun <A> Kind<Function1PartialOf<I>, A>.local(f: (I) -> I): Function1<I, A> = fix().local(f)
}
