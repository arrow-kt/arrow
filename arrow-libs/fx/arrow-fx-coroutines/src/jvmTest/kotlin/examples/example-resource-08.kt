// This file was automatically generated from Resource.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleResource08

import arrow.fx.coroutines.asFlow
import arrow.fx.coroutines.closeable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.nio.file.Path
import kotlin.io.path.useLines

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
