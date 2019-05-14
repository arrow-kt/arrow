package arrow.effects.data

import arrow.Kind
import arrow.effects.typeclasses.Duration
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.generators.timeUnit
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class DurationTest : UnitSpec() {

  init {
    "plus should be commutative" {
      forAll(Gen.intSmall(), Gen.timeUnit(), Gen.intSmall(), Gen.timeUnit()) { i, u, j, v ->
        val a = Duration(i.toLong(), u)
        val b = Duration(j.toLong(), v)
        a + b == b + a
      }
    }
  }
}

interface Functor<A> {
  fun <B> map(f: (A) -> B): Functor<B>
  interface Companion {
    fun <A> just(a: A): Functor<A>
  }
}

class ForId private constructor() { companion object }
typealias IdOf<A> = arrow.Kind<ForId, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> IdOf<A>.fix(): Id<A> =
  this as Id<A>

data class Id<A>(val value: A) : Functor<A> {
  override fun <B> map(f: (A) -> B): Id<B> = Id(f(value))
  companion object : Functor.Companion {
    override fun <A> just(a: A): Id<A> = Id(a)
  }
}

fun test(fa: Functor<Int>): Functor<Int> =
  fa.map { it + 1 }

fun main() {
  test(Id(1)).let(::println)
}
