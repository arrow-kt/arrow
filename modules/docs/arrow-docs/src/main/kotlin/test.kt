import arrow.core.ListK
import arrow.core.Tuple2
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.k

fun main() {
  ListK.applicative().map(listOf(1, 2).k(), listOf(3, 4).k()) { (a, b) ->
    Tuple2(a, b)
  }.let(::println)
}
