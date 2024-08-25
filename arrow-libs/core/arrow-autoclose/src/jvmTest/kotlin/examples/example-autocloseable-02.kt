// This file was automatically generated from AutoCloseScope.kt by Knit tool. Do not edit.
@file:OptIn(ExperimentalStdlibApi::class)
package arrow.autocloseable.examples.exampleAutocloseable02

import arrow.AutoCloseScope
import arrow.autoCloseScope

public class Scanner(
  private val path: String,
) : AutoCloseable, Iterable<String> by listOf("Hello", "World", "!") {
  override fun close(): Unit = Unit
}

public class Printer(private val path: String) : AutoCloseable {
  public fun print(line: String): Unit = Unit
  override fun close(): Unit = Unit
}

context(AutoCloseScope)
fun copyFiles(input: String, output: String) {
  val scanner = install(Scanner(input))
  val printer = install(Printer(output))
  for(line in scanner) {
    printer.print(line)
  }
}
