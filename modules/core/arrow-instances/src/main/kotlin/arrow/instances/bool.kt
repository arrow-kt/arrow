package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Order

interface BooleanEqInstance : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

interface BooleanOrderInstance: Order<Boolean>, BooleanEqInstance {
  override fun Boolean.compare(b: Boolean): Int =
          if (this == b) 0 else if (this) 1 else -1

  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

object Bool

fun Bool.eq(): Eq<Boolean> = lazyOf( object: BooleanEqInstance {}).value
fun Bool.order(): Order<Boolean> = lazyOf( object: BooleanOrderInstance {}).value


