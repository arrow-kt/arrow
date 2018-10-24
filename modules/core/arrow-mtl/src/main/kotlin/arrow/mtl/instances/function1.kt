package arrow.mtl.instances

import arrow.Kind
import arrow.core.Function1
import arrow.core.Function1PartialOf
import arrow.core.fix
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.instances.Function1MonadInstance
import arrow.mtl.typeclasses.MonadReader

@extension
interface Function1MonadReaderInstance<I> : MonadReader<Function1PartialOf<I>, I>, Function1MonadInstance<I> {

  override fun ask(): Function1<I, I> = Function1.ask()

  override fun <A> Kind<Function1PartialOf<I>, A>.local(f: (I) -> I): Function1<I, A> = fix().local(f)
}

class Function1MtlContext<A> : Function1MonadReaderInstance<A>

class Function1MtlContextPartiallyApplied<L> {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <A> extensions(f: Function1MtlContext<L>.() -> A): A =
    f(Function1MtlContext())
}

fun <L> ForForFunction1(): Function1MtlContextPartiallyApplied<L> =
  Function1MtlContextPartiallyApplied()