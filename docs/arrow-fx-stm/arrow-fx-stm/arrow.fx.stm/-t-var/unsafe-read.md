//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TVar](index.md)/[unsafeRead](unsafe-read.md)

# unsafeRead

[common]\
suspend fun [unsafeRead](unsafe-read.md)(): [A](index.md)

Read the value of a [TVar](index.md). This has no consistency guarantees for subsequent reads and writes since it is outside of a stm transaction.

Much faster than atomically { v.read() } because it avoids creating a transaction, it just reads the value.
