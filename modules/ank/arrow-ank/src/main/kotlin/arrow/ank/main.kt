@file:JvmName("main")

package arrow.ank

import arrow.core.Try
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.instances.io.monadDefer.monadDefer
import java.nio.file.Paths

fun main(vararg args: String) {
  when {
    args.size > 1 -> {
      Try { ank(Paths.get(args[0]), Paths.get(args[1]), args.drop(2), monadDeferInterpreter(IO.monadDefer())).fix().unsafeRunSync() }
        .fold({ ex ->
          throw ex
        }, { files ->
          println("ΛNK Generated $files files")
        })
    }
    else -> throw IllegalArgumentException("Required first 2 args as directory paths in this order: <required: source> <required: destination> <optional: classpath entries, one per arg..>")
  }
}
