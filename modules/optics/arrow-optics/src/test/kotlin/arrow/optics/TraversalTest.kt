package arrow.optics

import arrow.Kind
import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.toOption
import arrow.core.toT
import arrow.core.ForListK
import arrow.core.ListK
import arrow.mtl.State
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.fix
import arrow.core.k
import arrow.mtl.map
import arrow.mtl.run
import arrow.optics.mtl.assign
import arrow.optics.mtl.assignOld
import arrow.optics.mtl.assign_
import arrow.optics.mtl.extract
import arrow.optics.mtl.extractMap
import arrow.optics.mtl.toState
import arrow.optics.mtl.update
import arrow.optics.mtl.updateOld
import arrow.optics.mtl.update_
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.generators.listK
import arrow.test.generators.tuple2
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class TraversalTest : UnitSpec() {

  init {

    val listKTraverse = Traversal.fromTraversable<ForListK, Int, Int>(ListK.traverse())

    testLaws(
      TraversalLaws.laws(
        traversal = listKTraverse,
        aGen = Gen.listK(Gen.int()).map<Kind<ForListK, Int>> { it },
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = listKTraverse.asSetter(),
        aGen = Gen.listK(Gen.int()).map<Kind<ForListK, Int>> { it },
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = ListK.eq(Eq.any())
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
      aGen = Gen.tuple2(Gen.float(), Gen.float()),
      bGen = Gen.float(),
      funcGen = Gen.functionAToB(Gen.float()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    with(listKTraverse.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(Gen.listK(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(Gen.listK(Gen.int())) { ints ->
          nonEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(Gen.listK(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(Gen.listK(Gen.int())) { ints ->
          getAll(ints) == ints.k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(Gen.listK(Gen.int())) { ints ->
          combineAll(Int.monoid(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(Gen.listK(Gen.int())) { ints ->
          fold(Int.monoid(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(Gen.listK(Gen.int())) { ints ->
          headOption(ints) == ints.firstOrNull().toOption()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(Gen.listK(Gen.int())) { ints ->
          lastOption(ints) == ints.lastOrNull().toOption()
        }
      }
    }

    with(listKTraverse) {

      "Getting all targets of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints.k()) == ints.k()
        }
      }

      "Folding all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Finding an number larger than 10" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Get the length from a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints.k()) == ints.size
        }
      }

      "Extract should extract the focus from the state" {
        forAll(Gen.listK(Gen.int())) { ints ->
          extract().run(ints) ==
            State { iis: ListK<Int> ->
              iis toT getAll(iis)
            }.run(ints)
        }
      }

      "toState should be an alias to extract" {
        forAll(Gen.listK(Gen.int())) { ints ->
          toState().run(ints) == extract().run(ints)
        }
      }

      "Extracts with f should be same as extract and map" {
        forAll(Gen.listK(Gen.int()), Gen.functionAToB<Int, String>(Gen.string())) { ints, f ->
          extractMap(f).run(ints) == extract().map { it.map(f) }.run(ints)
        }
      }

      "update f should be same modify f within State and returning new state" {
        forAll(Gen.listK(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { ints, f ->
          update(f).run(ints) ==
            State<ListK<Int>, ListK<Int>> { iis: ListK<Int> ->
              modify(iis, f)
                .let { it.fix() toT getAll(it) }
            }.run(ints)
        }
      }

      "updateOld f should be same as modify f within State and returning old state" {
        forAll(Gen.listK(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { ints, f ->
          updateOld(f).run(ints) ==
            State { iis: ListK<Int> ->
              modify(iis, f).fix() toT getAll(iis)
            }.run(ints)
        }
      }

      "update_ f should be as modify f within State and returning Unit" {
        forAll(Gen.listK(Gen.int()), Gen.functionAToB<Int, Int>(Gen.int())) { ints, f ->
          update_(f).run(ints) ==
            State { iis: ListK<Int> ->
              modify(iis, f).fix() toT Unit
            }.run(ints)
        }
      }

      "assign a should be same set a within State and returning new value" {
        forAll(Gen.listK(Gen.int()), Gen.int()) { ints, i ->
          assign(i).run(ints) ==
            State { iis: ListK<Int> ->
              set(iis, i)
                .let { it.fix() toT getAll(it) }
            }.run(ints)
        }
      }

      "assignOld f should be same as modify f within State and returning old state" {
        forAll(Gen.listK(Gen.int()), Gen.int()) { ints, i ->
          assignOld(i).run(ints) ==
            State { iis: ListK<Int> ->
              set(iis, i).fix() toT getAll(iis)
            }.run(ints)
        }
      }

      "assign_ f should be as modify f within State and returning Unit" {
        forAll(Gen.listK(Gen.int()), Gen.int()) { ints, i ->
          assign_(i).run(ints) ==
            State { iis: ListK<Int> ->
              set(iis, i).fix() toT Unit
            }.run(ints)
        }
      }
    }
  }
}
