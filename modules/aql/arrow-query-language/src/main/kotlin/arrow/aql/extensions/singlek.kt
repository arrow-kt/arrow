package arrow.aql.extensions

import arrow.aql.*
import arrow.effects.rx2.ForSingleK
import arrow.effects.rx2.SingleK
import arrow.effects.rx2.extensions.singlek.applicative.applicative
import arrow.effects.rx2.extensions.singlek.functor.functor
import arrow.effects.rx2.extensions.singlek.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension
interface SingleKFrom : From<ForSingleK> {
  override fun applicative(): Applicative<ForSingleK> = SingleK.applicative()
}

@extension
interface SingleKSelect : Select<ForSingleK> {
  override fun functor(): Functor<ForSingleK> = SingleK.functor()
}

@extension
interface SingleKWhere : Where<ForSingleK> {
  override fun functorFilter(): FunctorFilter<ForSingleK> = SingleK.functorFilter()
}