package arrow.core

import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.Sample
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.choice
import io.kotest.property.checkAll

class EvalJvmTest : StringSpec({
  "stack safety stress test" {
    checkAll(DeepEval.gen) { d: DeepEval ->
      try {
        d.eval.value()
      } catch (e: StackOverflowError) {
        fail("stack overflowed with eval-depth ${DeepEval.maxDepth}")
      }
    }
  }
})

private data class DeepEval(val eval: Eval<Int>) {
  sealed class O {
    data class Map(val f: (Int) -> Int) : O()
    data class FlatMap(val f: (Int) -> Eval<Int>) : O()
    class Memoize : O()
    class Defer : O()

    companion object {
      val gen = Arb.choice(
        arbitrary { O.Map { it + 1 } },
        arbitrary { O.FlatMap { Eval.Now(it) } },
        arbitrary { O.Memoize() },
        arbitrary { O.Defer() },
      )
    }
  }

  companion object {
    const val maxDepth = 10000

    fun build(leaf: () -> Eval<Int>, os: List<O>) = run {
      tailrec fun step(i: Int, leaf: () -> Eval<Int>, cbs: MutableList<(Eval<Int>) -> Eval<Int>>): Eval<Int> =
        if (i >= os.size) {
          cbs.fold(leaf()) { e, f -> f(e) }
        } else {
          val o = os[i]
          when (o) {
            is O.Defer -> Eval.defer {
              @Suppress("NON_TAIL_RECURSIVE_CALL")
              step(i + 1, leaf, cbs)
            }
            is O.Memoize -> step(i + 1, leaf, cbs.also { it.add(0) { e: Eval<Int> -> e.memoize() } })
            is O.Map -> step(i + 1, leaf, cbs.also { it.add(0) { e: Eval<Int> -> e.map(o.f) } })
            is O.FlatMap -> step(i + 1, leaf, cbs.also { it.add(0) { e: Eval<Int> -> e.flatMap(o.f) } })
          }
        }

      step(0, leaf, mutableListOf())
    }

    val gen = arbitrary { rs ->
      val leaf = { Eval.Now(0) }
      val eval = build(leaf, O.gen.samples().map(Sample<O>::value).take(maxDepth).toList())
      DeepEval(eval)
    }
  }
}
