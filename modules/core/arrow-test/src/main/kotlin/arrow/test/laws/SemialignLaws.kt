package arrow.test.laws

import arrow.Kind
import arrow.core.Ior
import arrow.core.ListK
import arrow.core.extensions.eq
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.list.functorFilter.flattenOption
import arrow.core.extensions.list.monadFilter.filterMap
import arrow.core.extensions.listk.monoid.monoid
import arrow.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Semialign
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemialignLaws {

  val iorEq1 = Ior.eq(Int.eq(), Int.eq())
  val iorEq2 = Ior.eq(Int.eq(), iorEq1)

  fun <F> laws(
    SA: Semialign<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val intGen = GENK.genK(Gen.int())

    return FunctorLaws.laws(SA, GENK, EQK) + listOf(
      Law("Semialign Laws: idempotency") { SA.semialignIdempotency(intGen, EQK.liftEq(iorEq1)) },
      Law("Semialign Laws: commutativity") { SA.semialignCommutativity(intGen, EQK.liftEq(iorEq1)) },
      Law("Semialign Laws: associativity") { SA.semialignAssociativity(intGen, EQK.liftEq(iorEq2)) },
      Law("Semialign Laws: with") { SA.semialignWith(intGen, EQK.liftEq(String.eq())) },
      Law("Semialign Laws: functoriality") { SA.semialignFunctoriality(intGen, EQK.liftEq(Ior.eq(String.eq(), String.eq()))) }
    )
  }

  fun <F> laws(
    SA: Semialign<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ): List<Law> = laws(SA, GENK, EQK) +
    listOf(
      Law("Semialign Laws: alignedness") { SA.semialignAlignedness(GENK.genK(Gen.int()), FOLD) }
    )

  // Laws ported from https://hackage.haskell.org/package/semialign-1.1/docs/Data-Semialign.html

  fun <F, A> Semialign<F>.semialignIdempotency(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, A>>>) =
    forAll(G) { a ->
      align(a, a).equalUnderTheLaw(a.map { Ior.Both(it, it) }, EQ)
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
      val left = alignWith(a, b) { "$it" }
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
      es.foldMap(ListK.monoid()) { ListK.just(it) }
    }

    val left: List<A> = toList(a)

    // toListOf (folded . here) (align x y)
    val middle: List<A> = toList(align(a, b).map { it.toLeftOption() }).flattenOption()

    // mapMaybe justHere (toList (align x y))
    val right: List<A> = toList(align(a, b)).filterMap { it.toLeftOption() }

    left == right && left == middle
  }
}

private fun <A, B, C> Ior<Ior<A, B>, C>.assoc(): Ior<A, Ior<B, C>> =
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
