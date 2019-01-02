package arrow

/**
 * `Kind<F, A>` represents a generic `F<A>` in a way that's allowed by Kotlin.
 * To revert it back to its original form use the extension function `fix()`.
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val a: Kind<ForOption, Int> = Option(1)
 *   val fixedA: Option<Int> = a.fix()
 *   //sampleEnd
 *   println(fixedA)
 * }
 * ```
 */
@documented
interface Kind<out F, out A>
typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>
typealias Kind3<F, A, B, C> = Kind<Kind2<F, A, B>, C>
typealias Kind4<F, A, B, C, D> = Kind<Kind3<F, A, B, C>, D>
typealias Kind5<F, A, B, C, D, E> = Kind<Kind4<F, A, B, C, D>, E>
typealias Kind6<F, A, B, C, D, E, G> = Kind<Kind5<F, A, B, C, D, E>, G>
typealias Kind7<F, A, B, C, D, E, G, H> = Kind<Kind6<F, A, B, C, D, E, G>, H>
typealias Kind8<F, A, B, C, D, E, G, H, I> = Kind<Kind7<F, A, B, C, D, E, G, H>, I>
typealias Kind9<F, A, B, C, D, E, G, H, I, J> = Kind<Kind8<F, A, B, C, D, E, G, H, I>, J>
typealias Kind10<F, A, B, C, D, E, G, H, I, J, K> = Kind<Kind9<F, A, B, C, D, E, G, H, I, J>, K>
typealias Kind11<F, A, B, C, D, E, G, H, I, J, K, L> = Kind<Kind10<F, A, B, C, D, E, G, H, I, J, K>, L>
typealias Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M> = Kind<Kind11<F, A, B, C, D, E, G, H, I, J, K, L>, M>
typealias Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N> = Kind<Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M>, N>
typealias Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O> = Kind<Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N>, O>
typealias Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P> = Kind<Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O>, P>
typealias Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q> = Kind<Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P>, Q>
typealias Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R> = Kind<Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q>, R>
typealias Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Kind<Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R>, S>
typealias Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Kind<Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S>, T>
typealias Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Kind<Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, U>
typealias Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = Kind<Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, V>
typealias Kind22<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> = Kind<Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, W>
