package arrow.aql

data class Query<out F, A, out Z>(
  val select: Selection<A, Z>,
  val from: Source<F, A>
)
