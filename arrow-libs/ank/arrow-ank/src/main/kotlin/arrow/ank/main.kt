@file:JvmName("main")

package arrow.ank

import arrow.fx.IO
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.unsafe
import java.nio.file.Paths

fun main(vararg args: String) {
  unsafe {
    runBlocking {
      when {
        args.size > 1 -> {
          IO.concurrent().ank(
            Paths.get(args[0]),
            Paths.get(args[1]),
            args.drop(2),
            interpreter
          )
        }
        else -> throw IllegalArgumentException("Required first 2 args as directory paths in this order: <required: source> <required: destination> <optional: classpath entries, one per arg..>")
      }
    }
  }
}
