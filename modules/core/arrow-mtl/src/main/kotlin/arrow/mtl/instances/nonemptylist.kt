package arrow.mtl.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.ForNonEmptyList
import arrow.data.NonEmptyList
import arrow.data.fix
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter

@extension
interface NonEmptyListFunctorFilterInstance : FunctorFilter<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.mapFilter(f: (A) -> Option<B>): NonEmptyList<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}