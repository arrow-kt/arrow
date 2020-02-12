package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.MonadPlus
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadPlusLaws {

  fun <F> laws(MP: MonadPlus<F>, GK: GenK<F>, EQK: EqK<F>): List<Law> {
    val G = GK.genK(Gen.int())
    val EQ = EQK.liftEq(Int.eq())

    return MonadLaws.laws(MP, GK, EQK) +
      AlternativeLaws.laws(MP, GK, EQK) +
      monadPlusLaws(MP, G, EQ)
  }

  private fun <F> monadPlusLaws(MP: MonadPlus<F>, G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(
      Law("MonadPlus Laws: Left identity") { MP.leftIdentity(G, EQ) },
      Law("MonadPlus Laws: Right identity") { MP.rightIdentity(G, EQ) },
      Law("MonadPlus Laws: associativity") { MP.associativity(G, EQ) },
      Law("MonadPlus Laws: Left zero") { MP.leftZero(G, EQ) },
      Law("MonadPlus Laws: Right zero") { MP.rightZero(G, EQ) }
    )

  fun <F, A> MonadPlus<F>.leftIdentity(GEN: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(GEN) { a ->
      (zeroM<A>().plusM(a)).equalUnderTheLaw(a, EQ)
    }

  fun <F, A> MonadPlus<F>.rightIdentity(GEN: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(GEN) { a ->
      a.plusM(zeroM<A>()).equalUnderTheLaw(a, EQ)
    }

  fun <F, A> MonadPlus<F>.associativity(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(G, G, G) { m, n, o ->
      val ls = m.plusM(n.plusM(o))
      val rs = m.plusM(n).plusM(o)

      ls.equalUnderTheLaw(rs, EQ)
    }

  fun <F, A> MonadPlus<F>.leftZero(GEN: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(GEN) { a ->
      val r = zeroM<A>().flatMap {
        a
      }

      r.equalUnderTheLaw(zeroM(), EQ)
    }

  fun <F, A> MonadPlus<F>.rightZero(GEN: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(GEN) { a ->
      val r = a.flatMap {
        zeroM<A>()
      }

      r.equalUnderTheLaw(zeroM(), EQ)
    }
}
