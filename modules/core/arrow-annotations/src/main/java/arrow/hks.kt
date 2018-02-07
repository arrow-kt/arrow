package arrow

interface HK<out F, out A>

typealias HK2<F, A, B> = HK<HK<F, A>, B>

typealias HK3<F, A, B, C> = HK<HK2<F, A, B>, C>

typealias HK4<F, A, B, C, D> = HK<HK3<F, A, B, C>, D>

typealias HK5<F, A, B, C, D, E> = HK<HK4<F, A, B, C, D>, E>
