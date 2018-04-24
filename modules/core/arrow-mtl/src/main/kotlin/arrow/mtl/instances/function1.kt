package arrow.mtl.instances

import arrow.Kind
import arrow.core.Function1
import arrow.core.Function1PartialOf
import arrow.core.fix
import arrow.instance
import arrow.instances.Function1MonadInstance
import arrow.mtl.typeclasses.MonadReader

@instance(Function1::class)
interface Function1MonadReaderInstance<I> : Function1MonadInstance<I>, MonadReader<Function1PartialOf<I>, I> {

  override fun ask(): Function1<I, I> = Function1.ask()

  override fun <A> Kind<Function1PartialOf<I>, A>.local(f: (I) -> I): Function1<I, A> = fix().local(f)
}
