package arrow.optics.instances

import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.extensions.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.sequencek.eq.eq
import arrow.core.list
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.optics.Traversal
import arrow.optics.sequence
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class SequenceInstanceTest : UnitSpec() {

  private fun <A> Gen.Companion.sequence(genA: Gen<A>): Gen<Sequence<A>> = list(genA).map { it.asSequence() }

  private fun <A> sequenceEq(eqA: Eq<A>): Eq<Sequence<A>> = object : Eq<Sequence<A>> {
    override fun Sequence<A>.eqv(b: Sequence<A>): Boolean =
      SequenceK.eq(eqA).run { SequenceK(this@eqv).eqv(SequenceK(b)) }
  }

  init {

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal.sequence(),
        aGen = Gen.sequence(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = sequenceEq(String.eq()),
        EQOptionB = Option.eq(String.eq()),
        EQListB = Eq.list(String.eq())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = FilterIndex.sequence<String>().filter { true },
        aGen = Gen.sequence(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = sequenceEq(String.eq()),
        EQListB = Eq.list(String.eq()),
        EQOptionB = Option.eq(String.eq())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optionalGen = Gen.int().map { Index.sequence<String>().index(it) },
        aGen = Gen.sequence(Gen.string()),
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQOptionB = Option.eq(String.eq()),
        EQA = sequenceEq(String.eq())
      )
    )
  }
}
