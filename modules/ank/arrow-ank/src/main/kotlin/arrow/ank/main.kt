@file:JvmName("main")

package arrow.ank

import arrow.core.Try
import java.io.File

fun main(vararg args: String) {
  when {
    args.size > 1 -> {
      Try { ank(File(args[0]), File(args[1]), args.drop(2)) }
        .fold({ ex ->
          throw ex
        }, { files ->
          println("ΛNK Generated ${files.size} files")
        })
    }
    else -> throw IllegalArgumentException("Required first 2 args as directory paths in this order: <required: source> <required: destination> <optional: classpath entries, one per arg..>")
  }
}
