package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Option
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.fix
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter

@extension
interface NonEmptyListFunctorFilter : FunctorFilter<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.mapFilter(f: (A) -> Option<B>): NonEmptyList<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}
