package arrow.core.test.laws

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Eval.Companion.always
import arrow.core.ForListK
import arrow.core.ForOption
import arrow.core.some
import arrow.core.right
import arrow.core.fix
import arrow.core.Left
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.identity
import arrow.core.extensions.ListKEq
import arrow.core.extensions.either.monad.flatMap
import arrow.core.extensions.eq
import arrow.core.extensions.list.eqK.eqK
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.monoid
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.monad.monad
import arrow.core.test.concurrency.SideEffect
import arrow.core.test.generators.GenK
import arrow.core.test.generators.functionAAToA
import arrow.core.test.generators.functionABToB
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.functionBAToB
import arrow.core.test.generators.eval
import arrow.core.test.generators.genK
import arrow.core.test.generators.intPredicate
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.listK
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object FoldableLaws {

  fun <F> laws(FF: Foldable<F>, GENK: GenK<F>): List<Law> {
    val GEN: Gen<Kind<F, Int>> = GENK.genK(Gen.intSmall())
    val GENListK: Gen<Kind<F, ListK<Int>>> = GENK.genK(Gen.listK(Gen.intSmall()))
    val GENB = GENK.genK(Gen.bool())

    val EQ: Eq<Int> = Int.eq()
    val EQBool = Boolean.eq()
    val EQOptionInt = Option.eq(Int.eq())
    val EQListKInt: ListKEq<Int> = ListK.eq(Int.eq())
    val EQForOptionInt: Eq<Kind<ForOption, Int>> = Option.eqK().liftEq(Int.eq())

    return listOf(
      Law("Foldable Laws: foldRight is lazy") { FF.`foldRight is lazy`(GEN) },
      Law("Foldable Laws: Left fold consistent with foldMap") { FF.leftFoldConsistentWithFoldMap(GEN, EQ) },
      Law("Foldable Laws: Right fold consistent with foldMap") { FF.rightFoldConsistentWithFoldMap(GEN, EQ) },
      Law("Foldable Laws: find matching predicate should return some(value) or none") { FF.`find matching predicate should return some(value) or none`(GEN, EQOptionInt) },
      Law("Foldable Laws: Exists is consistent with find") { FF.existsConsistentWithFind(GEN) },
      Law("Foldable Laws: Exists is lazy") { FF.existsIsLazy(GEN, EQ) },
      Law("Foldable Laws: ForAll is lazy") { FF.forAllIsLazy(GEN, EQ) },
      Law("Foldable Laws: ForAll consistent with exists") { FF.forallConsistentWithExists(GEN) },
      Law("Foldable Laws: ForAll returns true if isEmpty") { FF.forallReturnsTrueIfEmpty(GEN) },
      Law("Foldable Laws: FoldM for Id is equivalent to fold left") { FF.foldMIdIsFoldL(GEN, EQ) },
      Law("Foldable Laws: firstOrNone returns None if isEmpty") { FF.firstOrNoneReturnsNoneIfEmpty(GEN) },
      Law("Foldable Laws: firstOrNone returns None if predicate fails") { FF.firstOrNoneReturnsNoneIfPredicateFails(GEN) },
      Law("Foldable Laws: firstOrNone is consistent with find") { FF.`firstOrNone is consistent with find`(GEN, EQOptionInt) },
      Law("Foldable Laws: firstOrNone is consistent with find matching predicate") { FF.`firstOrNone is consistent with find predicate`(GEN, EQOptionInt) },
      Law("Foldable Laws: toList turn items into a list") { FF.`toList turn items into a list`(GEN, EQ) },
      Law("Foldable Laws: fold returns combination of all items") { FF.`fold should combine all items`(GENListK, EQListKInt) },
      Law("Foldable Laws: combineAll is an alias for fold") { FF.`combineAll consistent with fold (alias)`(GENListK, EQListKInt) },
      Law("Foldable Laws: reduceLeftToOption combines all items into an optional value") { FF.`reduceLeftToOption returns Option value`(GENB, EQOptionInt) },
      Law("Foldable Laws: reduceRightToOption combines all items into an optional value") { FF.`reduceRightToOption returns Option value`(GENB, EQOptionInt) },
      Law("Foldable Laws: reduceLeftOption consistent with reduceLeftToOption") { FF.`reduceLeftOption returns Option value`(GEN, EQOptionInt) },
      Law("Foldable Laws: reduceRightOption consistent with reduceRightToOption") { FF.`reduceRightOption returns Option value`(GEN, EQOptionInt) },
      Law("Foldable Laws: traverse_ consistent with foldRight") { FF.`traverse_ consistent with foldRight`(Option.applicative(), GEN, Option.genK(), Option.eqK().liftEq(Eq.any())) },
      Law("Foldable Laws: sequence_ consistent with traverse_") { FF.`sequence_ consistent with traverse_`(Option.applicative(), GENK, Option.genK(), Option.eqK().liftEq(Eq.any())) },
      Law("Foldable Laws: isEmpty returns if there are elements or not") { FF.`isEmpty returns if there are elements or not`(GEN, EQBool) },
      Law("Foldable Laws: isNotEmpty consistent with isEmpty") { FF.`isNotEmpty consistent with isEmpty`(GEN, EQBool) },
      Law("Foldable Laws: foldMapM folds on F mapping values to G(B) using given Monoid") { FF.`foldMapM folds on F mapping values to G(B) using given Monoid`(GEN, EQForOptionInt) },
      Law("Foldable Laws: get gets the item at the given index of the Foldable") { FF.`get gets the item at the given index of the Foldable`(GEN, EQOptionInt) },
      Law("Foldable Laws: foldMapA folds on F mapping values to G(B) using given Monoid") { FF.`foldMapA folds on F mapping values to G(B) using given Monoid`(GEN, EQForOptionInt) }
    )
  }

  fun <F> laws(FF: Foldable<F>, GA: Applicative<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val EQKListKInt: Eq<Kind<F, Kind<ForListK, Int>>> = EQK.liftEq(ListK.eq(Int.eq()))

    return laws(FF, GENK) + listOf(
      Law("Foldable Laws: orEmpty consistent with just empty") { FF.`orEmpty consistent with just empty`(GA, EQKListKInt) }
    )
  }

  fun <F> Foldable<F>.`foldRight is lazy`(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      val lazyResult = fa.foldRight(Eval.now(0)) { _: Int, _: Eval<Int> ->
        sideEffect.increment()
        Eval.later { 1 }
      }
      sideEffect.counter shouldBe 0
      lazyResult.value()

      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter == expected
    }

  fun <F> Foldable<F>.leftFoldConsistentWithFoldMap(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }, EQ)
      }
    }

  fun <F> Foldable<F>.rightFoldConsistentWithFoldMap(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldRight(Eval.later { empty() }) { a, lb: Eval<Int> -> lb.map { f(a).combine(it) } }.value(), EQ)
      }
    }

  fun <F> Foldable<F>.`find matching predicate should return some(value) or none`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      val expected = fa.foldRight(Eval.now<Option<Int>>(None)) { a, lb -> if (f(a)) Eval.now(a.some()) else lb }.value()
      fa.find { f(it) }.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.existsConsistentWithFind(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.exists(f).equalUnderTheLaw(fa.find(f).fold({ false }, { true }), Eq.any())
    }

  fun <F> Foldable<F>.existsIsLazy(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.exists { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.forAllIsLazy(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.all { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else fa.size(Long.monoid())
      sideEffect.counter.equalUnderTheLaw(expected.toInt(), EQ)
    }

  fun <F> Foldable<F>.forallConsistentWithExists(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      if (fa.all(f)) {
        // if f is true for all elements, then there cannot be an element for which
        // it does not hold.
        val negationExists = fa.exists { a -> !(f(a)) }
        // if f is true for all elements, then either there must be no elements
        // or there must exist an element for which it is true.
        !negationExists && (fa.isEmpty() || fa.exists(f))
      } else true
    }

  fun <F> Foldable<F>.forallReturnsTrueIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      !fa.isEmpty() || fa.all(f)
    }

  fun <F> Foldable<F>.foldMIdIsFoldL(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        val foldL: Option<Int> = Some(fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) })
        val foldM: Option<Int> = fa.foldM(Option.monad(), empty()) { acc, a -> Some(acc.combine(f(a))) }.fix()
        foldM == foldL
      }
    }

  fun <F> Foldable<F>.firstOrNoneReturnsNoneIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      if (fa.isEmpty()) fa.firstOrNone().isEmpty()
      else fa.firstOrNone().isDefined()
    }

  fun <F> Foldable<F>.firstOrNoneReturnsNoneIfPredicateFails(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOrNone { false }.isEmpty()
    }

  fun <F> Foldable<F>.`firstOrNone is consistent with find`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOrNone().equalUnderTheLaw(fa.find { true }, EQ)
    }

  fun <F> Foldable<F>.`firstOrNone is consistent with find predicate`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.firstOrNone(f).equalUnderTheLaw(fa.find { f(it) }, EQ)
    }

  fun <F> Foldable<F>.`toList turn items into a list`(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val result = fa.toList()
      val expected = fa.foldRight(Eval.now(emptyList<Int>())) { v, acc -> acc.map { listOf(v) + it } }.value()
      result.eqK(expected, EQ)
    }

  fun <F> Foldable<F>.`fold should combine all items`(G: Gen<Kind<F, ListK<Int>>>, EQ: Eq<ListK<Int>>) =
    forAll(G) { fa: Kind<F, ListK<Int>> ->
      with(ListK.monoid<Int>()) {
        fa.fold(this).equalUnderTheLaw(fa.foldLeft(empty()) { acc, a -> acc.combine(a) }, EQ)
      }
    }

  fun <F> Foldable<F>.`combineAll consistent with fold (alias)`(G: Gen<Kind<F, ListK<Int>>>, EQ: Eq<ListK<Int>>) =
    forAll(G) { fa: Kind<F, ListK<Int>> ->
      with(ListK.monoid<Int>()) {
        fa.combineAll(this).equalUnderTheLaw(fa.fold(this), EQ)
      }
    }

  fun <F> Foldable<F>.`reduceLeftToOption returns Option value`(G: Gen<Kind<F, Boolean>>, EQ: Eq<Option<Int>>) =
    forAll(
      Gen.functionAToB<Boolean, Int>(Gen.intSmall()),
      Gen.functionBAToB<Boolean, Int>(Gen.intSmall()),
      G
    ) { f: (Boolean) -> Int, g: (Int, Boolean) -> Int, fa: Kind<F, Boolean> ->

      val expected = fa.foldLeft(Option.empty<Int>()) { option, a ->
        when (option) {
          is Some<Int> -> Some(g(option.t, a))
          is None -> Some(f(a))
        }
      }
      fa.reduceLeftToOption(f, g).equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.`reduceRightToOption returns Option value`(G: Gen<Kind<F, Boolean>>, EQ: Eq<Option<Int>>) =
    forAll(
      Gen.functionAToB<Boolean, Int>(Gen.intSmall()),
      Gen.functionABToB<Boolean, Eval<Int>>(Gen.intSmall().eval()),
      G
    ) { f: (Boolean) -> Int, g: (Boolean, Eval<Int>) -> Eval<Int>, fa: Kind<F, Boolean> ->

      val expected = fa.foldRight(Eval.Now<Option<Int>>(Option.empty())) { a: Boolean, lb: Eval<Option<Int>> ->
        lb.flatMap { option ->
          when (option) {
            is Some<Int> -> g(a, Eval.Now(option.t)).map { Some(it) }
            is None -> Eval.Later { Some(f(a)) }
          }
        }
      }
      fa.reduceRightToOption(f, g).value().equalUnderTheLaw(expected.value(), EQ)
    }

  fun <F> Foldable<F>.`reduceLeftOption returns Option value`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.functionAAToA(Gen.intSmall()), G) { f: (Int, Int) -> Int, fa: Kind<F, Int> ->
      fa.reduceLeftOption(f).equalUnderTheLaw(fa.reduceLeftToOption({ a -> a }, f), EQ)
    }

  fun <F> Foldable<F>.`reduceRightOption returns Option value`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.functionABToB<Int, Eval<Int>>(Gen.intSmall().eval()), G) { f: (Int, Eval<Int>) -> Eval<Int>, fa ->
      fa.reduceRightOption(f).value().equalUnderTheLaw(fa.reduceRightToOption({ a -> a }, f).value(), EQ)
    }

  fun <F> Foldable<F>.`orEmpty consistent with just empty`(GA: Applicative<F>, EQ: Eq<Kind<F, Kind<ForListK, Int>>>) =
    GA.run {
      with(ListK.monoid<Int>()) {
        orEmpty(this@run, this).equalUnderTheLaw(just(this.empty()), EQ)
      }
    }

  fun <F, G> Foldable<F>.`traverse_ consistent with foldRight`(GA: Applicative<G>, GF: Gen<Kind<F, Int>>, GG: GenK<G>, EQG: Eq<Kind<G, Unit>>) =
    forAll(Gen.functionAToB<Int, Kind<G, Int>>(GG.genK(Gen.intSmall())), GF) { f: (Int) -> Kind<G, Int>, fa: Kind<F, Int> ->
      GA.run {
        val expected = fa.foldRight(always { GA.just(Unit) }) { a, acc -> GA.run { f(a).apEval(acc.map { it.map { { _: Int -> Unit } } }) } }.value()
        fa.traverse_(this, f).equalUnderTheLaw(expected, EQG)
      }
    }

  fun <F, G> Foldable<F>.`sequence_ consistent with traverse_`(GA: Applicative<G>, GF: GenK<F>, GG: GenK<G>, EQ: Eq<Kind<G, Unit>>) =
    forAll(GF.genK(GG.genK(Gen.intSmall()))) { fa: Kind<F, Kind<G, Int>> ->
      GA.run {
        fa.sequence_(this).equalUnderTheLaw(fa.traverse_(this, ::identity), EQ)
      }
    }

  fun <F> Foldable<F>.`isEmpty returns if there are elements or not`(G: Gen<Kind<F, Int>>, EQ: Eq<Boolean>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.isEmpty().equalUnderTheLaw(fa.foldRight(Eval.True) { _, _ -> Eval.False }.value(), EQ)
    }

  fun <F> Foldable<F>.`isNotEmpty consistent with isEmpty`(G: Gen<Kind<F, Int>>, EQ: Eq<Boolean>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.isNotEmpty().equalUnderTheLaw(!fa.isEmpty(), EQ)
    }

  fun <F> Foldable<F>.`foldMapM folds on F mapping values to G(B) using given Monoid`(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<ForOption, Int>>) =
    forAll(Gen.functionAToB<Int, Kind<ForOption, Int>>(Gen.intSmall().map(::Some)), G) { f: (Int) -> Kind<ForOption, Int>, fa: Kind<F, Int> ->
      Option.monad().run {
        with(Int.monoid()) {
          val expected = fa.foldM(this@run, this.empty()) { b, a -> f(a).map { this.run { b.combine(it) } } }
          fa.foldMapM(this@run, this, f).equalUnderTheLaw(expected, EQ)
        }
      }
    }

  fun <F> Foldable<F>.`get gets the item at the given index of the Foldable`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.intSmall(), G) { index, fa: Kind<F, Int> ->
      val idx = index.toLong()
      val expected = if (idx < 0L) {
        None
      } else {
        fa.foldLeft<Int, Either<Int, Long>>(0L.right()) { i, a ->
          i.flatMap {
            if (it == idx) Left(a)
            else Right(it + 1L)
          }
        }.swap().toOption()
      }
      fa.get(idx).equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.`foldMapA folds on F mapping values to G(B) using given Monoid`(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<ForOption, Int>>) =
    forAll(Gen.functionAToB<Int, Kind<ForOption, Int>>(Gen.intSmall().map(::Some)), G) { f: (Int) -> Kind<ForOption, Int>, fa: Kind<F, Int> ->
      Option.monad().run {
        with(Int.monoid()) {
          val expected = fa.foldM(this@run, this.empty()) { b, a -> f(a).map { this.run { b.combine(it) } } }
          fa.foldMapA(this@run, this, f).equalUnderTheLaw(expected, EQ)
        }
      }
    }
}
