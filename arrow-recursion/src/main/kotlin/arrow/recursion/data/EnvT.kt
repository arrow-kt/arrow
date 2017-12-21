package arrow

@higherkind data class EnvT<out B, out W, out A>(val ask: B, val lower: HK<W, A>) : EnvTKind<B, W, A>