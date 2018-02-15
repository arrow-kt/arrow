package arrow

interface Kind<out F, out A>

typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>

typealias Kind3<F, A, B, C> = Kind<Kind2<F, A, B>, C>

typealias Kind4<F, A, B, C, D> = Kind<Kind3<F, A, B, C>, D>
