package arrow.core

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.edgecases
import kotlin.jvm.JvmOverloads
import kotlin.random.nextInt

@JvmOverloads
inline fun <reified A> Arb.Companion.array(
  gen: Arb<A>,
  range: IntRange = 0..100
): Arb<Array<A>> {
  check(!range.isEmpty())
  check(range.first >= 0)
  return arb(edgecases = emptyArray<A>() prependTo gen.edgecases().map { arrayOf(it) }) {
    sequence {
      val genIter = gen.generate(it).iterator()
      while (true) {
        val targetSize = it.random.nextInt(range)
        val list = ArrayList<A>(targetSize)
        while (list.size < targetSize && genIter.hasNext()) {
          list.add(genIter.next().value)
        }
        check(list.size == targetSize)
        yield(list.toTypedArray())
      }
    }
  }
}
