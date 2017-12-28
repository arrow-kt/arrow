package arrow

interface Function0FunctorInstance : arrow.Functor<Function0HK> {
    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)
}

object Function0FunctorInstanceImplicits {
    fun instance(): Function0FunctorInstance = arrow.Function0.Companion.functor()
}

fun arrow.Function0.Companion.functor(): Function0FunctorInstance =
        object : Function0FunctorInstance, arrow.Functor<Function0HK> {}

interface Function0ApplicativeInstance : arrow.Applicative<Function0HK> {
    override fun <A, B> ap(fa: arrow.Function0Kind<A>, ff: arrow.Function0Kind<kotlin.Function1<A, B>>): arrow.Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Function0<A> =
            arrow.Function0.pure(a)
}

object Function0ApplicativeInstanceImplicits {
    fun instance(): Function0ApplicativeInstance = arrow.Function0.Companion.applicative()
}

fun arrow.Function0.Companion.applicative(): Function0ApplicativeInstance =
        object : Function0ApplicativeInstance, arrow.Applicative<Function0HK> {}

interface Function0MonadInstance : arrow.Monad<Function0HK> {
    override fun <A, B> ap(fa: arrow.Function0Kind<A>, ff: arrow.Function0Kind<kotlin.Function1<A, B>>): arrow.Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, arrow.Function0Kind<B>>): arrow.Function0<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.Function0Kind<arrow.Either<A, B>>>): arrow.Function0<B> =
            arrow.Function0.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Function0<A> =
            arrow.Function0.pure(a)
}

object Function0MonadInstanceImplicits {
    fun instance(): Function0MonadInstance = arrow.Function0.Companion.monad()
}

fun arrow.Function0.Companion.monad(): Function0MonadInstance =
        object : Function0MonadInstance, arrow.Monad<Function0HK> {}

interface Function0ComonadInstance : arrow.Comonad<Function0HK> {
    override fun <A, B> coflatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<arrow.Function0Kind<A>, B>): arrow.Function0<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.Function0Kind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)
}

object Function0ComonadInstanceImplicits {
    fun instance(): Function0ComonadInstance = arrow.Function0.Companion.comonad()
}

fun arrow.Function0.Companion.comonad(): Function0ComonadInstance =
        object : Function0ComonadInstance, arrow.Comonad<Function0HK> {}

interface Function0BimonadInstance : arrow.Bimonad<Function0HK> {
    override fun <A, B> ap(fa: arrow.Function0Kind<A>, ff: arrow.Function0Kind<kotlin.Function1<A, B>>): arrow.Function0<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, arrow.Function0Kind<B>>): arrow.Function0<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.Function0Kind<arrow.Either<A, B>>>): arrow.Function0<B> =
            arrow.Function0.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.Function0Kind<A>, f: kotlin.Function1<A, B>): arrow.Function0<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Function0<A> =
            arrow.Function0.pure(a)

    override fun <A, B> coflatMap(fa: arrow.Function0Kind<A>, f: kotlin.Function1<arrow.Function0Kind<A>, B>): arrow.Function0<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.Function0Kind<A>): A =
            fa.ev().extract()
}

object Function0BimonadInstanceImplicits {
    fun instance(): Function0BimonadInstance = arrow.Function0.Companion.bimonad()
}

fun arrow.Function0.Companion.bimonad(): Function0BimonadInstance =
        object : Function0BimonadInstance, arrow.Bimonad<Function0HK> {}
