//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[mapNotNull](map-not-null.md)

# mapNotNull

[common]\
inline fun &lt;[B](map-not-null.md)&gt; [mapNotNull](map-not-null.md)(f: ([A](index.md)) -&gt; [B](map-not-null.md)?): [Option](index.md)&lt;[B](map-not-null.md)&gt;

Returns $none if the result of applying $f to this $option's value is null. Otherwise returns the result.

## Parameters

common

| | |
|---|---|
| f | the function to apply. |
