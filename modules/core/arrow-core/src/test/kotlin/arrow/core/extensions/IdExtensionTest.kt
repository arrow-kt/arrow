package arrow.core.extensions

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.id.foldable.foldable
import arrow.core.extensions.id.semialign.semialign
import arrow.test.UnitSpec
import arrow.test.laws.SemialignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class IdExtensionTest : UnitSpec() {
  init {
    testLaws(SemialignLaws.laws(Id.semialign(),
      Gen.id(Gen.int()) as Gen<Kind<ForId, Int>>,
      { Id.eq(it) as Eq<Kind<ForId, *>> },
      Id.foldable()
    ))
  }
}

fun <T> Gen.Companion.id(gen: Gen<T>): Gen<Id<T>> = object : Gen<Id<T>> {
  override fun constants(): Iterable<Id<T>> =
    gen.constants().map { Id.just(it) }

  override fun random(): Sequence<Id<T>> =
    gen.random().map { Id.just(it) }
}
