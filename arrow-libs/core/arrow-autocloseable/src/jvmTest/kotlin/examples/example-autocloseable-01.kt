// This file was automatically generated from AutoCloseScope.kt by Knit tool. Do not edit.
package arrow.autocloseable.examples.exampleAutocloseable01

import arrow.autoCloseScope
import arrow.install
import java.io.File
import java.io.PrintWriter
import java.util.Scanner

@ExperimentalStdlibApi
fun main() = autoCloseScope {
  val scanner = install(Scanner(File("testRead.txt")))
  val printer = install(PrintWriter(File("testWrite.txt")))
  for(line in scanner) {
    printer.print(line)
  }
}
