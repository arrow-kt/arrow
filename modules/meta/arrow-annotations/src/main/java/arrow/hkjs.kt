@file:Suppress("UnusedImports")

package arrow

import io.kindedj.Hk as HK_J

typealias HkJ<F, A> = HK_J<F, A>

typealias HkJ2<F, A, B> = HK_J<HK_J<F, A>, B>

typealias HkJ3<F, A, B, C> = HK_J<HkJ2<F, A, B>, C>

typealias HkJ4<F, A, B, C, D> = HK_J<HkJ3<F, A, B, C>, D>

typealias HkJ5<F, A, B, C, D, E> = HK_J<HkJ4<F, A, B, C, D>, E>
