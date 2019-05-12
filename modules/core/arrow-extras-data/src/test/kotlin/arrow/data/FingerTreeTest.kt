package arrow.data

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.firstOrNone
import arrow.core.getOrElse
import arrow.data.fingertree.FingerTree
import arrow.data.fingertree.FingerTree.Companion.single
import arrow.data.fingertree.FingerTree.Deep
import arrow.data.fingertree.FingerTree.Empty
import arrow.data.fingertree.FingerTree.Single
import arrow.data.fingertree.internal.Affix.Four
import arrow.data.fingertree.internal.Affix.One
import arrow.data.fingertree.internal.Affix.Three
import arrow.data.fingertree.internal.Affix.Two
import arrow.data.fingertree.internal.Node
import arrow.data.fingertree.internal.Node.Branch2
import arrow.data.fingertree.internal.Node.Branch3
import arrow.test.generators.functionAToB
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.Collections

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
      val fingerTree = Deep(Four(1, 2, 3, 4), Empty(), One(5))

      fingerTree.prepend(6) shouldBe Deep<Int>(Two(6, 1), Single(Branch3(2, 3, 4)), One(5))
    }

    "Property based testing for prepend()" {

      forAll(Gen.list(Gen.int())) { ints ->
        var fingerTree: FingerTree<Int> = Empty()
        ints.forEach {
          fingerTree = fingerTree.prepend(it)
        }
        fingerTree.asList() == ints.reversed()
      }
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
      val fingerTree = Deep(One(1), Empty(), Four(2, 3, 4, 5))

      fingerTree.append(6) shouldBe Deep<Int>(One(1), Single(Branch3(2, 3, 4)), Two(5, 6))
    }

    "Property based testing for append()" {

      forAll(Gen.list(Gen.int())) { ints ->
        var fingerTree: FingerTree<Int> = Empty()
        ints.forEach {
          fingerTree = fingerTree.append(it)
        }
        fingerTree.asList() == ints
      }
    }

    /**
     * appendAll()
     */

    "Property based testing for appendAll()" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { l1, l2 ->
        val tree = FingerTree.fromList(l1)

        tree.appendAll(l2).asList() == l1 + l2
      }
    }

    /**
     * prependAll()
     */

    "Property based testing for prependAll()" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { l1, l2 ->
        val tree = FingerTree.fromList(l1)

        tree.prependAll(l2).asList() == l2.asReversed() + l1
      }
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

    "viewL() should return the prefix element and the affix as the remaining finger tree when the finger tree has one prefix element and an empty deeper finger tree" {
      Deep(One(1), Empty(), One(2)).viewL() shouldBe Option.just(Tuple2(1, Single(2)))
    }

    "viewL() should return the prefix element and a deep remaining finger tree when the finger tree has one prefix element and an none empty deeper finger tree" {
      val fingerTree = Deep<Int>(One(1), Single(Branch2(1, 2)), One(2))

      fingerTree.viewL() shouldBe Option.just(Tuple2(1, Deep<Int>(Two(1, 2), Empty(), One(2))))
    }

    "Property based testing for viewL()" {
      forAll(Gen.list(Gen.int())) { ints ->
        val fingerTree = FingerTree.fromList(ints)
        val viewLeftResultList = mutableListOf<Int>()

        var viewLResult = fingerTree.viewL()
        while (viewLResult is Some) {

          viewLeftResultList.add(viewLResult.t.a)
          viewLResult = viewLResult.t.b.viewL()
        }

        viewLeftResultList == ints
      }
    }

    /**
     * head()
     */

    "Property based testing for head()" {
      forAll(Gen.list(Gen.int())) { l ->
        FingerTree.fromList(l).head() == l.firstOrNone()
      }
    }

    /**
     * tail()
     */

    "Property based testing for tail()" {
      forAll(Gen.list(Gen.int())) { l ->
        FingerTree.fromList(l).tail().getOrElse { Empty() }.asList() == l.drop(1)
      }
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

    "viewR() should return the suffix element and the prefix as the remaining finger tree when the finger tree has one affix element and an empty deeper finger tree" {
      Deep(One(1), Empty(), One(2)).viewR() shouldBe Option.just(Tuple2(2, Single(1)))
    }

    "viewR() should return the affix element and a deep remaining finger tree when the finger tree has one affix element and an none empty deeper finger tree" {
      val fingerTree = Deep<Int>(One(1), Single(Branch2(1, 2)), One(2))

      fingerTree.viewR() shouldBe Option.just(Tuple2(2, Deep(One(1), Empty(), Two(1, 2))))
    }

    "Property based testing for viewR()" {
      forAll(Gen.list(Gen.int())) { ints ->
        val fingerTree = FingerTree.fromList(ints)
        val viewLeftResultList = mutableListOf<Int>()

        var viewRResult = fingerTree.viewR()
        while (viewRResult is Some) {

          viewLeftResultList.add(viewRResult.t.a)
          viewRResult = viewRResult.t.b.viewR()
        }

        viewLeftResultList == ints.reversed()
      }
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

    "Property based testing for rotateClockwise()" {

      forAll(Gen.positiveIntegers(), Gen.list(Gen.int())) { rotationCount, list ->
        val tree = FingerTree.fromList(list)
        Collections.rotate(list, -rotationCount)

        tree.rotateClockwise(rotationCount).asList() == list
      }
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

    "Property based testing for rotateCounterClockwise()" {

      forAll(Gen.positiveIntegers(), Gen.list(Gen.int())) { rotationCount, list ->
        val tree = FingerTree.fromList(list)
        Collections.rotate(list, rotationCount)

        tree.rotateCounterClockwise(rotationCount).asList() == list
      }
    }

    /**
     * isEmpty()
     */

    "isEmpty() should return true when the finger tree has no elements" {
      Empty<Int>().isEmpty() shouldBe true
    }

    "isEmpty() should return false when the finger tree has one element" {
      Single(1).isEmpty() shouldBe false
    }

    "isEmpty() should return false when the finger tree has more than one element" {
      Deep(One(1), Empty(), One(2)).isEmpty() shouldBe false
    }

    "Property based testing for isEmpty()" {
      forAll(Gen.list(Gen.int())) { l ->
        FingerTree.fromList(l).isEmpty() == l.isEmpty()
      }
    }

    /**
     * size()
     */

    "size() should return 0 when the finger tree is a empty finger tree" {
      Empty<Int>().size() shouldBe 0
    }

    "size() should return 1 when the finger tree is a single finger tree" {
      Single(1).size() shouldBe 1
    }

    "Property based testing for size()" {
      forAll(Gen.list(Gen.int())) { l ->
        FingerTree.fromList(l).size() == l.size
      }
    }

    /**
     * concat()
     */

    "concat() should return an empty finger tree when both finger trees are empty" {
      Empty<Int>() concat Empty() shouldBe Empty()
    }

    "concat() should return the left finger three when the right one is empty" {
      val leftFingerTree = Deep(One(1), Deep<Node<Int>>(One(Branch2(3, 4)), Empty(), One(Branch2(5, 6))), One(2))

      leftFingerTree concat Empty() shouldBe leftFingerTree
    }

    "concat() should return the right finger three when the left one is empty" {
      val rightFingerTree = Deep(One(1), Deep<Node<Int>>(One(Branch2(3, 4)), Empty(), One(Branch2(5, 6))), One(2))

      Empty<Int>() concat rightFingerTree shouldBe rightFingerTree
    }

    "concat() should prepend the the single tree's value when the left item is a single finger tree" {
      Single(1) concat Deep(One(2), Empty(), One(3)) shouldBe Deep(Two(1, 2), Empty(), One(3))
    }

    "concat() should append the the single tree's value when the right item is a single finger tree" {
      Deep(One(1), Empty(), One(2)) concat Single(3) shouldBe Deep(One(1), Empty(), Two(2, 3))
    }

    "Property based testing for concat()" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { l1, l2 ->
        (FingerTree.fromList(l1) concat FingerTree.fromList(l2)).asList() == l1 + l2
      }
    }


    /**
     * map
     */

    "Property based testing for map()" {
      forAll(Gen.list(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { l, f ->
        (FingerTree.fromList(l).map(f)).asList() == l.map(f)
      }
    }

    /**
     * flatMap
     */

    "Property based testing for flatMap()" {
      forAll(Gen.list(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { l, f ->
        (FingerTree.fromList(l).flatMap { single(f(it)) }).asList() == l.flatMap { listOf(f(it)) }
      }
    }

    /**
     * ap()
     */

    "Property based testing for ap()" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.functionAToB<Int, Int>(Gen.int()))) { l, fs ->
        (FingerTree.fromList(l).ap(FingerTree.fromList(fs))).asList() == fs.flatMap { f -> l.map(f) }
      }
    }

    /**
     * tailRecM()
     */

    "Property based testing for tailRecM()" {
      // TODO: Write property based test
    }

  }
}
