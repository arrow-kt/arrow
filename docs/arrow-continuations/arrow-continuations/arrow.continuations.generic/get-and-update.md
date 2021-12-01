//[arrow-continuations](../../index.md)/[arrow.continuations.generic](index.md)/[getAndUpdate](get-and-update.md)

# getAndUpdate

[common]\
inline fun &lt;[V](get-and-update.md)&gt; [AtomicRef](-atomic-ref/index.md)&lt;[V](get-and-update.md)&gt;.[getAndUpdate](get-and-update.md)(function: ([V](get-and-update.md)) -&gt; [V](get-and-update.md)): [V](get-and-update.md)

Updates variable atomically using the specified [function](get-and-update.md) of its value and returns its old value.
