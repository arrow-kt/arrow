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

fun tree(value: Tree<String>): Generic<Tree<String>> =
  when (value) {
    is Leaf -> leaf(Generic.String(value.value))
    is Branch -> branch(tree(value.left), tree(value.right))
  }

fun leaf(value: String): Generic<Tree<String>> =
  leaf(Generic.String(value))

inline fun <reified A> leaf(value: Generic<A>): Generic<Tree<A>> =
  Generic.Coproduct(
    Generic.Info(Tree::class.qualifiedName!!),
    Generic.Info(Leaf::class.qualifiedName!!),
    listOf("value" to value),
    0
  )

inline fun <reified A> branch(left: Generic<Tree<A>>, right: Generic<Tree<A>>): Generic<Tree<A>> =
  Generic.Coproduct(
    Generic.Info(Tree::class.qualifiedName!!),
    Generic.Info(Branch::class.qualifiedName!!),
    listOf("left" to left, "right" to right),
    1
  )
