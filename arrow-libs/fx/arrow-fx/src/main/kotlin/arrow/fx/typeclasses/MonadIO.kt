package arrow.fx.typeclasses

import arrow.Kind
import arrow.fx.IO
import arrow.fx.IODeprecation
import arrow.typeclasses.Monad

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.MonadIO)
 *
 * Lift concrete [IO] into a polymorphic monad [M]. This is used to call [IO] from mtl style programs which don't have a concrete type.
 * This is in theory enough to define the entire effect hierarchy up to [Concurrent] however those instances might have different semantics for different
 *  types which is why it is not done here. If one wants to use [Concurrent] methods by only delegating to [IO] simply perform the task in [IO] and lift it.
 *  If that is not enough use the fx-mtl package for better instances of the effect hierarchy.
 **/
@Deprecated(IODeprecation)
interface MonadIO<M> : Monad<M> {
  fun <A> IO<A>.liftIO(): Kind<M, A>
}
