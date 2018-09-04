package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*
import arrow.instances.traverse as idTraverse

@instance(Id::class)
interface IdEqInstance<A> : Eq<Id<A>> {

  fun EQ(): Eq<A>

  override fun Id<A>.eqv(b: Id<A>): Boolean =
    EQ().run { value.eqv(b.value) }
}

@instance(Id::class)
interface IdShowInstance<A> : Show<Id<A>> {
  override fun Id<A>.show(): String =
    toString()
}

@instance(Id::class)
interface IdFunctorInstance : Functor<ForId> {
  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)
}

@instance(Id::class)
interface IdApplicativeInstance : Applicative<ForId> {
  override fun <A, B> Kind<ForId, A>.apPipe(ff: Kind<ForId, (A) -> B>): Id<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)
}

@instance(Id::class)
interface IdMonadInstance : Monad<ForId> {
  override fun <A, B> Kind<ForId, A>.apPipe(ff: Kind<ForId, (A) -> B>): Id<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForId, A>.flatMap(f: (A) -> Kind<ForId, B>): Id<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IdOf<Either<A, B>>>): Id<B> =
    Id.tailRecM(a, f)

  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)
}

@instance(Id::class)
interface IdComonadInstance : Comonad<ForId> {
  override fun <A, B> Kind<ForId, A>.coflatMap(f: (Kind<ForId, A>) -> B): Id<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForId, A>.extract(): A =
    fix().extract()

  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)
}

@instance(Id::class)
interface IdBimonadInstance : Bimonad<ForId> {
  override fun <A, B> Kind<ForId, A>.apPipe(ff: Kind<ForId, (A) -> B>): Id<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForId, A>.flatMap(f: (A) -> Kind<ForId, B>): Id<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IdOf<Either<A, B>>>): Id<B> =
    Id.tailRecM(a, f)

  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <A> just(a: A): Id<A> =
    Id.just(a)

  override fun <A, B> Kind<ForId, A>.coflatMap(f: (Kind<ForId, A>) -> B): Id<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForId, A>.extract(): A =
    fix().extract()
}

@instance(Id::class)
interface IdFoldableInstance : Foldable<ForId> {
  override fun <A, B> Kind<ForId, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForId, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <A, G, B> IdOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Id<B>> = GA.run {
  f(value()).map { Id(it) }
}

fun <A, G> IdOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Id<A>> =
  idTraverse(GA, ::identity)

@instance(Id::class)
interface IdTraverseInstance : Traverse<ForId> {
  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForId, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Id<B>> =
    idTraverse(AP, f)

  override fun <A, B> Kind<ForId, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> arrow.Kind<arrow.core.ForId, A>.foldRight(lb: arrow.core.Eval<B>, f: (A, arrow.core.Eval<B>) -> arrow.core.Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

object IdContext : IdBimonadInstance, IdTraverseInstance {
  override fun <A, B> Kind<ForId, A>.map(f: (A) -> B): Id<B> =
    fix().map(f)
}

infix fun <L> ForId.Companion.extensions(f: IdContext.() -> L): L =
  f(IdContext)
