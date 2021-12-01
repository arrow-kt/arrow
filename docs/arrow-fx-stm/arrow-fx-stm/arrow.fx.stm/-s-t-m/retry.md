//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[retry](retry.md)

# retry

[common]\
abstract fun [retry](retry.md)(): [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)

Abort and retry the current transaction.

Aborts the transaction and suspends until any of the accessed [TVar](../-t-var/index.md)'s changed, after which the transaction will restart. Since all other datastructures are built upon [TVar](../-t-var/index.md)'s this automatically extends to those structures as well.

The main use for this is to abort once the transaction has hit an invalid state or otherwise needs to wait for changes.

import arrow.fx.stm.atomically\
import arrow.fx.stm.stm\
\
suspend fun main() {\
  //sampleStart\
  val result = atomically {\
    stm { retry() } orElse { "Alternative" }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-04.kt -->
