package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlinx.serialization.Serializable

@Serializable
enum class Planet(
  val mass: Double,
  val radius: Double
) {
  MERCURY(3.303e+23, 2.4397e6),
  VENUS(4.869e+24, 6.0518e6),
  EARTH(5.976e+24, 6.37814e6),
  MARS(6.421e+23, 3.3972e6),
  JUPITER(1.9e+27, 7.1492e7),
  SATURN(5.688e+26, 6.0268e7),
  URANUS(8.686e+25, 2.5559e7),
  NEPTUNE(1.024e+26, 2.4746e7);
}

class EnumSpec : StringSpec({

  "Encoding enum should contain all values, and correct index" {
    checkAll(Arb.of(Planet.values())) { planet ->
      val res = Generic.encode(planet)
      val expected = Generic.Enum<Any?>(
          Generic.ObjectInfo(Planet::class.qualifiedName!!),
          Planet.values().map { Generic.EnumValue(it.name, it.ordinal) },
          planet.ordinal
        )

      res shouldBe expected
    }
  }
})
