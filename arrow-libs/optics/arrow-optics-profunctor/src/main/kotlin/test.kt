import arrow.core.Either
import arrow.core.Eval
import arrow.core.iterateRight
import arrow.optics.Optic
import arrow.optics.PIxTraversal
import arrow.optics.PTraversal
import arrow.optics.collectOf
import arrow.optics.combinators.backwards
import arrow.optics.combinators.default
import arrow.optics.combinators.drop
import arrow.optics.combinators.filter
import arrow.optics.combinators.get
import arrow.optics.combinators.id
import arrow.optics.combinators.re
import arrow.optics.combinators.singular
import arrow.optics.combinators.take
import arrow.optics.compose
import arrow.optics.firstOrNull
import arrow.optics.get
import arrow.optics.internal.Applicative
import arrow.optics.internal.IxWanderF
import arrow.optics.internal.Kind
import arrow.optics.internal.WanderF
import arrow.optics.ixCollectOf
import arrow.optics.ixCompose
import arrow.optics.ixGet
import arrow.optics.ixTraverseLazyOf
import arrow.optics.ixTraverseLazyOf_
import arrow.optics.ixTraversing
import arrow.optics.ixView
import arrow.optics.modify
import arrow.optics.predef.eitherLeft
import arrow.optics.predef.foldedIterable
import arrow.optics.predef.foldedList
import arrow.optics.predef.notNull
import arrow.optics.predef.pairFirst
import arrow.optics.predef.traversedList
import arrow.optics.predef.traversedMap
import arrow.optics.review
import arrow.optics.set
import arrow.optics.traverseLazyOf
import arrow.optics.traversing
import arrow.optics.typeclasses.Plated
import arrow.optics.typeclasses.cosmos
import arrow.optics.typeclasses.deep
import arrow.optics.view

fun main() {

  val xs = listOf(2, null, 3)

  val o = Optic.traversedList<Int?, Int?>().notNull().also {

  }
  xs.modify(o) { it * 3 }
    .also(::println) // [6, null, 9]

  Optic.pairFirst<Int?, Int?, String>().notNull().filter { it % 2 == 0 }.let {
    (2 to "").modify(it) { it * 3 }
      .also(::println) // (6, "")
  }

  val g = Optic.ixGet { i: Int -> 1 to i }.get { it * 2 }.let {
    it.ixCompose(Optic.ixGet { it -> "Hello" to it })
  }

  100.ixView(g)
    .also(::println)

  val f = Optic.traversedList<String, String>()
    .backwards()
    .also {

    }

  listOf("Hello", "World", "!")
    .ixCollectOf(f)
    .also(::println)

  val h = Optic.traversedMap<String, Int, Int>()
    .compose(Optic.id())
    .backwards()

  mapOf("Hello" to 3, "World" to 5)
    .ixCollectOf(h.singular())
    .also(::println)

  "20".review(Optic.get<String, Either<String, Int>> { Either.Left(it) }.re())
    .also(::println)

  val x = Optic.eitherLeft<Int, String, Double>().re().re()

  "Hello".review(x)
    .also(::println)

  val y = Optic.pairFirst<Int?, Int?, Double>().re().re() // re().re() = id()
    .compose(Optic.default(100))

  (null to 1.0).view(y)
    .also(::println) // 100
  (null to 1.0).set(y, 100)
    .also(::println) // (null, 1.0)

  val test = Optic.foldedIterable<List<Int>, Int>()
    .take(5)
    .drop(1)

  listOf(100, 200, 300)
    .firstOrNull(test)
    .also(::println)

  val plate = Plated.list<Int>()

  (0..10000).toList()
    .collectOf(plate.deep(Optic.filter { it.size == 2 }))
    .also(::println) // [[9999, 10000]]

  (0..3).toList()
    .collectOf(plate.cosmos())
    .also(::println) // [[0,1,2,3], [1,2,3], [2,3], [3], []]

  val t = Tree.Branches(
    sequenceOf(
      Tree.Leaf(1),
      Tree.Leaf(200),
      Tree.Branches(
        sequenceOf(
          Tree.Leaf(3),
          Tree.Leaf(25),
          Tree.Leaf(20)
        )
      )
    )
  )

  t.modify(Tree.traversed()) { "$it" }
    .also(::println)

  val treeLens = Tree.traversed<Int, Int>()
    .filter { it % 2 == 0 }

  t.collectOf(treeLens)
    .also(::println)

  t.modify(treeLens) { it * 2 }
    .also(::println)
  
  t.collectOf(Plated.tree<Int>().plate())
    .also(::println)

  t.collectOf(Plated.tree<Int>().deep(Optic.filter { it is Tree.Leaf }))
    .also(::println)
}

sealed class Tree<A> {
  class Branches<A>(val xs: Sequence<Tree<A>>) : Tree<A>() {
    override fun toString(): String = "Branches " + xs.toList().toString()
  }
  class Leaf<A>(val a: A) : Tree<A>() {
    override fun toString(): String = a.toString()
  }

  companion object {
    fun <A, B> traversed(): PTraversal<Tree<A>, Tree<B>, A, B> =
      Optic.traversing(object : WanderF<Tree<A>, Tree<B>, A, B> {
        override fun <F> invoke(
          AF: Applicative<F>,
          source: Tree<A>,
          f: (A) -> Kind<F, B>
        ): Kind<F, Tree<B>> {
          return when (source) {
            is Branches -> source.xs.map { t -> t.traverseLazyOf(traversed(), AF, f) }
              .iterator().iterateRight(Eval.now(AF.pure(emptyList<Tree<B>>()))) { fb, acc ->
                AF.apLazy(AF.map(fb) { t -> { xs: List<Tree<B>> -> listOf(t) + xs } }, acc)
              }.value().let { AF.map(it) { Branches(it.asSequence()) } }
            is Leaf -> AF.map(f(source.a)) { b -> Leaf(b) }
          }
        }
      })
  }
}

fun <A> Plated.Companion.tree(): Plated<Tree<A>> = Plated { 
  Optic.traversing(object : WanderF<Tree<A>, Tree<A>, Tree<A>, Tree<A>> {
    override fun <F> invoke(AF: Applicative<F>, source: Tree<A>, f: (Tree<A>) -> Kind<F, Tree<A>>): Kind<F, Tree<A>> =
      when (source) {
        is Tree.Branches -> source.xs.map(f).iterator().iterateRight(Eval.now(AF.pure(emptyList<Tree<A>>()))) { ft, acc ->
          AF.apLazy(AF.map(ft) { t -> { xs: List<Tree<A>> -> listOf(t) + xs } }, acc)
        }.value().let { AF.map(it) { Tree.Branches(it.asSequence()) } }
        is Tree.Leaf -> AF.pure(source)
      }
  })
}

