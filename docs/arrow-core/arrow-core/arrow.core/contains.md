//[arrow-core](../../index.md)/[arrow.core](index.md)/[contains](contains.md)

# contains

[common]\
fun &lt;[A](contains.md), [B](contains.md)&gt; [Either](-either/index.md)&lt;[A](contains.md), [B](contains.md)&gt;.[contains](contains.md)(elem: [B](contains.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Returns true if this is a [Right](-either/-right/index.md) and its value is equal to elem (as determined by ==), returns false otherwise.

Example:

&lt;!--- KNIT example-arrow-core-either-contains-01.kt --&gt;\
Right("something").contains("something") // Result: true\
Right("something").contains("anything")  // Result: false\
Left("something").contains("something")  // Result: false

#### Return

true if the option has an element that is equal (as determined by ==) to elem, false otherwise.

## Parameters

common

| | |
|---|---|
| elem | the element to test. |
