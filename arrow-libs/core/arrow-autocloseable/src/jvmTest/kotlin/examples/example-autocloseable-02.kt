// This file was automatically generated from AutoCloseScope.kt by Knit tool. Do not edit.
package arrow.autocloseable.examples.exampleAutocloseable02

import arrow.AutoCloseScope
import arrow.install
import java.io.File
import java.io.PrintWriter
import java.util.Scanner

context(AutoCloseScope)
@ExperimentalStdlibApi
fun copyFiles() {
  val scanner = install(Scanner(File("testRead.txt")))
  val printer = install(PrintWriter(File("testWrite.txt")))
  for(line in scanner) {
    printer.print(line)
  }
}
