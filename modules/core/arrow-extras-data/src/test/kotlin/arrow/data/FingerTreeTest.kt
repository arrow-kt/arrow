package arrow.data

import arrow.core.None
import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.fingertree.internal.Affix.*
import arrow.data.fingertree.FingerTree
import arrow.data.fingertree.FingerTree.*
import arrow.data.fingertree.internal.Node
import arrow.data.fingertree.internal.Node.Branch2
import arrow.data.fingertree.internal.Node.Branch3
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FingerTreeTest : StringSpec() {

  init {

    /**
     * prepend()
     */

    "prepend() should return a single finger tree when the finger tree is empty" {
      val fingerTree = Empty<Int>()

      fingerTree.prepend(1) shouldBe Single(1)
    }

    "prepend() should return a deep finger tree with one prefix and one suffix element when the finger tree has only one element" {
      val fingerTree = Single(1)

      fingerTree.prepend(2) shouldBe Deep(One(2), Empty(), One(1))
    }

    "prepend() should add the item to the first place of the tree prefix when the finger tree has three prefix elements and an empty deeper tree" {
      val fingerTree = Deep(Three(1, 2, 3), Empty(), One(4))

      fingerTree.prepend(5) shouldBe Deep(Four(5, 1, 2, 3), Empty(), One(4))
    }

    "prepend() should create a finger tree with the new affix, the result of the prepend on the deeper tree and the same suffix when the prefix has four elements" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>(relaxed = true)
      val fingerTree = Deep(Four(1, 2, 3, 4), mockDeeperFingerTree, One(5))

      val dummyDeeperFingerTree: FingerTree<Node<Int>> = Single(Branch3(2, 3, 4))
      every { mockDeeperFingerTree.prepend(any()) } returns dummyDeeperFingerTree

      fingerTree.prepend(6) shouldBe Deep(Two(6, 1), dummyDeeperFingerTree, One(5))
    }

    /**
     * append()
     */

    "append() should return a single finger tree when the finger tree is empty" {
      val fingerTree = Empty<Int>()

      fingerTree.append(1) shouldBe Single(1)
    }

    "append() should return a deep finger tree with one prefix and one suffix element when the finger tree has only one element" {
      val fingerTree = Single(1)

      fingerTree.append(2) shouldBe Deep(One(1), Empty(), One(2))
    }

    "append() should add the item to the last place of the tree suffix when the finger tree has three suffix elements and an empty deeper tree" {
      val fingerTree = Deep(One(1), Empty(), Three(2, 3, 4))

      fingerTree.append(5) shouldBe Deep(One(1), Empty(), Four(2, 3, 4, 5))
    }

    "append() should create a finger tree with the same prefix, the result of the append on the deeper tree and the new suffix when the prefix has four elements" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>(relaxed = true)
      val fingerTree = Deep(One(1), mockDeeperFingerTree, Four(2, 3, 4, 5))

      val dummyDeeperFingerTree: FingerTree<Node<Int>> = Single(Branch3(2, 3, 4))
      every { mockDeeperFingerTree.append(any()) } returns dummyDeeperFingerTree

      fingerTree.append(6) shouldBe Deep(One(1), dummyDeeperFingerTree, Two(5, 6))
    }

    /**
     * viewL()
     */

    "viewL() should return None when called on an empty finger tree" {
      val fingerTree = Empty<Int>()

      fingerTree.viewL() shouldBe None
    }

    "viewL() should return the single element and an empty finger tree when called on a single finger tree" {
      val fingerTree = Single(1)

      fingerTree.viewL() shouldBe Option.just(Tuple2(1, Empty<Int>()))
    }

    "viewL() should return the first element of the prefix and the remaining finger tree when the finger tree has more than one prefix elements" {
      val fingerTree = Deep(Two(1, 2), Empty(), Three(3, 4, 5))

      fingerTree.viewL() shouldBe Option.just(Tuple2(1, Deep(One(2), Empty(), Three(3, 4, 5))))
    }

    "viewL() should call viewL() on the deeper finger tree when the finger tree has one prefix element" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>()
      val fingerTree = Deep(One(1), mockDeeperFingerTree, One(2))

      every { mockDeeperFingerTree.viewL() } returns Option.empty()

      fingerTree.viewL()

      verify { mockDeeperFingerTree.viewL() }
    }

    "viewL() should return the first element of the prefix and the suffix as the remaining finger tree when the prefix has one element and viewL() on the deeper finger tree returns None" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>()
      val fingerTree = Deep(One(1), mockDeeperFingerTree, One(2))

      every { mockDeeperFingerTree.viewL() } returns Option.empty()

      fingerTree.viewL() shouldBe Option.just(Tuple2(1, Single(2)))
    }

    "viewL() should return the first element of the prefix and the result of viewL() on the deeper finger tree when the prefix has one element and viewL() on the deeper finger tree returns a non empty result" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>()
      val fingerTree = Deep(One(1), mockDeeperFingerTree, One(2))

      val remainingTree = Deep<Node<Int>>(One(Branch2(5, 6)), Empty(), One(Branch2(7, 8)))
      every { mockDeeperFingerTree.viewL() } returns Option.just(Tuple2(Branch2(3, 4), remainingTree))

      fingerTree.viewL() shouldBe Option.just(Tuple2(1, Deep(Two(3, 4), remainingTree, One(2))))
    }

    /**
     * viewR()
     */

    "viewR() should return None when called on an empty finger tree" {
      val fingerTree = Empty<Int>()

      fingerTree.viewR() shouldBe None
    }

    "viewR() should return the single element and an empty finger tree when called on a single finger tree" {
      val fingerTree = Single(1)

      fingerTree.viewR() shouldBe Option.just(Tuple2(1, Empty<Int>()))
    }

    "viewR() should return the last element of the suffix and the remaining finger tree when the finger tree has more than one suffix elements" {
      val fingerTree = Deep(Three(1, 2, 3), Empty(), Two(4, 5))

      fingerTree.viewR() shouldBe Option.just(Tuple2(5, Deep(Three(1, 2, 3), Empty(), One(4))))
    }

    "viewR() should call viewR() on the deeper finger tree when the finger tree has one suffix element" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>()
      val fingerTree = Deep(One(1), mockDeeperFingerTree, One(2))

      every { mockDeeperFingerTree.viewR() } returns Option.empty()

      fingerTree.viewR()

      verify { mockDeeperFingerTree.viewR() }
    }

    "viewR() should return the last element of the suffix and the prefix as the remaining finger tree when the suffix has one element and viewR() on the deeper finger tree returns None" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>()
      val fingerTree = Deep(One(1), mockDeeperFingerTree, One(2))

      every { mockDeeperFingerTree.viewR() } returns Option.empty()

      fingerTree.viewR() shouldBe Option.just(Tuple2(2, Single(1)))
    }

    "viewR() should return the last element of the suffix and the result of viewR() on the deeper finger tree when the suffix has one element and viewR() on the deeper finger tree returns a non empty result" {
      val mockDeeperFingerTree = mockk<FingerTree<Node<Int>>>()
      val fingerTree = Deep(One(1), mockDeeperFingerTree, One(2))

      val remainingTree = Deep<Node<Int>>(One(Branch2(5, 6)), Empty(), One(Branch2(7, 8)))
      every { mockDeeperFingerTree.viewR() } returns Option.just(Tuple2(Branch2(3, 4), remainingTree))

      fingerTree.viewR() shouldBe Option.just(Tuple2(2, Deep(One(1), remainingTree, Two(3, 4))))
    }

    /**
     * asList()
     */

    "asList() should return an empty list for an empty finger tree" {
      Empty<Int>().asList() shouldBe emptyList()
    }

    "asList() should return a singleton list for a single finger tree" {
      Single(1).asList() shouldBe listOf(1)
    }

    "asList() should return a correct list for a larger finger tree" {
      val fingerTree = Deep(One(0), Deep<Node<Int>>(One(Branch3(1, 2, 3)), Empty(), One(Branch3(4, 5, 6))), Four(7, 8, 9, 10))

      fingerTree.asList() shouldBe listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    /**
     * asSequence()
     */

    "asSequence() should return an empty sequence for an empty finger tree" {
      Empty<Int>().asSequence().toList() shouldBe emptyList()
    }

    "asSequence() should return a singleton sequence for a single finger tree" {
      Single(1).asSequence().toList() shouldBe listOf(1)
    }

    "asSequence() should return a correct sequence for a larger finger tree" {
      val fingerTree = Deep(One(0), Deep<Node<Int>>(One(Branch3(1, 2, 3)), Empty(), One(Branch3(4, 5, 6))), Four(7, 8, 9, 10))

      fingerTree.asSequence().toList() shouldBe listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    /**
     * rotateClockwise()
     */

    "rotateClockwise(1) should return an empty finger tree when the finger tree is empty" {
      Empty<Int>().rotateClockwise(1) shouldBe Empty()
    }

    "rotateClockwise(1) should return a single finger tree when the finger tree has one element" {
      Single(1).rotateClockwise(1) shouldBe Single(1)
    }

    "rotateClockwise(1) should rotate the list one time to the left when the finger tree more than one element" {
      val fingerTree = Deep(One(0), Deep<Node<Int>>(One(Branch3(1, 2, 3)), Empty(), One(Branch3(4, 5, 6))), Four(7, 8, 9, 10))

      fingerTree.rotateClockwise(1).asList() shouldBe listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 0)
    }

    "rotateClockwise(11) should create the same finger tree when the finger tree has 11 elements" {
      val fingerTree = Deep(One(0), Deep<Node<Int>>(One(Branch3(1, 2, 3)), Empty(), One(Branch3(4, 5, 6))), Four(7, 8, 9, 10))

      fingerTree.rotateClockwise(11).asList() shouldBe listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    /**
     * rotateCounterClockwise()
     */

    "rotateCounterClockwise(1) should return an empty finger tree when the finger tree is empty" {
      Empty<Int>().rotateCounterClockwise(1) shouldBe Empty()
    }

    "rotateCounterClockwise(1) should return a single finger tree when the finger tree has one element" {
      Single(1).rotateCounterClockwise(1) shouldBe Single(1)
    }

    "rotateCounterClockwise(1) should rotate the list one time to the left when the finger tree more than one element" {
      val fingerTree = Deep(One(0), Deep<Node<Int>>(One(Branch3(1, 2, 3)), Empty(), One(Branch3(4, 5, 6))), Four(7, 8, 9, 10))

      fingerTree.rotateCounterClockwise(1).asList() shouldBe listOf(10, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    }

    "rotateCounterClockwise(11) should create the same finger tree when the finger tree has 11 elements" {
      val fingerTree = Deep(One(0), Deep<Node<Int>>(One(Branch3(1, 2, 3)), Empty(), One(Branch3(4, 5, 6))), Four(7, 8, 9, 10))

      fingerTree.rotateCounterClockwise(11).asList() shouldBe listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    }

    /**
     * isEmpty()
     */

    "isEmpty() should return true when the finger tree has no elements" {
      Empty<Int>().isEmpty() shouldBe true
    }

    "isEmpty() shoudl return false when the finger tree has one element" {
      Single(1).isEmpty() shouldBe false
    }

    "isEmpty() should return false when the finger tree has more than one element" {
      Deep(One(1), Empty(), One(2)).isEmpty() shouldBe false
    }

  }
}
