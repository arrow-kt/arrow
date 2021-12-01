//[arrow-fx-stm](../../index.md)/[arrow.fx.stm](index.md)/[atomically](atomically.md)

# atomically

[common]\
suspend fun &lt;[A](atomically.md)&gt; [atomically](atomically.md)(f: [STM](-s-t-m/index.md).() -&gt; [A](atomically.md)): [A](atomically.md)

Run a transaction to completion.

This comes with the guarantee that, at the time of committing the transaction, all read variables have a consistent state (they have not changed after the first read). Otherwise the transaction will be aborted and run again.

Note that only reads and writes inside a single transaction have this guarantee. Code that calls [atomically](atomically.md) as follows will again be subject to race conditions: atomically { v.read() }.let { atomically { v.write(it + 1) } }. Because those are separate transactions the value inside v might change between transactions! The only safe way is to do it in one go: atomically { v.write(v.read() + 1) }

Transactions that only read or access completely disjoint set of [TVar](-t-var/index.md)'s will be able to commit in parallel as [STM](-s-t-m/index.md) in arrow uses an approach the locks only modified [TVar](-t-var/index.md)'s on commit. Only calls to [STM.write](-s-t-m/write.md) need to be synchronized, however the performance of [STM](-s-t-m/index.md) is still heavily linked to the amount of [TVar](-t-var/index.md)'s accessed so it is good practice to keep transactions short.

Keeping transactions short has another benefit which comes from another drawback of [STM](-s-t-m/index.md): There is no notion of fairness when it comes to transactions. The fastest transaction always wins. This can be problematic if a large number of small transactions starve out a larger transaction by forcing it to retry a lot. In practice this rarely happens, however to avoid such a scenario it is recommended to keep transactions small.

This may suspend if [STM.retry](-s-t-m/retry.md) is called and no accessed [TVar](-t-var/index.md) changed. It will then resume automatically after any accessed [TVar](-t-var/index.md) changed.

Rethrows all exceptions not caught by inside [f](atomically.md). Remember to use [STM.catch](-s-t-m/catch.md) to handle exceptions as try {} catch will not handle transaction state properly!
