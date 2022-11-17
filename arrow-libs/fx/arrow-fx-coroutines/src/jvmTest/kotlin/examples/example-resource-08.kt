// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource08

import arrow.fx.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.nio.file.Path
import kotlin.io.path.*

fun Flow<ByteArray>.writeAll(path: Path): Flow<Unit> =
  closeable { path.toFile().outputStream() }
    .asFlow()
    .flatMapConcat { writer -> map { writer.write(it) } }
    .flowOn(Dispatchers.IO)

fun Path.readAll(): Flow<String> = flow {
  useLines { lines -> emitAll(lines.asFlow()) }
}

suspend fun main() {
  Path.of("example.kt")
    .readAll()
    .collect(::println)
}
