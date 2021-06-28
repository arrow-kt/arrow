package arrow.generic

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.serialization.Serializable

@Serializable
sealed class Tree<A>

@Serializable
data class Branch<A>(val left: Tree<A>, val right: Tree<A>) : Tree<A>()

@Serializable
data class Leaf<A>(val value: A) : Tree<A>()

class SumSpec : StringSpec({

  "Tree" {
    checkAll(Arb.string(), Arb.string(), Arb.string()) { a, b, c ->
      val tree: Branch<String> =
        Branch(Leaf(a), Branch(Leaf(b), Leaf(c)))

      Generic.encode(tree, serializersModule = serializersModule) shouldBe Generic.Coproduct(
        Generic.ObjectInfo(Tree::class.qualifiedName!!),
        listOf(
          Generic.Product<Leaf<String>>(Generic.ObjectInfo(Leaf::class.qualifiedName!!), listOf("value" to Generic.String(a))),
          Generic.Product(
            Generic.ObjectInfo(Branch::class.qualifiedName!!),
            "left" to Generic.Product<Leaf<String>>(Generic.ObjectInfo(Leaf::class.qualifiedName!!), listOf("value" to Generic.String(b))),
            "left" to Generic.Product<Leaf<String>>(Generic.ObjectInfo(Leaf::class.qualifiedName!!), listOf("value" to Generic.String(c))),
          )
        )
      )
    }
  }
})
