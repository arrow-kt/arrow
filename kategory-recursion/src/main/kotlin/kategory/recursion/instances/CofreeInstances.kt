package kategory

fun <W, B, A> Cofree.Companion.birecursive(): Birecursive<CofreeKindPartial<W>, EnvTKind<B, W, A>> {
    val alg: (HK<Nested<CofreeKindPartial<W>, EnvTKind<B, W, A>>, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>>) -> HK<CofreeKindPartial<W>, EnvTKind<B, W, A>> =
            { c: HK<Nested<CofreeKindPartial<W>, EnvTKind<B, W, A>>, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>> ->
                /*
                    val cc: Cofree<W, HK<EnvTKindPartial<B, W>, HK<CofreeKindPartial<W>, EnvTKindPartial<B, W>>>> = c.unnest().ev()
                    val head: EnvT<B, W, HK<CofreeKindPartial<W>, EnvTKindPartial<B, W>>> = cc.head.ev()
                    val lower: HK<W, HK<CofreeKindPartial<W>, EnvTKindPartial<B, W>>> = head.lower
                    val tail: Eval.Later<HK<W, Cofree<W, EnvTKindPartial<B, W>>>> = Eval.later { lower }
                    // HK<W, Cofree<W, B>>
                    val cofree = Cofree<W, EnvTKindPartial<B, CofreeKindPartial<W>>>(cc.FS, head, tail)
                    val nest: NestedType<CofreeKindPartial<W>, EnvTKindPartial<B, W>, CofreeKindPartial<W>> = cofree.nest()
                    nest
                    */
                null
            }
    val coalg: (HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>) -> HK<Nested<CofreeKindPartial<W>, EnvTKind<B, W, A>>, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>> =
            { c: HK<CofreeKindPartial<W>, EnvTKind<B, W, A>> ->
                val cofree: Cofree<W, EnvTKind<B, W, A>> = c.ev()
                val cofreeHead: EnvTKind<B, W, A> = cofree.head
                val tail: Eval<CofreeEval<W, EnvTKind<B, W, A>>> = cofree.tail
                // HK<S, Cofree<S, A>>
                val value: CofreeEval<W, EnvTKind<B, W, A>> = tail.value()
                val envT: EnvT<EnvTKind<B, W, A>, W, Cofree<W, EnvTKind<B, W, A>>> = EnvT(cofreeHead, value)
                val head1: EnvTKind<B, W, A> = envT.ask
                val head: EnvT<B, W, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>> = EnvT(null, cofree.tail.value())


                val lower1: HK<W, Cofree<W, EnvTKind<B, W, HK<CofreeKindPartial<W>, EnvTKindPartial<B, W>>>>> = envT.lower

                val value1: HK<W, Cofree<W, EnvTKindPartial<B, W>>> = tail.value()
                val lower: HK<W, Cofree<W, EnvTKind<B, W, HK<CofreeKindPartial<W>, EnvTKindPartial<B, W>>>>> = value1

                val cofree3: Cofree<W, EnvTKind<B, W, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>>> = Cofree(cofree.FS, head, Eval.later { lower })

                val tail1: Eval.Later<HK<W, Cofree<W, EnvTKind<B, W, HK<CofreeKindPartial<W>, EnvTKindPartial<B, W>>>>>> = Eval.later { lower }

                val tailTrue: Eval<CofreeEval<W, EnvTKind<B, W, A>>> = Eval.later { value }

                val headTrue: HK<HK<HK<EnvTHK, B>, W>, A> = null
                val headTrue2: HK<HK<HK<EnvTHK, B>, W>, EnvTKind<B, W, A>> = null

                val cofreew: Cofree<W, HK<HK<HK<EnvTHK, B>, W>, A>> = Cofree(cofree.FS, headTrue, tailTrue)
                val cofreeNest: NestedType<HK<CofreeHK, W>, HK<HK<EnvTHK, B>, W>, A> = Cofree(cofree.FS, headTrue, tailTrue).nest()
                val cofreeNest2: NestedType<HK<CofreeHK, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>>, HK2<F, A, B>, A> = Cofree<HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>, EnvTKind<B, W, A>>(cofree.FS, headTrue2, tailTrue).nest()

                val cofree2: UnnestedType<CofreeKindPartial<W>, EnvTKind<B, W, A>, CofreeKind<W, EnvTKind<B, W, A>>> = Cofree(cofree.FS, headTrue, tailTrue)
                val nest: NestedType<CofreeKindPartial<W>, EnvTKind<B, W, A>, HK<CofreeKindPartial<W>, EnvTKind<B, W, A>>> = cofree2.nest()
                nest
            }
    return algebraIso(alg, coalg)
}