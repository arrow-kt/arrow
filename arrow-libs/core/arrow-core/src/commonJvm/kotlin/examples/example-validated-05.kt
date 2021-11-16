// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated05

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.valid
import arrow.core.invalid

data class Config(val map: Map<String, String>) {
 fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
  val v = map[key]
  return when (v) {
   null -> Validated.Invalid(ConfigError.MissingConfig(key))
   else ->
    when (val s = read.read(v)) {
     null -> ConfigError.ParseConfig(key).invalid()
     else -> s.valid()
    }
  }
 }
}
