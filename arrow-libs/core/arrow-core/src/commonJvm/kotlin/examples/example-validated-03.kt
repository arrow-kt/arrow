// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated03

sealed class ConfigError {
 data class MissingConfig(val field: String): ConfigError()
 data class ParseConfig(val field: String): ConfigError()
}
