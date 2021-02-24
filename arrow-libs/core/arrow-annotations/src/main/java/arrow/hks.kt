package arrow

const val KindDeprecation =
  """Higher Kinded types and their related type classes will no longer be supported after Arrow 0.13.0. Most relevant APIs are now concrete over the data types available as members or top level extension functions"""

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
@Deprecated(KindDeprecation)
interface Kind<out F, out A>

@Deprecated(KindDeprecation)
typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>

@Deprecated(KindDeprecation)
typealias Kind3<F, A, B, C> = Kind<Kind2<F, A, B>, C>

@Deprecated(KindDeprecation)
typealias Kind4<F, A, B, C, D> = Kind<Kind3<F, A, B, C>, D>

@Deprecated(KindDeprecation)
typealias Kind5<F, A, B, C, D, E> = Kind<Kind4<F, A, B, C, D>, E>

@Deprecated(KindDeprecation)
typealias Kind6<F, A, B, C, D, E, G> = Kind<Kind5<F, A, B, C, D, E>, G>

@Deprecated(KindDeprecation)
typealias Kind7<F, A, B, C, D, E, G, H> = Kind<Kind6<F, A, B, C, D, E, G>, H>

@Deprecated(KindDeprecation)
typealias Kind8<F, A, B, C, D, E, G, H, I> = Kind<Kind7<F, A, B, C, D, E, G, H>, I>

@Deprecated(KindDeprecation)
typealias Kind9<F, A, B, C, D, E, G, H, I, J> = Kind<Kind8<F, A, B, C, D, E, G, H, I>, J>

@Deprecated(KindDeprecation)
typealias Kind10<F, A, B, C, D, E, G, H, I, J, K> = Kind<Kind9<F, A, B, C, D, E, G, H, I, J>, K>

@Deprecated(KindDeprecation)
typealias Kind11<F, A, B, C, D, E, G, H, I, J, K, L> = Kind<Kind10<F, A, B, C, D, E, G, H, I, J, K>, L>

@Deprecated(KindDeprecation)
typealias Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M> = Kind<Kind11<F, A, B, C, D, E, G, H, I, J, K, L>, M>

@Deprecated(KindDeprecation)
typealias Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N> = Kind<Kind12<F, A, B, C, D, E, G, H, I, J, K, L, M>, N>

@Deprecated(KindDeprecation)
typealias Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O> = Kind<Kind13<F, A, B, C, D, E, G, H, I, J, K, L, M, N>, O>

@Deprecated(KindDeprecation)
typealias Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P> = Kind<Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O>, P>

@Deprecated(KindDeprecation)
typealias Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q> = Kind<Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P>, Q>

@Deprecated(KindDeprecation)
typealias Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R> = Kind<Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q>, R>

@Deprecated(KindDeprecation)
typealias Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S> = Kind<Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R>, S>

@Deprecated(KindDeprecation)
typealias Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> = Kind<Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S>, T>

@Deprecated(KindDeprecation)
typealias Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> = Kind<Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, U>

@Deprecated(KindDeprecation)
typealias Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> = Kind<Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, V>

@Deprecated(KindDeprecation)
typealias Kind22<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> = Kind<Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, W>
