package arrow

@instance(Tuple2::class)
interface Tuple2MonoidInstance<A, B> : Monoid<Tuple2<A, B>> {

    fun MA(): Monoid<A>

    fun MB(): Monoid<B>

    override fun empty(): Tuple2<A, B> = Tuple2(MA().empty(), MB().empty())

    override fun combine(a: Tuple2<A, B>, b: Tuple2<A, B>): Tuple2<A, B> {
        val (xa, xb) = a
        val (ya, yb) = b
        return Tuple2(MA().combine(xa, ya), MB().combine(xb, yb))
    }
}

@instance(Tuple2::class)
interface Tuple2EqInstance<A, B> : Eq<Tuple2<A, B>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    override fun eqv(a: Tuple2<A, B>, b: Tuple2<A, B>): Boolean =
            EQA().eqv(a.a, b.a) && EQB().eqv(a.b, b.b)
}

@instance(Tuple3::class)
interface Tuple3EqInstance<A, B, C> : Eq<Tuple3<A, B, C>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    override fun eqv(a: Tuple3<A, B, C>, b: Tuple3<A, B, C>): Boolean =
            EQA().eqv(a.a, b.a) && EQB().eqv(a.b, b.b) && EQC().eqv(a.c, b.c)
}

@instance(Tuple4::class)
interface Tuple4EqInstance<A, B, C, D> : Eq<Tuple4<A, B, C, D>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    override fun eqv(a: Tuple4<A, B, C, D>, b: Tuple4<A, B, C, D>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
}

@instance(Tuple5::class)
interface Tuple5EqInstance<A, B, C, D, E> : Eq<Tuple5<A, B, C, D, E>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    fun EQE(): Eq<E>

    override fun eqv(a: Tuple5<A, B, C, D, E>, b: Tuple5<A, B, C, D, E>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
                    && EQE().eqv(a.e, b.e)

}

@instance(Tuple6::class)
interface Tuple6EqInstance<A, B, C, D, E, F> : Eq<Tuple6<A, B, C, D, E, F>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    fun EQE(): Eq<E>

    fun EQF(): Eq<F>

    override fun eqv(a: Tuple6<A, B, C, D, E, F>, b: Tuple6<A, B, C, D, E, F>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
                    && EQE().eqv(a.e, b.e)
                    && EQF().eqv(a.f, b.f)

}

@instance(Tuple7::class)
interface Tuple7EqInstance<A, B, C, D, E, F, G> : Eq<Tuple7<A, B, C, D, E, F, G>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    fun EQE(): Eq<E>

    fun EQF(): Eq<F>

    fun EQG(): Eq<G>

    override fun eqv(a: Tuple7<A, B, C, D, E, F, G>, b: Tuple7<A, B, C, D, E, F, G>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
                    && EQE().eqv(a.e, b.e)
                    && EQF().eqv(a.f, b.f)
                    && EQG().eqv(a.g, b.g)

}

@instance(Tuple8::class)
interface Tuple8EqInstance<A, B, C, D, E, F, G, H> : Eq<Tuple8<A, B, C, D, E, F, G, H>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    fun EQE(): Eq<E>

    fun EQF(): Eq<F>

    fun EQG(): Eq<G>

    fun EQH(): Eq<H>

    override fun eqv(a: Tuple8<A, B, C, D, E, F, G, H>, b: Tuple8<A, B, C, D, E, F, G, H>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
                    && EQE().eqv(a.e, b.e)
                    && EQF().eqv(a.f, b.f)
                    && EQG().eqv(a.g, b.g)
                    && EQH().eqv(a.h, b.h)

}

@instance(Tuple9::class)
interface Tuple9EqInstance<A, B, C, D, E, F, G, H, I> : Eq<Tuple9<A, B, C, D, E, F, G, H, I>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    fun EQE(): Eq<E>

    fun EQF(): Eq<F>

    fun EQG(): Eq<G>

    fun EQH(): Eq<H>

    fun EQI(): Eq<I>

    override fun eqv(a: Tuple9<A, B, C, D, E, F, G, H, I>, b: Tuple9<A, B, C, D, E, F, G, H, I>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
                    && EQE().eqv(a.e, b.e)
                    && EQF().eqv(a.f, b.f)
                    && EQG().eqv(a.g, b.g)
                    && EQH().eqv(a.h, b.h)
                    && EQI().eqv(a.i, b.i)

}

@instance(Tuple10::class)
interface Tuple10EqInstance<A, B, C, D, E, F, G, H, I, J> : Eq<Tuple10<A, B, C, D, E, F, G, H, I, J>> {

    fun EQA(): Eq<A>

    fun EQB(): Eq<B>

    fun EQC(): Eq<C>

    fun EQD(): Eq<D>

    fun EQE(): Eq<E>

    fun EQF(): Eq<F>

    fun EQG(): Eq<G>

    fun EQH(): Eq<H>

    fun EQI(): Eq<I>

    fun EQJ(): Eq<J>

    override fun eqv(a: Tuple10<A, B, C, D, E, F, G, H, I, J>, b: Tuple10<A, B, C, D, E, F, G, H, I, J>): Boolean =
            EQA().eqv(a.a, b.a)
                    && EQB().eqv(a.b, b.b)
                    && EQC().eqv(a.c, b.c)
                    && EQD().eqv(a.d, b.d)
                    && EQE().eqv(a.e, b.e)
                    && EQF().eqv(a.f, b.f)
                    && EQG().eqv(a.g, b.g)
                    && EQH().eqv(a.h, b.h)
                    && EQI().eqv(a.i, b.i)
                    && EQJ().eqv(a.j, b.j)

}