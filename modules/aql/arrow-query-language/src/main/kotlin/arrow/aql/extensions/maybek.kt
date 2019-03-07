package arrow.aql.extensions

import arrow.aql.*
import arrow.effects.rx2.ForMaybeK
import arrow.effects.rx2.MaybeK
import arrow.effects.rx2.extensions.maybek.applicative.applicative
import arrow.effects.rx2.extensions.maybek.foldable.foldable
import arrow.effects.rx2.extensions.maybek.functor.functor
import arrow.effects.rx2.extensions.maybek.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface MaybeKFrom : From<ForMaybeK> {
  override fun applicative(): Applicative<ForMaybeK> = MaybeK.applicative()
}

@extension
interface MaybeKSelect : Select<ForMaybeK> {
  override fun functor(): Functor<ForMaybeK> = MaybeK.functor()
}

@extension
interface MaybeKWhere : Where<ForMaybeK> {
  override fun functorFilter(): FunctorFilter<ForMaybeK> = MaybeK.functorFilter()
}

@extension
interface MaybeKGroupBy : GroupBy<ForMaybeK> {
  override fun foldable(): Foldable<ForMaybeK> = MaybeK.foldable()
}

@extension
interface MaybeKCount : Count<ForMaybeK> {
  override fun foldable(): Foldable<ForMaybeK> = MaybeK.foldable()
}

@extension
interface MaybeKSum : Sum<ForMaybeK> {
  override fun foldable(): Foldable<ForMaybeK> = MaybeK.foldable()
}

@extension
interface MaybeKOrderBy : OrderBy<ForMaybeK> {
  override fun foldable(): Foldable<ForMaybeK> = MaybeK.foldable()
}

@extension
interface MaybeKUnion : Union<ForMaybeK> {
  override fun foldable(): Foldable<ForMaybeK> = MaybeK.foldable()
}