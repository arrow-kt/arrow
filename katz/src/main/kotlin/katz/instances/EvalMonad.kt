package katz

interface EvalMonad : Monad<Eval.F> {
    override fun <A> pure(a: A): Eval<A> =
            Eval.now(a)

    override fun <A, B> flatMap(fa: HK<Eval.F, A>, f: (A) -> HK<Eval.F, B>): Eval<B> =
            fa.ev().flatMap({ f(it).ev() })

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<Eval.F, Either<A, B>>): Eval<B> = TODO()
            /*f(a).ev().let { eval: Eval<Either<A, B>> ->
                when (eval) {
                    is Eval.Now<Either<A, B>> -> { nest: Eval.Now<Either<A, B>> ->
                        when (eval.value) {
                            is Either.Left<*> -> tailRecM(eval.value.a as A, f)
                            is Either.Right<*> -> Eval.Now(eval.value.b as B)
                        }
                    }
                    is Eval.Later<Either<A, B>> ->
                        when (eval.value()) {
                            is Either.Left<*> -> tailRecM(eval.value.a as A, f)
                            is Either.Right<*> -> Eval.Later({ eval.value.b } as B)
                        }
                    is Eval.Always<Either<A, B>> ->
                        when (eval.value()) {
                            is Either.Left<*> -> tailRecM(eval.value.a as A, f)
                            is Either.Right<*> -> Eval.Now(eval.value.b as B)
                        }
                    is Eval.Call<Either<A, B>> ->
                        when (eval.value) {
                            is Either.Left<*> -> tailRecM(eval.value.a as A, f)
                            is Either.Right<*> -> Eval.Now(eval.value.b as B)
                        }
                    is Eval.Compute<Either<A, B>> ->
                        when (eval.value) {
                            is Either.Left<*> -> tailRecM(eval.value.a as A, f)
                            is Either.Right<*> -> Eval.Now(eval.value.b as B)
                        }
                }

            }*/


}
