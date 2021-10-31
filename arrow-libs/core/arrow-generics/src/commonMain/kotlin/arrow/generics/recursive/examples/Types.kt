package arrow.generics.recursive.examples

import arrow.generics.examples.* // ktlint-disable no-wildcard-imports
import arrow.generics.recursive.* // ktlint-disable no-wildcard-imports

public typealias TreeRepr = Sum2<Product1<ParR>, Product2<RecR, RecR>>

public fun <A> Tree<A>.toGeneric(): Generic<TreeRepr, A> = when (this) {
  is Leaf<A> ->
    This("Leaf", And("x", Par(x), EndD(0)))
  is Node<A> ->
    That(This("Node",
      And("left", Rec(left.toGeneric()),
        And("right", Rec(right.toGeneric()),
          EndD(0)))))
}

public fun <A> Generic<TreeRepr, A>.fromGeneric(): Tree<A> = when (this) {
  is This -> when (this.value) {
    is And -> when (this.value.value) {
      is Par -> Leaf(this.value.value.value)
      else -> throw IllegalStateException()
    }
    else -> throw IllegalStateException()
  }
  is That -> when (this.next) {
    is This -> when (this.next.value) {
      is And -> when (this.next.value.value) {
        is Rec -> when (this.next.value.rest) {
          is And -> when (this.next.value.rest.value) {
            is Rec -> Node(this.next.value.value.value.fromGeneric(), this.next.value.rest.value.value.fromGeneric())
            else -> throw IllegalStateException()
          }
          else -> throw IllegalStateException()
        }
        else -> throw IllegalStateException()
      }
      else -> throw IllegalStateException()
    }
    else -> throw IllegalStateException()
  }
}

