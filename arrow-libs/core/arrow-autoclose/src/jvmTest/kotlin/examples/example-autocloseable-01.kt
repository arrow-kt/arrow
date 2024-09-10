// This file was automatically generated from AutoCloseScope.kt by Knit tool. Do not edit.
@file:OptIn(ExperimentalStdlibApi::class)
package arrow.autocloseable.examples.exampleAutocloseable01

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

public fun main() {

Scanner("testRead.txt")
  .use { scanner ->
    Printer("testWrite.txt")
      .use { printer ->
        for(line in scanner) {
          printer.print(line)
        }
      }
  }

autoCloseScope {
  val scanner = install(Scanner("testRead.txt"))
  val printer = install(Printer("testWrite.txt"))
  for(line in scanner) {
    printer.print(line)
  }
}
}
