package arrow.test.laws

import arrow.Kind
import arrow.core.Const
import arrow.core.Either
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.list.functorFilter.flattenOption
import arrow.core.extensions.list.monadFilter.filterMap
import arrow.core.toT
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Semialign
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemialignLaws {

  val iorEq1 = Ior.eq(Int.eq(), Int.eq())
  val iorEq2 = Ior.eq(Int.eq(), iorEq1)

  fun <F> laws(
    SA: Semialign<F>,
    gen: Gen<Kind<F, Int>>,
    buildEq: (Eq<*>) -> Eq<Kind<F, *>>,
    FOLD: Foldable<F>
  ): List<Law> = listOf(
    Law("Semialign Laws: commutativity") { SA.semialignCommutativity(gen, buildEq(iorEq1)) },
    Law("Semialign Laws: associativity") { SA.semialignAssociativity(gen, buildEq(iorEq2)) },
    Law("Semialign Laws: with") { SA.semialignWith(gen, buildEq(String.eq())) },
    Law("Semialign Laws: functoriality") { SA.semialignFunctoriality(gen, buildEq(Ior.eq(String.eq(), String.eq()))) },
    Law("Semialign Laws: alignedness") { SA.semialignAlignedness(gen, FOLD) }
  )

  // Laws ported from https://hackage.haskell.org/package/semialign-1.1/docs/Data-Semialign.html

  fun <F> Semialign<F>.semialignIdempotency() {
    TODO()
  }

  fun <F, A> Semialign<F>.semialignCommutativity(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, A>>>) =
    forAll(G, G) { a: Kind<F, A>, b: Kind<F, A> ->
      val left: Kind<F, Ior<A, A>> = align(a, b).map { it.swap() }
      val right: Kind<F, Ior<A, A>> = align(b, a)
      left.equalUnderTheLaw(right, EQ)
    }

  fun <F, A> Semialign<F>.semialignAssociativity(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, Ior<A, A>>>>) =
    forAll(G, G, G) { x: Kind<F, A>, y: Kind<F, A>, z: Kind<F, A> ->

      val left = align(x, align(y, z))

      val right = align(align(x, y), z).map { it.assoc() }

      left.equalUnderTheLaw(right, EQ)
    }

  fun <F, A> Semialign<F>.semialignWith(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, String>>) =
    forAll(G, G) { a: Kind<F, A>, b: Kind<F, A> ->
      val left = alignWith({ "$it" }, a, b)
      val right = align(a, b).map { "$it" }

      left.equalUnderTheLaw(right, EQ)
    }

  fun <F, A> Semialign<F>.semialignFunctoriality(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<String, String>>>) =
    forAll(G, G) { a: Kind<F, A>, b: Kind<F, A> ->

      val left = align(a.map { "$it" }, b.map { "$it" })
      val right = align(a, b).map { ior ->
        ior.bimap({ "$it" }, { "$it" })
      }

      left.equalUnderTheLaw(right, EQ)
    }

  fun <F, A> Semialign<F>.semialignAlignedness(
    G: Gen<Kind<F, A>>,
    FOLD: Foldable<F>
  ) = forAll(G, G) { a: Kind<F, A>, b: Kind<F, A> ->

    fun <E> toList(es: Kind<F, E>): List<E> = FOLD.run {
      es.foldLeft(emptyList()) { acc, e ->
        acc + e
      }
    }

    val left: List<A> = toList(a)

    // toListOf (folded . here) (align x y)
    val middle: List<A> = toList(align(a, b).map { it.justLeft() }).flattenOption()

    // mapMaybe justHere (toList (align x y))
    val right: List<A> = toList(align(a, b)).filterMap { it.justLeft() }

    left == right && left == middle
  }
}

// http://hackage.haskell.org/package/these-0.8/docs/src/Data.These.Combinators.html#justHere

fun <A, B> Ior<A, B>.justLeft(): Option<A> =
  fold({ Some(it) }, { None }, { a, _ -> Some(a) })

// http://hackage.haskell.org/package/these-1.0.1/docs/src/Data.These.html#line-289

fun <A, B, C> Ior<Ior<A, B>, C>.assoc(): Ior<A, Ior<B, C>> =
  when (this) {
    is Ior.Left -> when (val inner = this.value) {
      is Ior.Left -> Ior.Left(inner.value)
      is Ior.Right -> Ior.Right(Ior.Left(inner.value))
      is Ior.Both -> Ior.Both(inner.leftValue, Ior.Left(inner.rightValue))
    }
    is Ior.Right -> Ior.Right(Ior.Right(this.value))
    is Ior.Both -> when (val inner = this.leftValue) {
      is Ior.Left -> Ior.Both(inner.value, Ior.Right(this.rightValue))
      is Ior.Right -> Ior.Right(Ior.Both(inner.value, this.rightValue))
      is Ior.Both -> Ior.Both(inner.leftValue, Ior.Both(inner.rightValue, this.rightValue))
    }
  }

fun <A, B, C> Ior<A, Ior<B, C>>.unassoc(): Ior<Ior<A, B>, C> =
  when (this) {
    is Ior.Left -> Ior.Left(Ior.Left(this.value))
    is Ior.Right -> when (val inner = this.value) {
      is Ior.Left -> Ior.Left(Ior.Right(inner.value))
      is Ior.Right -> Ior.Right(inner.value)
      is Ior.Both -> Ior.Both(Ior.Right(inner.leftValue), inner.rightValue)
    }
    is Ior.Both -> when (val inner = this.rightValue) {
      is Ior.Left -> Ior.Left(Ior.Both(this.leftValue, inner.value))
      is Ior.Right -> Ior.Both(Ior.Left(this.leftValue), inner.value)
      is Ior.Both -> Ior.Both(Ior.Both(this.leftValue, inner.leftValue), inner.rightValue)
    }
  }

fun <A, B, C> Either<Either<A, B>, C>.assoc(): Either<A, Either<B, C>> =
  when (this) {
    is Either.Left -> when (val inner = this.a) {
      is Either.Left -> Either.Left(inner.a)
      is Either.Right -> Either.Right(Either.left(inner.b))
    }
    is Either.Right -> Either.Right(Either.Right(this.b))
  }

fun <A, B, C> Either<A, Either<B, C>>.unassoc(): Either<Either<A, B>, C> =
  when (this) {
    is Either.Left -> Either.Left(Either.Left(this.a))
    is Either.Right -> when (val inner = this.b) {
      is Either.Left -> Either.Left(Either.Right(inner.a))
      is Either.Right -> Either.Right(inner.b)
    }
  }

fun <A, B, C> Tuple2<Tuple2<A, B>, C>.assoc(): Tuple2<A, Tuple2<B, C>> =
  this.a.a toT (this.a.b toT this.b)

fun <A, B, C> Tuple2<A, Tuple2<B, C>>.unassoc(): Tuple2<Tuple2<A, B>, C> =
  (this.a toT this.b.a) toT this.b.b

fun <A, T> Const<Const<A, T>, T>.assoc(): Const<A, T> =
  this.value()

fun <A, T> Const<A, T>.unassoc(): Const<Const<A, T>, A> =
  Const.just(this)
