//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[all](all.md)

# all

[common]\
inline fun [all](all.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if this option is empty '''or''' the predicate $predicate returns true when applied to this $option's value.

## Parameters

common

| | |
|---|---|
| predicate | the predicate to test |
