package arrow.optics.test.generators

import io.kotest.property.Arb

fun Gen.Companion.char(): Arb<Char> =
  Gen.from(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())
