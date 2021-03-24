package arrow.optics

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Const
import arrow.core.Either
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.AndMonoid
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.either.traverse.traverse
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.monoid
import arrow.core.extensions.option.traverse.traverse
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.core.toT
import arrow.core.value
import arrow.optics.typeclasses.Id
import arrow.optics.typeclasses.fix
import arrow.optics.typeclasses.idApplicative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monoid
import arrow.typeclasses.Traverse

@Deprecated(KindDeprecation)
class ForPTraversal private constructor() {
  companion object
}
@Deprecated(KindDeprecation)
typealias PTraversalOf<S, T, A, B> = arrow.Kind4<ForPTraversal, S, T, A, B>
@Deprecated(KindDeprecation)
typealias PTraversalPartialOf<S, T, A> = arrow.Kind3<ForPTraversal, S, T, A>
@Deprecated(KindDeprecation)
typealias PTraversalKindedJ<S, T, A, B> = arrow.HkJ4<ForPTraversal, S, T, A, B>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(KindDeprecation)
inline fun <S, T, A, B> PTraversalOf<S, T, A, B>.fix(): PTraversal<S, T, A, B> =
  this as PTraversal<S, T, A, B>

/**
 * [Traversal] is a type alias for [PTraversal] which fixes the type arguments
 * and restricts the [PTraversal] to monomorphic updates.
 */
typealias Traversal<S, A> = PTraversal<S, S, A, A>

typealias ForTraversal = ForPTraversal
typealias TraversalOf<S, A> = PTraversalOf<S, S, A, A>
typealias TraversalPartialOf<S> = Kind<ForTraversal, S>
typealias TraversalKindedJ<S, A> = PTraversalKindedJ<S, S, A, A>

/**
 * A [Traversal] is an optic that allows to see into a structure with 0 to N foci.
 *
 * [Traversal] is a generalisation of [arrow.Traverse] and can be seen as a representation of modifyF.
 * all methods are written in terms of modifyF
 *
 * @param S the source of a [PTraversal]
 * @param T the modified source of a [PTraversal]
 * @param A the target of a [PTraversal]
 * @param B the modified target of a [PTraversal]
 */
interface PTraversal<S, T, A, B> : PTraversalOf<S, T, A, B> {

  fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T>

