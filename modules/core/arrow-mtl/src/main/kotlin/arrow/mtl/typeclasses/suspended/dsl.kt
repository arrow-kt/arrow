package arrow.mtl.typeclasses.suspended

import arrow.Kind
import arrow.core.Option
import arrow.core.PartialFunction
import arrow.mtl.typeclasses.MonadFilter
import arrow.typeclasses.suspended.MonadSyntax

interface MonadFilterSyntax<F> : MonadSyntax<F>, MonadFilter<F> {

  private suspend fun <B> filtering(fb: MonadFilter<F>.() -> Kind<F, B>): B =
    run<MonadFilter<F>, Kind<F, B>> { fb(this) }.bind()

  suspend fun <B> empty(unit: Unit = Unit): B =
    filtering { empty<B>() }

  suspend fun <B, C> Kind<F, B>.mapFilter(unit: Unit = Unit, f: (B) -> Option<C>): C =
    filtering { mapFilter(f) }

  suspend fun <B, C> Kind<F, B>.collect(unit: Unit = Unit, f: PartialFunction<B, C>): C =
    filtering { collect(f) }

  suspend fun <B> Kind<F, Option<B>>.flattenOption(unit: Unit = Unit): B =
    filtering { flattenOption() }

  suspend fun <B> Kind<F, B>.filter(unit: Unit = Unit, f: (B) -> Boolean): B =
    filtering { filter(f) }
}