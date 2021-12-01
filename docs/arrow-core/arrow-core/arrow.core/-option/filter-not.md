//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[filterNot](filter-not.md)

# filterNot

[common]\
inline fun [filterNot](filter-not.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](index.md)&lt;[A](index.md)&gt;

Returns this $option if it is nonempty '''and''' applying the predicate $p to this $option's value returns false. Otherwise, return $none.

## Parameters

common

| | |
|---|---|
| predicate | the predicate used for testing. |
