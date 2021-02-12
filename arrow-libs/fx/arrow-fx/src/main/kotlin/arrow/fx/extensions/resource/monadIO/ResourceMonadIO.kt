package arrow.fx.extensions.resource.monadIO

import arrow.fx.IO
import arrow.fx.IODeprecation
import arrow.fx.Resource
import arrow.fx.Resource.Companion
import arrow.fx.extensions.ResourceMonadIO
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.MonadIO
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("liftIO")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, E, A> IO<A>.liftIO(FIO: MonadIO<F>, BR: Bracket<F, E>): Resource<F, E, A> =
    arrow.fx.Resource.monadIO<F, E>(FIO, BR).run {
  this@liftIO.liftIO<A>() as arrow.fx.Resource<F, E, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, E> Companion.monadIO(FIO: MonadIO<F>, BR: Bracket<F, E>): ResourceMonadIO<F, E> =
    object : arrow.fx.extensions.ResourceMonadIO<F, E> { override fun FIO():
    arrow.fx.typeclasses.MonadIO<F> = FIO

  override fun BR(): arrow.fx.typeclasses.Bracket<F, E> = BR }
