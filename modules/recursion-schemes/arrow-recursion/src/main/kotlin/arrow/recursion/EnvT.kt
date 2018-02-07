package arrow.recursion

import arrow.Kind
import arrow.higherkind

@higherkind data class EnvT<out B, out W, out A>(val ask: B, val lower: Kind<W, A>) : EnvTKind<B, W, A>