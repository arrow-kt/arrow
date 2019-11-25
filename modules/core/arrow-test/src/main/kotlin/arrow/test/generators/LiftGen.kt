package arrow.test.generators

import arrow.Kind
import arrow.core.ForId
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.ForSequenceK
import arrow.core.Id
import arrow.core.ListK
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.SequenceK
import io.kotlintest.properties.Gen

interface LiftGen<F> {
  /**
   * lifts a Gen<A> to the context F. the resulting Gen can be used to create types Kind<F, A>
   */
  fun <A> liftGen(gen: Gen<A>): Gen<Kind<F, A>>
}

fun Option.Companion.liftGen() = object : LiftGen<ForOption> {
  override fun <A> liftGen(gen: Gen<A>): Gen<Kind<ForOption, A>> =
    Gen.option(gen) as Gen<Kind<ForOption, A>>
}

fun Id.Companion.liftGen() = object : LiftGen<ForId> {
  override fun <A> liftGen(gen: Gen<A>): Gen<Kind<ForId, A>> =
    Gen.id(gen) as Gen<Kind<ForId, A>>
}

fun ListK.Companion.liftGen() = object : LiftGen<ForListK> {
  override fun <A> liftGen(gen: Gen<A>): Gen<Kind<ForListK, A>> =
    Gen.listK(gen) as Gen<Kind<ForListK, A>>
}

fun NonEmptyList.Companion.liftGen() = object : LiftGen<ForNonEmptyList> {
  override fun <A> liftGen(gen: Gen<A>): Gen<Kind<ForNonEmptyList, A>> =
    Gen.nonEmptyList(gen) as Gen<Kind<ForNonEmptyList, A>>
}

fun SequenceK.Companion.liftGen() = object : LiftGen<ForSequenceK> {
  override fun <A> liftGen(gen: Gen<A>): Gen<Kind<ForSequenceK, A>> =
    Gen.sequenceK(gen) as Gen<Kind<ForSequenceK, A>>
}

fun <T> Gen.Companion.id(gen: Gen<T>): Gen<Id<T>> = object : Gen<Id<T>> {
  override fun constants(): Iterable<Id<T>> =
    gen.constants().map { Id.just(it) }

  override fun random(): Sequence<Id<T>> =
    gen.random().map { Id.just(it) }
}
