package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.serializer

class MapSpec : StringSpec({
  "top-level map" {
    checkAll(Arb.map(Arb.string(), Arb.int())) { map ->
      Generic.encode(map) shouldBe map(map)
    }
  }

  "map inside polymorphic product" {
    checkAll(Arb.map(Arb.string(), Arb.int())) { map ->
      Generic.encode(Id(map)) shouldBe map(map).id()
    }
  }

  "map inside inline" {
    checkAll(Arb.map(Arb.string().map(::IString), Arb.int().map(::IInt))) { map ->
      Generic.encode(Id(map)) shouldBe map(map).id()
    }
  }

  "map inside sum-type".config(enabled = false) {
    checkAll(
      Arb.map(Arb.string(), Arb.int()),
      Arb.map(Arb.string(), Arb.int()),
      Arb.map(Arb.string(), Arb.int())
    ) { a, b, c ->
      val tree: Tree<Map<String, Int>> =
        Branch(Leaf(a), Branch(Leaf(b), Leaf(c)))

      Generic.encode(tree,
//         TODO Caused by SerializationException: Class 'LinkedHashMap' is not registered for polymorphic serialization in the scope of 'Any'.
        Tree.serializer(MapSerializer(String.serializer(), Int.serializer())),
        serializersModule = serializersModule
      ) shouldBe branch(leaf(map(a)), branch(leaf(map(b)), leaf(map(c))))
    }
  }
})

inline fun <reified A, reified B> map(
  map: Map<A, B>,
  serializerA: KSerializer<A> = serializer(),
  serializerB: KSerializer<B> = serializer()
): Generic<List<A>> {
  var index = 0
  return Generic.Product(
    Generic.Info("kotlin.collections.LinkedHashMap"),
    map.map { (a, b) ->
      "${index++}" to Generic.Product<Any?>(
        Generic.Info(Pair::class.qualifiedName!!),
        listOf(
          "first" to Generic.encode(a, serializerA),
          "second" to Generic.encode(b, serializerB)
        )
      )
    }
  )
}
