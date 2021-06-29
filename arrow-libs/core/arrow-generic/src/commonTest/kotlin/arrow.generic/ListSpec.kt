package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class ListSpec : StringSpec({
  "top-level list" {
    checkAll(Arb.list(Arb.int())) { l ->
      Generic.encode(l) shouldBe list(l)
    }
  }

  "list inside polymorphic product" {
    checkAll(Arb.list(Arb.int())) { l ->
      Generic.encode(Id(l)) shouldBe list(l).id()
    }
  }

  "list inside inline" {
    checkAll(Arb.list(Arb.int().map(::IInt))) { l ->
      Generic.encode(Id(l)) shouldBe list(l).id()
    }
  }

  // TODO Caused by SerializationException: Class 'ArrayList' is not registered for polymorphic serialization in the scope of 'Any'.
  "list inside sum-type".config(enabled = false) {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b, c ->
      val tree: Tree<List<Int>> =
        Branch(Leaf(a), Branch(Leaf(b), Leaf(c)))

      Generic.encode(tree) shouldBe branch(leaf(list(a)), branch(leaf(list(b)), leaf(list(c))))
    }
  }
})

inline fun <reified A> list(
  list: List<A>,
  serializer: KSerializer<A> = serializer()
): Generic<List<A>> =
  Generic.Product(
    Generic.Info("kotlin.collections.ArrayList"),
    list.mapIndexed { index: Int, a: A ->
      Pair(index.toString(), Generic.encode(a, serializer))
    }
  )
