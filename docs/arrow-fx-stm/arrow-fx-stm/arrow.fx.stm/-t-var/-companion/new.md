//[arrow-fx-stm](../../../../index.md)/[arrow.fx.stm](../../index.md)/[TVar](../index.md)/[Companion](index.md)/[new](new.md)

# new

[common]\
suspend fun &lt;[A](new.md)&gt; [new](new.md)(a: [A](new.md)): [TVar](../index.md)&lt;[A](new.md)&gt;

Return a new [TVar](../index.md)

More efficient than atomically { newVar(a) } because it skips creating a transaction.
