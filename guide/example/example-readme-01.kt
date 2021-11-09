// This file was automatically generated from README.md by Knit tool. Do not edit.
package example.exampleReadme01

import arrow.Cont
import arrow.cont

object EmptyPath

fun readFile(path: String): Cont<EmptyPath, Unit> = cont {
  if (path.isNotEmpty()) shift(EmptyPath) else Unit
}

fun readFile2(path: String?): Cont<EmptyPath, Unit> = cont {
  ensure(!path.isNullOrBlank()) { EmptyPath }
}
