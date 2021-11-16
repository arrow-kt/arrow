// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated12

import arrow.core.Validated
import arrow.core.computations.either
import arrow.core.valid
import arrow.core.invalid
import arrow.core.NonEmptyList
import arrow.typeclasses.Semigroup

data class ConnectionParams(val url: String, val port: Int)

abstract class Read<A> {
 abstract fun read(s: String): A?

 companion object {

  val stringRead: Read<String> =
   object : Read<String>() {
    override fun read(s: String): String? = s
   }

  val intRead: Read<Int> =
   object : Read<Int>() {
    override fun read(s: String): Int? =
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
   }
 }
}

sealed class ConfigError {
 data class MissingConfig(val field: String) : ConfigError()
 data class ParseConfig(val field: String) : ConfigError()
}

data class Config(val map: Map<String, String>) {
  suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
    val value = Validated.fromNullable(map[key]) {
      ConfigError.MissingConfig(key)
    }.bind()
    val readVal = Validated.fromNullable(read.read(value)) {
      ConfigError.ParseConfig(key)
    }.bind()
    readVal
  }.toValidatedNel()
}

suspend fun main() {
val config = Config(mapOf("wrong field" to "127.0.0.1", "port" to "not a number"))

val valid = config.parse(Read.stringRead, "url").zip(
 Semigroup.nonEmptyList<ConfigError>(),
 config.parse(Read.intRead, "port")
) { url, port -> ConnectionParams(url, port) }
 println("valid = $valid")
}
