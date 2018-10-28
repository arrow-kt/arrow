package arrow.data

fun <A, B> IterableHolder<A>.mapK(f: (A) -> B): ListK<B> = getWrappedIterable().map(f).k()
