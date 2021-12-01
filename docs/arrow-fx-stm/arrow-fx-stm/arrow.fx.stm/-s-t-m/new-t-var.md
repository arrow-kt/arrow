//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[newTVar](new-t-var.md)

# newTVar

[common]\
open fun &lt;[A](new-t-var.md)&gt; [newTVar](new-t-var.md)(a: [A](new-t-var.md)): [TVar](../-t-var/index.md)&lt;[A](new-t-var.md)&gt;

Create a new [TVar](../-t-var/index.md) inside a transaction, because [TVar.new](../-t-var/-companion/new.md) is not possible inside [STM](index.md) transactions.
