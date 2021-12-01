//[arrow-continuations](../../../index.md)/[arrow.continuations.generic](../index.md)/[AtomicRef](index.md)/[compareAndSet](compare-and-set.md)

# compareAndSet

[common, js, native]\
[common, js, native]\
fun [compareAndSet](compare-and-set.md)(expected: [V](index.md), new: [V](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Compare current value with expected and set to new if they're the same. Note, 'compare' is checking the actual object id, not 'equals'.
