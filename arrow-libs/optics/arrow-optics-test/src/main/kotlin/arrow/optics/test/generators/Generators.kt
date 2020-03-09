package arrow.optics.test.generators

import io.kotlintest.properties.Gen

fun Gen.Companion.char(): Gen<Char> =
  Gen.from(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())
