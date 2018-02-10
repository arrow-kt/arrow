package arrow.mtl.instances

import arrow.data.Function1
import arrow.data.Function1Of
import arrow.data.Function1PartialOf
import arrow.data.fix
import arrow.instance
import arrow.instances.Function1MonadInstance
import arrow.mtl.MonadReader

@instance(Function1::class)
interface Function1MonadReaderInstance<I> : Function1MonadInstance<I>, MonadReader<Function1PartialOf<I>, I> {

    override fun ask(): Function1<I, I> = Function1.ask()

    override fun <A> local(f: (I) -> I, fa: Function1Of<I, A>): Function1<I, A> = fa.fix().local(f)
}
