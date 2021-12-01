//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[flatMap](flat-map.md)

# flatMap

[common]\
inline fun &lt;[B](flat-map.md)&gt; [flatMap](flat-map.md)(f: ([A](index.md)) -&gt; [Option](index.md)&lt;[B](flat-map.md)&gt;): [Option](index.md)&lt;[B](flat-map.md)&gt;

Returns the result of applying $f to this $option's value if this $option is nonempty. Returns $none if this $option is empty. Slightly different from map in that $f is expected to return an $option (which could be $none).

## See also

common

| | |
|---|---|
| [arrow.core.Option](map.md) |  |

## Parameters

common

| | |
|---|---|
| f | the function to apply |
