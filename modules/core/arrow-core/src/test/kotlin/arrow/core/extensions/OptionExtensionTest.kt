package arrow.core.extensions

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.semialign.semialign
import arrow.test.UnitSpec
import arrow.test.generators.option
import arrow.test.laws.SemialignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class OptionExtensionTest : UnitSpec() {
  init {
    testLaws(SemialignLaws.laws(Option.semialign(),
      Gen.option(Gen.int()) as Gen<Kind<ForOption, Int>>,
      { Option.eq(it) as Eq<Kind<ForOption, *>> },
      Option.foldable()
    ))
  }
}
