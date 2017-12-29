package arrow.mtl.instances

import arrow.*
import arrow.data.Function1
import arrow.instances.Function1MonadInstance

@instance(Function1::class)
interface Function1MonadReaderInstance<I> : Function1MonadInstance<I>, MonadReader<Function1KindPartial<I>, I> {

    override fun ask(): Function1<I, I> = Function1.ask()

    override fun <A> local(f: (I) -> I, fa: Function1Kind<I, A>): Function1<I, A> = fa.ev().local(f)
}