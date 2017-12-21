@file:JvmName("main")
package io.kategory.ank

import arrow.Either
import arrow.ListKW
import arrow.ev
import arrow.monadError
import io.arrow.ank.ank
import io.arrow.ank.ankMonadErrorInterpreter
import io.arrow.ank.run
import java.io.File

typealias Target<A> = Either<Throwable, A>

fun main(vararg args: String) {
    when {
        args.size > 1 -> {
            val ME = Either.monadError<Throwable>()
            ank(File(args[0]), File(args[1]), ListKW(args.drop(2)))
                    .ev()
                    .run(ankMonadErrorInterpreter(ME), ME).ev()
                    .fold({ ex ->
                        throw ex
                    }, { files ->
                        println("ΛNK Generated:\n\t${files.joinToString(separator = "\n\t")}")
                    })
        }
        else -> throw IllegalArgumentException("Required first 2 args as directory paths in this order: <required: source> <required: destination> <optional: classpath entries, one per arg..>")
    }
}
