package arrow.data

fun <A, B> Iterable<A>.mapK(f: (A) -> B): ListK<B> = map(f).k()

fun <A, B> Sequence<A>.mapK(f: (A) -> B): SequenceK<B> = map(f).k()
