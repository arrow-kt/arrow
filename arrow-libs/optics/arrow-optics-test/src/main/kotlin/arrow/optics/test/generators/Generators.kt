package arrow.optics.test.generators

import arrow.core.test.generators.nonEmptyList
import io.kotlintest.properties.Gen

fun Gen.Companion.char(): Gen<Char> =
  Gen.from(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())

fun <A> Gen.Companion.iterable(gen: Gen<A>): Gen<Iterable<A>> =
  oneOf(list(gen), set(gen), nonEmptyList(gen))
