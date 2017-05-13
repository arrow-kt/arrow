package katz.free

import katz.*

val cofreeOptionToNel: FunctionK<CofreeF<Option.F>, NonEmptyList.F> = object : FunctionK<CofreeF<Option.F>, NonEmptyList.F> {
    override fun <A> invoke(fa: HK<CofreeF<Option.F>, A>): HK<NonEmptyList.F, A> =
            fa.ev().let { c ->
                NonEmptyList.fromListUnsafe(listOf(c.head) + c.tailForced().ev().fold({ listOf<A>() }, { invoke(it).ev().all }))
            }
}

val cofreeListToNel: FunctionK<CofreeF<CofreeTest.ListT.ListF>, NonEmptyList.F> = object : FunctionK<CofreeF<CofreeTest.ListT.ListF>, NonEmptyList.F> {
    override fun <A> invoke(fa: HK<CofreeF<CofreeTest.ListT.ListF>, A>): HK<NonEmptyList.F, A> =
            fa.ev().let { c: Cofree<CofreeTest.ListT.ListF, A> ->
                val all: List<Cofree<CofreeTest.ListT.ListF, A>> = c.tailForced().ev().all
                val tail: List<A> = all.foldRight(listOf<A>(), { v, acc -> acc + invoke(v).ev().all })
                val headL: List<A> = listOf(c.head)
                NonEmptyList.fromListUnsafe(headL + tail)
            }
}

val optionToList: FunctionK<Option.F, CofreeTest.ListT.ListF> = object : FunctionK<Option.F, CofreeTest.ListT.ListF> {
    override fun <A> invoke(fa: HK<Option.F, A>): HK<CofreeTest.ListT.ListF, A> =
            fa.ev().fold({ CofreeTest.ListT(listOf()) }, { CofreeTest.ListT(listOf(it)) })
}

val optionInterpreter: FunctionK<Ops.F, Option.F> = object : FunctionK<Ops.F, Option.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Option.Some(op.a + op.y)
            is Ops.Subtract -> Option.Some(op.a - op.y)
            is Ops.Value -> Option.Some(op.a)
        } as Option<A>
    }
}
val nonEmptyListInterpreter: FunctionK<Ops.F, NonEmptyList.F> = object : FunctionK<Ops.F, NonEmptyList.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): NonEmptyList<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> NonEmptyList.of(op.a + op.y)
            is Ops.Subtract -> NonEmptyList.of(op.a - op.y)
            is Ops.Value -> NonEmptyList.of(op.a)
        } as NonEmptyList<A>
    }
}
val idInterpreter: FunctionK<Ops.F, Id.F> = object : FunctionK<Ops.F, Id.F> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Id<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Id(op.a + op.y)
            is Ops.Subtract -> Id(op.a - op.y)
            is Ops.Value -> Id(op.a)
        } as Id<A>
    }
}