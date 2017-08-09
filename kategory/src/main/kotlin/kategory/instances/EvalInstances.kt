package kategory

interface EvalInstances :
        Functor<EvalHK>,
        Applicative<EvalHK>,
        Monad<EvalHK> {

    override fun <A> pure(a: A): Eval<A> = Eval.now(a)

    override fun <A, B> map(fa: HK<EvalHK, A>, f: (A) -> B): Eval<B> = fa.ev().map(f)

    override fun <A, B> flatMap(fa: HK<EvalHK, A>, f: (A) -> HK<EvalHK, B>): Eval<B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<EvalHK, Either<A, B>>): Eval<B> =
            f(a).ev().flatMap { eval: Either<A, B> ->
                when (eval) {
                    is Either.Left -> tailRecM(eval.a, f)
                    is Either.Right -> pure(eval.b)
                }
            }

}
