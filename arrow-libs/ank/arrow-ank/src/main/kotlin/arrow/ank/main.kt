@file:JvmName("main")

package arrow.ank

import java.nio.file.Paths

public suspend fun main(vararg args: String): Unit =
  when {
    args.size > 1 -> {
      ank(
        Paths.get(args[0]),
        Paths.get(args[1]),
        args.drop(2),
        interpreter
      )
    }
    else -> throw IllegalArgumentException("Required first 2 args as directory paths in this order: <required: source> <required: destination> <optional: classpath entries, one per arg..>")
  }
