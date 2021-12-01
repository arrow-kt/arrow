//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)/[mapLeft](map-left.md)

# mapLeft

[common]\
inline fun &lt;[EE](map-left.md)&gt; [mapLeft](map-left.md)(f: ([E](index.md)) -&gt; [EE](map-left.md)): [Validated](index.md)&lt;[EE](map-left.md), [A](index.md)&gt;

Apply a function to an Invalid value, returning a new Invalid value. Or, if the original valid was Valid, return it.
