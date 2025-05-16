package arrow.integrations.jackson.module

import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string

data class SomeObject(val someString: String, val someInt: Int)

fun Arb.Companion.someObject(): Arb<SomeObject> = Arb.bind(Arb.string(), Arb.int()) { str, int -> SomeObject(str, int) }

data class MapContainer<V>(val value: Map<Key, V>) {
  enum class Key { First, Second }
}

fun <V> arbMapContainer(arbValue: Arb<V>): Arb<MapContainer<V>> =
  Arb.map(Arb.of(MapContainer.Key.First, MapContainer.Key.Second), arbValue)
    .map { MapContainer(it) }