  companion object {
    fun <S> id() = PIso.id<S>().asTraversal()

    fun <S> codiagonal(): Traversal<Either<S, S>, S> = object : Traversal<Either<S, S>, S> {
      override fun <F> modifyF(FA: Applicative<F>, s: Either<S, S>, f: (S) -> Kind<F, S>): Kind<F, Either<S, S>> =
        FA.run {
          s.bimap(f, f).fold({ fa -> fa.map { a -> Either.Left(a) } }, { fa -> fa.map { a -> Either.Right(a) } })
        }
    }

    /**
     * Construct a [PTraversal] from a [Traverse] instance.
     */
    fun <T, A, B> fromTraversable(TT: Traverse<T>) = object : PTraversal<Kind<T, A>, Kind<T, B>, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: Kind<T, A>, f: (A) -> Kind<F, B>): Kind<F, Kind<T, B>> =
        TT.run { s.traverse(FA, f) }
    }

    /**
     * [PTraversal] that points to nothing
     */
    fun <S, A> void() = POptional.void<S, A>().asTraversal()

    /**
     * [PTraversal] constructor from multiple getters of the same source.
     */
    operator fun <S, T, A, B> invoke(get1: (S) -> A, get2: (S) -> A, set: (B, B, S) -> T): PTraversal<S, T, A, B> =
      object : PTraversal<S, T, A, B> {
        override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
          FA.mapN(
            f(get1(s)), f(get2(s))
          ) { (b1, b2) -> set(b1, b2, s) }
      }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      set: (B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s))
        ) { (b1, b2, b3) -> set(b1, b2, b3, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      set: (B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s))
        ) { (b1, b2, b3, b4) -> set(b1, b2, b3, b4, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      set: (B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s))
        ) { (b1, b2, b3, b4, b5) -> set(b1, b2, b3, b4, b5, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      set: (B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s))
        ) { (b1, b2, b3, b4, b5, b6) -> set(b1, b2, b3, b4, b5, b6, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      set: (B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s))
        ) { (b1, b2, b3, b4, b5, b6, b7) -> set(b1, b2, b3, b4, b5, b6, b7, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      set: (B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s))
        ) { (b1, b2, b3, b4, b5, b6, b7, b8) -> set(b1, b2, b3, b4, b5, b6, b7, b8, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)), f(get2(s)), f(get3(s)), f(get4(s)), f(get5(s)), f(get6(s)), f(get7(s)), f(get8(s)), f(get9(s))
        ) { (b1, b2, b3, b4, b5, b6, b7, b8, b9) -> set(b1, b2, b3, b4, b5, b6, b7, b8, b9, s) }
    }

    operator fun <S, T, A, B> invoke(
      get1: (S) -> A,
      get2: (S) -> A,
      get3: (S) -> A,
      get4: (S) -> A,
      get5: (S) -> A,
      get6: (S) -> A,
      get7: (S) -> A,
      get8: (S) -> A,
      get9: (S) -> A,
      get10: (S) -> A,
      set: (B, B, B, B, B, B, B, B, B, B, S) -> T
    ): PTraversal<S, T, A, B> = object : PTraversal<S, T, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: S, f: (A) -> Kind<F, B>): Kind<F, T> =
        FA.mapN(
          f(get1(s)),
          f(get2(s)),
          f(get3(s)),
          f(get4(s)),
          f(get5(s)),
          f(get6(s)),
          f(get7(s)),
          f(get8(s)),
          f(get9(s)),
          f(get10(s))
        ) { (b1, b2, b3, b4, b5, b6, b7, b8, b9, b10) -> set(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, s) }
    }

    /**
     * [Traversal] for [List] that focuses in each [A] of the source [List].
     */
    @JvmStatic
    fun <A> list(): Traversal<List<A>, A> =
      object : Traversal<List<A>, A> {
        override fun <F> modifyF(FA: Applicative<F>, s: List<A>, f: (A) -> Kind<F, A>): Kind<F, List<A>> =
          s.k().traverse(FA, f)
      }

    /**
     * [Traversal] for [Either] that has focus in each [Either.Right].
     *
     * @receiver [Traversal.Companion] to make it statically available.
     * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
     */
    @JvmStatic
    fun <L, R> either(): Traversal<Either<L, R>, R> =
      object : Traversal<Either<L, R>, R> {
        override fun <F> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> =
          with(Either.traverse<L>()) {
            FA.run { s.traverse(FA, f).map { it.fix() } }
          }
      }

    @JvmStatic
    fun <K, V> map(): Traversal<Map<K, V>, V> = object : Traversal<Map<K, V>, V> {
      override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
        s.k().traverse(FA, f)
      }
    }

    /**
     * [Traversal] for [NonEmptyList] that has focus in each [A].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [NonEmptyList] and focus every [A] of the source.
     */
    @JvmStatic
    fun <A> nonEmptyList(): Traversal<NonEmptyList<A>, A> =
      object : Traversal<NonEmptyList<A>, A> {
        override fun <F> modifyF(
          FA: Applicative<F>,
          s: NonEmptyList<A>,
          f: (A) -> Kind<F, A>
        ): Kind<F, NonEmptyList<A>> =
          s.traverse(FA, f)
      }

    /**
     * [Traversal] for [Option] that has focus in each [arrow.core.Some].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [Option] and focus in every [arrow.core.Some] of the source.
     */
    @JvmStatic
    fun <A> option(): Traversal<Option<A>, A> =
      object : Traversal<Option<A>, A> {
        override fun <F> modifyF(FA: Applicative<F>, s: Option<A>, f: (A) -> Kind<F, A>): Kind<F, Option<A>> =
          with(Option.traverse()) {
            s.traverse(FA, f)
          }
      }

    @JvmStatic
    fun <A> sequence(): Traversal<Sequence<A>, A> =
      object : Traversal<Sequence<A>, A> {
        override fun <F> modifyF(FA: Applicative<F>, s: Sequence<A>, f: (A) -> Kind<F, A>): Kind<F, Sequence<A>> =
          FA.run { s.k().traverse(FA, f) }
      }

    /**
     * [Traversal] for [String] that focuses in each [Char] of the source [String].
     *
     * @receiver [PTraversal.Companion] to make it statically available.
     * @return [Traversal] with source [String] and foci every [Char] in the source.
     */
    @JvmStatic
    fun string(): Traversal<String, Char> =
      object : Traversal<String, Char> {
        override fun <F> modifyF(FA: Applicative<F>, s: String, f: (Char) -> Kind<F, Char>): Kind<F, String> = FA.run {
          s.toList().k().traverse(FA, f).map { it.joinToString(separator = "") }
        }
      }

    /**
     * [PTraversal] to focus into the first and second value of a [Pair]
     */
    @JvmStatic
    fun <A, B> pairPTraversal(): PTraversal<Pair<A, A>, Pair<B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        set = { a, b, _ -> a to b }
      )

    /**
     * [Traversal] to focus into the first and second value of a [Pair]
     */
    @JvmStatic
    fun <A> pairTraversal(): Traversal<Pair<A, A>, A> =
      pairPTraversal()

    /**
     * [PTraversal] to focus into the first, second and third value of a [Triple]
     */
    @JvmStatic
    fun <A, B> triplePTraversal(): PTraversal<Triple<A, A, A>, Triple<B, B, B>, A, B> =
      PTraversal(
        get1 = { it.first },
        get2 = { it.second },
        get3 = { it.third },
        set = { a, b, c, _ -> Triple(a, b, c) }
      )

    /**
     * [Traversal] to focus into the first, second and third value of a [Triple]
     */
    @JvmStatic
    fun <A> tripleTraversal(): Traversal<Triple<A, A, A>, A> =
      triplePTraversal()

    /**
     * [PTraversal] to focus into the first and second value of a [arrow.core.Tuple2]
     */
    @JvmStatic
    fun <A, B> tuple2PTraversal(): PTraversal<Tuple2<A, A>, Tuple2<B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        set = { a, b, _ -> a toT b }
      )

    /**
     * [Traversal] to focus into the first and second value of a [arrow.core.Tuple2]
     */
    @JvmStatic
    fun <A> tuple2Traversal(): Traversal<Tuple2<A, A>, A> =
      tuple2PTraversal()

    /**
     * [PTraversal] to focus into the first, second and third value of a [arrow.core.Tuple3]
     */
    @JvmStatic
    fun <A, B> tuple3PTraversal(): PTraversal<Tuple3<A, A, A>, Tuple3<B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        set = { a, b, c, _ -> Tuple3(a, b, c) }
      )

    /**
     * [Traversal] to focus into the first, second and third value of a [arrow.core.Tuple3]
     */
    @JvmStatic
    fun <A> tuple3Traversal(): Traversal<Tuple3<A, A, A>, A> =
      tuple3PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
     */
    @JvmStatic
    fun <A, B> tuple4PTraversal(): PTraversal<Tuple4<A, A, A, A>, Tuple4<B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        set = { a, b, c, d, _ -> Tuple4(a, b, c, d) }
      )

    /**
     * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
     */
    @JvmStatic
    fun <A> tuple4Traversal(): Traversal<Tuple4<A, A, A, A>, A> =
      tuple4PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
     */
    @JvmStatic
    fun <A, B> tuple5PTraversal(): PTraversal<Tuple5<A, A, A, A, A>, Tuple5<B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        set = { a, b, c, d, e, _ -> Tuple5(a, b, c, d, e) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
     */
    @JvmStatic
    fun <A> tuple5Traversal(): Traversal<Tuple5<A, A, A, A, A>, A> =
      tuple5PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
     */
    @JvmStatic
    fun <A, B> tuple6PTraversal(): PTraversal<Tuple6<A, A, A, A, A, A>, Tuple6<B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        get6 = { it.f },
        set = { a, b, c, d, e, f, _ -> Tuple6(a, b, c, d, e, f) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
     */
    @JvmStatic
    fun <A> tuple6Traversal(): Traversal<Tuple6<A, A, A, A, A, A>, A> =
      tuple6PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
     */
    @JvmStatic
    fun <A, B> tuple7PTraversal(): PTraversal<Tuple7<A, A, A, A, A, A, A>, Tuple7<B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        get6 = { it.f },
        get7 = { it.g },
        set = { a, b, c, d, e, f, g, _ -> Tuple7(a, b, c, d, e, f, g) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
     */
    @JvmStatic
    fun <A> tuple7Traversal(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> =
      tuple7PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
     */
    @JvmStatic
    fun <A, B> tuple8PTraversal(): PTraversal<Tuple8<A, A, A, A, A, A, A, A>, Tuple8<B, B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        get6 = { it.f },
        get7 = { it.g },
        get8 = { it.h },
        set = { a, b, c, d, e, f, g, h, _ -> Tuple8(a, b, c, d, e, f, g, h) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
     */
    @JvmStatic
    fun <A> tuple8Traversal(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> =
      tuple8PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
     */
    @JvmStatic
    fun <A, B> tuple9PTraversal(): PTraversal<Tuple9<A, A, A, A, A, A, A, A, A>, Tuple9<B, B, B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        get6 = { it.f },
        get7 = { it.g },
        get8 = { it.h },
        get9 = { it.i },
        set = { a, b, c, d, e, f, g, h, i, _ -> Tuple9(a, b, c, d, e, f, g, h, i) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
     */
    @JvmStatic
    fun <A> tuple9Traversal(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
      tuple9PTraversal()

    /**
     * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
     */
    @JvmStatic
    fun <A, B> tuple10PTraversal(): PTraversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, Tuple10<B, B, B, B, B, B, B, B, B, B>, A, B> =
      PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        get6 = { it.f },
        get7 = { it.g },
        get8 = { it.h },
        get9 = { it.i },
        get10 = { it.j },
        set = { a, b, c, d, e, f, g, h, i, j, _ -> Tuple10(a, b, c, d, e, f, g, h, i, j) }
      )

    /**
     * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
     */
    @JvmStatic
    fun <A> tuple10Traversal(): Traversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> =
      tuple10PTraversal()
  }

  /**
   * Map each target to a Monoid and combine the results
   */
  fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
    modifyF(Const.applicative(M), s) { b -> Const<R, B>(f(b)) }.value()

  /**
   * Fold using the given [Monoid] instance.
   */
  fun fold(M: Monoid<A>, s: S): A = foldMap(M, s, ::identity)

  /**
   * Alias for fold.
   */
  fun combineAll(M: Monoid<A>, s: S): A = fold(M, s)

  /**
   * Get all foci of the [PTraversal]
   */
  fun getAll(s: S): ListK<A> = foldMap(ListK.monoid(), s) { ListK(listOf(it)) }

  /**
   * Set polymorphically the target of a [PTraversal] with a value
   */
  fun set(s: S, b: B): T = modify(s) { b }

  /**
   * Calculate the number of targets in the [PTraversal]
   */
  fun size(s: S): Int = foldMap(Int.monoid(), s) { 1 }

  /**
   * Check if there is no target
   */
  fun isEmpty(s: S): Boolean = foldMap(AndMonoid, s) { _ -> false }

  /**
   * Check if there is at least one target
   */
  fun nonEmpty(s: S): Boolean = !isEmpty(s)

  /**
   * Find the first target or [Option.None] if no targets
   */
  fun headOption(s: S): Option<A> = foldMap(firstOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  /**
   * Find the first target or [Option.None] if no targets
   */
  fun lastOption(s: S): Option<A> = foldMap(lastOptionMonoid<A>(), s) { b -> Const(Some(b)) }.value()

  fun <U, V> choice(other: PTraversal<U, V, A, B>): PTraversal<Either<S, U>, Either<T, V>, A, B> =
    object : PTraversal<Either<S, U>, Either<T, V>, A, B> {
      override fun <F> modifyF(FA: Applicative<F>, s: Either<S, U>, f: (A) -> Kind<F, B>): Kind<F, Either<T, V>> =
        FA.run {
          s.fold(
            { a -> this@PTraversal.modifyF(FA, a, f).map { Either.Left(it) } },
            { u -> other.modifyF(FA, u, f).map { Either.Right(it) } }
          )
        }
    }

  /**
   * Compose a [PTraversal] with a [PTraversal]
   */
  infix fun <C, D> compose(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = object : PTraversal<S, T, C, D> {
    override fun <F> modifyF(FA: Applicative<F>, s: S, f: (C) -> Kind<F, D>): Kind<F, T> =
      this@PTraversal.modifyF(FA, s) { b -> other.modifyF(FA, b, f) }
  }

  /**
   * Compose a [PTraversal] with a [PSetter]
   */
  infix fun <C, D> compose(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = asSetter() compose other

  /**
   * Compose a [PTraversal] with a [POptional]
   */
  infix fun <C, D> compose(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PLens]
   */
  infix fun <C, D> compose(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PPrism]
   */
  infix fun <C, D> compose(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [PIso]
   */
  infix fun <C, D> compose(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other.asTraversal())

  /**
   * Compose a [PTraversal] with a [Fold]
   */
  infix fun <C> compose(other: Fold<A, C>): Fold<S, C> = asFold() compose other

  /**
   * Plus operator overload to compose [PTraversal] with other optics
   */
  operator fun <C, D> plus(other: PTraversal<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PSetter<A, B, C, D>): PSetter<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: POptional<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PLens<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PPrism<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C, D> plus(other: PIso<A, B, C, D>): PTraversal<S, T, C, D> = compose(other)

  operator fun <C> plus(other: Fold<A, C>): Fold<S, C> = compose(other)

  fun asSetter(): PSetter<S, T, A, B> = PSetter { s, f -> modify(s, f) }

  fun asFold(): Fold<S, A> = object : Fold<S, A> {
    override fun <R> foldMap(M: Monoid<R>, s: S, f: (A) -> R): R =
      this@PTraversal.foldMap(M, s, f)
  }

  /**
   * Find the first target matching the predicate
   */
  fun find(s: S, p: (A) -> Boolean): Option<A> = foldMap(firstOptionMonoid<A>(), s) { a ->
    if (p(a)) Const(Some(a))
    else Const(None)
  }.value()

  /**
   * Map each target to a Monoid and combine the results
   */
  fun <R> foldMap(s: S, f: (A) -> R, M: Monoid<R>): R =
    modifyF(Const.applicative(M), s) { b -> Const(f(b)) }.value()

  /**
   * Modify polymorphically the target of a [PTraversal] with a function [f]
   */
  fun modify(s: S, f: (A) -> B): T = modifyF(idApplicative, s) { b -> Id(f(b)) }.fix().value

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun exist(s: S, p: (A) -> Boolean): Boolean = find(s, p).fold({ false }, { true })

  /**
   * Check if forall targets satisfy the predicate
   */
  fun forall(s: S, p: (A) -> Boolean): Boolean = foldMap(s, p, AndMonoid)

  /**
   * DSL to compose [Traversal] with a [Lens] for a structure [S] to see all its foci [A]
   *
   * @receiver [Lens] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PLens<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this@every.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Iso] for a structure [S] to see all its foci [A]
   *
   * @receiver [Iso] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PIso<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this@every.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Prism] for a structure [S] to see all its foci [A]
   *
   * @receiver [Prism] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PPrism<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Optional] for a structure [S] to see all its foci [A]
   *
   * @receiver [Optional] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> POptional<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Setter] for a structure [S] to see all its foci [A]
   *
   * @receiver [Setter] with a focus in [S]
   * @return [Setter] with a focus in [A]
   */
  val <U, V> PSetter<U, V, S, T>.every: PSetter<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Traversal] for a structure [S] to see all its foci [A]
   *
   * @receiver [Traversal] with a focus in [S]
   * @return [Traversal] with a focus in [A]
   */
  val <U, V> PTraversal<U, V, S, T>.every: PTraversal<U, V, A, B>
    get() =
      this.compose(this@PTraversal)

  /**
   * DSL to compose [Traversal] with a [Fold] for a structure [S] to see all its foci [A]
   *
   * @receiver [Fold] with a focus in [S]
   * @return [Fold] with a focus in [A]
   */
  val <U> Fold<U, S>.every: Fold<U, A> get() = this.compose(this@PTraversal.asFold())
}
