package arrow.ap.objects.deriving

interface DerivingFunctorInstance : arrow.typeclasses.Functor<ForDeriving> {
  override fun <A, B> arrow.ap.objects.deriving.DerivingOf<A>.map(f: kotlin.Function1<A, B>): arrow.ap.objects.deriving.Deriving<B> =
    fix().map(f)
}

fun arrow.ap.objects.deriving.Deriving.Companion.functor(): DerivingFunctorInstance =
  object : DerivingFunctorInstance, arrow.typeclasses.Functor<ForDeriving> {}


interface DerivingApplicativeInstance : arrow.typeclasses.Applicative<ForDeriving> {
  override fun <A> just(a: A): arrow.ap.objects.deriving.Deriving<A> =
    arrow.ap.objects.deriving.Deriving.just(a)

  override fun <A, B> arrow.ap.objects.deriving.DerivingOf<A>.ap(ff: arrow.ap.objects.deriving.DerivingOf<kotlin.Function1<A, B>>): arrow.ap.objects.deriving.Deriving<B> =
    fix().ap(ff)
}

fun arrow.ap.objects.deriving.Deriving.Companion.applicative(): DerivingApplicativeInstance =
  object : DerivingApplicativeInstance, arrow.typeclasses.Applicative<ForDeriving> {}


interface DerivingMonadInstance : arrow.typeclasses.Monad<ForDeriving> {
  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.ap.objects.deriving.DerivingOf<arrow.core.Either<A, B>>>): arrow.ap.objects.deriving.Deriving<B> =
    arrow.ap.objects.deriving.Deriving.tailRecM(a, f)

  override fun <A, B> arrow.ap.objects.deriving.DerivingOf<A>.flatMap(f: kotlin.Function1<A, arrow.ap.objects.deriving.DerivingOf<B>>): arrow.ap.objects.deriving.Deriving<B> =
    fix().flatMap(f)

  override fun <A, B> arrow.ap.objects.deriving.DerivingOf<A>.map(f: kotlin.Function1<A, B>): arrow.ap.objects.deriving.Deriving<B> =
    fix().map(f)

  override fun <A> just(a: A): arrow.ap.objects.deriving.Deriving<A> =
    arrow.ap.objects.deriving.Deriving.just(a)
}

fun arrow.ap.objects.deriving.Deriving.Companion.monad(): DerivingMonadInstance =
  object : DerivingMonadInstance, arrow.typeclasses.Monad<ForDeriving> {}
