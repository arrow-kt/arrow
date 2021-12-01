//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[map](map.md)

# map

[common]\
inline fun &lt;[B](map.md)&gt; [map](map.md)(f: ([A](index.md)) -&gt; [B](map.md)): [Option](index.md)&lt;[B](map.md)&gt;

Returns a Some<$B> containing the result of applying $f to this $option's value if this $option is nonempty. Otherwise return $none.

## See also

common

| | |
|---|---|
| [arrow.core.Option](flat-map.md) |  |

## Parameters

common

| | |
|---|---|
| f | the function to apply |
