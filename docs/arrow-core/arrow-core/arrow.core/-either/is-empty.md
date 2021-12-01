//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Either](index.md)/[isEmpty](is-empty.md)

# isEmpty

[common]\
fun [isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if [Left](-left/index.md)

Example:

&lt;!--- KNIT example-either-49.kt --&gt;\
Left("foo").isEmpty()  // Result: true\
Right("foo").isEmpty() // Result: false<!--- KNIT example-either-50.kt -->
