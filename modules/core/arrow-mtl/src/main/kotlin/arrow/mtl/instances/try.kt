package arrow.mtl.instances

import arrow.Kind
import arrow.core.ForTry
import arrow.core.Option
import arrow.core.Try
import arrow.core.fix
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter

@extension
interface TryFunctorFilterInstance : FunctorFilter<ForTry> {

  override fun <A, B> Kind<ForTry, A>.mapFilter(f: (A) -> Option<B>): Try<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}