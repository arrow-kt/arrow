package arrow.test.laws

import arrow.data.MapKW
import arrow.data.k
import arrow.instances.MapKWMonoidInstance
import arrow.typeclasses.Eq
import arrow.typeclasses.eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MapKWLaws {
    inline fun <reified A, reified B> laws(monoid: MapKWMonoidInstance<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B?> = eq(), eqMapKW: Eq<MapKW<A, B>> = eq()): List<Law> = listOf(
            Law("Monoid Laws: identity", { monoidIdentity(monoid, aGen, bGen, EQB) }),
            Law("Semigroup laws: associativity", { associativity(monoid, aGen, bGen, eqMapKW) })
    )

    inline fun <reified A, reified B> monoidIdentity(monoid: MapKWMonoidInstance<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B?>): Unit {
        val identity = monoid.empty()
        forAll(aGen, bGen, { a, b ->
            val map = mapOf(a to b).k()
            monoid.combine(identity, map)[a].equalUnderTheLaw(map[a], EQB)
        })
        forAll(aGen, bGen, { a, b ->
            val map = mapOf(a to b).k()
            monoid.combine(map, identity)[a].equalUnderTheLaw(map[a], EQB)
        })
    }

    inline fun <reified A, reified B> associativity(monoid: MapKWMonoidInstance<A, B>, aGen: Gen<A>, bGen : Gen<B>, eqMapKW: Eq<MapKW<A, B>>): Unit {
        forAll(aGen, bGen, bGen, bGen, { k: A, a: B, b: B, c: B ->
            val mapA = mapOf(k to a).k()
            val mapB = mapOf(k to b).k()
            val mapC = mapOf(k to c).k()

            monoid.combine(mapA, monoid.combine(mapB, mapC)).equalUnderTheLaw(monoid.combine(monoid.combine(mapA, mapB), mapC), eqMapKW)
        })
    }
}
