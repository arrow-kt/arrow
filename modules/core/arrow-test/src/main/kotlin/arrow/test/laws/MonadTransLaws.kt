package arrow.test.laws

import arrow.Kind
import arrow.Kind2
import arrow.core.extensions.eq
import arrow.mtl.typeclasses.MonadTrans
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadTransLaws {
  fun <T, F> laws(
    monadTrans: MonadTrans<T>,
    monadF: Monad<F>,
    monadTF: Monad<Kind<T, F>>,
    genkF: GenK<F>,
    eqkTF: EqK<Kind<T, F>>
  ): List<Law> {
    val genFA = genkF.genK(Gen.int())
    val genFunAtoFB = Gen.functionAToB<Int, Kind<F, Int>>(genFA)
    val eq = eqkTF.liftEq(Int.eq())

    return listOf(
      Law("MonadTrans Laws: Identity") {
        monadTrans.identity(Gen.int(), monadF, monadTF, eq)
      },
      Law("MonadTrans Laws: associativity") {
        monadTrans.associativity(genFA, genFunAtoFB, monadF, monadTF, eq)
      }
    )
  }

  private fun <T, F, A> MonadTrans<T>.identity(
    genA: Gen<A>,
    MM: Monad<F>,
    MF: Monad<Kind<T, F>>,
    EQ: Eq<Kind2<T, F, A>>
  ) {
    forAll(genA) { a ->
      val ls = MM.just(a).liftT(MM)
      val rs = MF.just(a)

      ls.equalUnderTheLaw(rs, EQ)
    }
  }

  private fun <T, F, A, B> MonadTrans<T>.associativity(
    genFA: Gen<Kind<F, A>>,
    genFunAtoFB: Gen<(A) -> Kind<F, B>>,
    monadF: Monad<F>,
    monadTF: Monad<Kind<T, F>>,
    EQ: Eq<Kind2<T, F, B>>
  ) = forAll(genFA, genFunAtoFB) { fa, ffa ->
    val ls = monadF.run { fa.flatMap(ffa) }.liftT(monadF)
    val rs = monadTF.run { fa.liftT(monadF).flatMap { a -> ffa(a).liftT(monadF) } }

    ls.equalUnderTheLaw(rs, EQ)
  }
}
