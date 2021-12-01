//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PIso](../index.md)/[Companion](index.md)/[id](id.md)

# id

[common]\
fun &lt;[S](id.md)&gt; [id](id.md)(): [Iso](../../index.md#1786632304%2FClasslikes%2F-617900156)&lt;[S](id.md), [S](id.md)&gt;

create an [PIso](../index.md) between any type and itself. Id is the zero element of optics composition, for any optic o of type O (e.g. PLens, Prism, POptional, ...): o compose Iso.id == o
