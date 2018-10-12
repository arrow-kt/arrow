package test

import arrow.core.*
import arrow.data.*
import arrow.optics.*

data class HealthPack(val amountLeft: Int)
object OutOfPacks
data class Inventory(val item: Either<OutOfPacks, HealthPack>)

val healthPack: Optional<Inventory, HealthPack> = Optional(
  getOrModify = { player -> player.item.fold({ player.left() }, { it.right() }) },
  set = { healthPack -> { player -> player.copy(item = healthPack.right()) } }
)

val inv = Inventory(HealthPack(50).right())

val inspect = healthPack.extract()

val useHealthPack = healthPack.mod { it.copy(amountLeft = it.amountLeft - 50) }


val pickUpNew = healthPack.assign_(HealthPack(100))


fun main(args: Array<String>) {
  inspect.run(inv).let(::println)
  inspect.run(Inventory(OutOfPacks.left())).let(::println)
  useHealthPack.run(inv).let(::println)
  pickUpNew.run(inv).let(::println)
}