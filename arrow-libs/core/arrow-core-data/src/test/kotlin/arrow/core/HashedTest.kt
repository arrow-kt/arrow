package arrow.core

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.hashed.eq.eq
import arrow.core.extensions.hashed.eqK.eqK
import arrow.core.extensions.hashed.foldable.foldable
import arrow.core.extensions.hashed.hash.hash
import arrow.core.extensions.hashed.show.show
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.hashed
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.FoldableLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.ShowLaws
import io.kotlintest.properties.Gen

class HashedTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Hashed.eq(Int.eq()), Gen.int().hashed(Int.hash())),
      EqKLaws.laws(Hashed.eqK(), Hashed.genK()),
      ShowLaws.laws(Hashed.show(Int.show()), Hashed.eq(Int.eq()), Gen.int().hashed(Int.hash())),
      FoldableLaws.laws(Hashed.foldable(), Hashed.genK()),
      HashLaws.laws(Hashed.hash(), Gen.int().hashed(Int.hash()), Hashed.eq(Int.eq()))
    )
  }
}
