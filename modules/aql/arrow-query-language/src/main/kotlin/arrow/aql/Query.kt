package arrow.aql

import arrow.Kind

data class Query<out F, A, out Z>(
  val select: Selection<A, Z>,
  val from: Source<F, A>
)

