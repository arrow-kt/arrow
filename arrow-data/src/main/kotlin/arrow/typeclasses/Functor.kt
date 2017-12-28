package arrow

interface Functor<F> : Typeclass {

    fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>

    fun <A, B> lift(f: (A) -> B): (HK<F, A>) -> HK<F, B> =
            { fa: HK<F, A> ->
                map(fa, f)
            }

    fun <A> void(fa: HK<F, A>): HK<F, Unit> = map(fa, { _ -> Unit })

    fun <A, B> fproduct(fa: HK<F, A>, f: (A) -> B): HK<F, Tuple2<A, B>> = map(fa, { a -> Tuple2(a, f(a)) })

    fun <A, B> `as`(fa: HK<F, A>, b: B): HK<F, B> = map(fa, { _ -> b })

    fun <A, B> tupleLeft(fa: HK<F, A>, b: B): HK<F, Tuple2<B, A>> = map(fa, { a -> Tuple2(b, a) })

    fun <A, B> tupleRight(fa: HK<F, A>, b: B): HK<F, Tuple2<A, B>> = map(fa, { a -> Tuple2(a, b) })

}

fun <F, B, A : B> Functor<F>.widen(fa: HK<F, A>): HK<F, B> = fa as HK<F, B>

inline fun <reified F> functor(): Functor<F> = instance(InstanceParametrizedType(Functor::class.java, listOf(typeLiteral<F>())))

//Syntax

inline fun <reified F, A, B> HK<F, A>.map(FT : Functor<F> = functor(), noinline f: (A) -> B): HK<F, B> = FT.map(this, f)

inline fun <reified F, A, B> ((A) -> B).lift(FT : Functor<F> = functor()): (HK<F, A>) -> HK<F, B> = FT.lift(this)