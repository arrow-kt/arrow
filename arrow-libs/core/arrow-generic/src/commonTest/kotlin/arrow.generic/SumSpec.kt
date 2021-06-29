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
data class Leaf<A>(val value: A) : Tree<A>()

@Serializable
data class Branch<A>(val left: Tree<A>, val right: Tree<A>) : Tree<A>()

class SumSpec : StringSpec({

  "Tree" {
    checkAll(Arb.string(), Arb.string(), Arb.string()) { a, b, c ->
      val tree: Tree<String> =
        Branch(Leaf(a), Branch(Leaf(b), Leaf(c)))

      Generic.encode(tree, serializersModule = serializersModule) shouldBe branch(leaf(a), branch(leaf(b), leaf(c)))
    }
  }
})

fun leaf(value: String): Generic<Tree<String>> =
  Generic.Coproduct(
    Generic.ObjectInfo(Tree::class.qualifiedName!!),
    Generic.ObjectInfo(Leaf::class.qualifiedName!!),
    listOf("value" to Generic.String(value)),
    0
  )

fun branch(left: Generic<Tree<String>>, right: Generic<Tree<String>>): Generic<Tree<String>> =
  Generic.Coproduct(
    Generic.ObjectInfo(Tree::class.qualifiedName!!),
    Generic.ObjectInfo(Branch::class.qualifiedName!!),
    listOf("left" to left, "right" to right),
    1
  )
