package kategory

val cofreeOptionToNel: FunctionK<CofreeF<OptionHK>, NonEmptyListHK> = object : FunctionK<CofreeF<OptionHK>, NonEmptyListHK> {
    override fun <A> invoke(fa: HK<CofreeF<OptionHK>, A>): HK<NonEmptyListHK, A> =
            fa.ev().let { c ->
                NonEmptyList.fromListUnsafe(listOf(c.head) + c.tailForced().ev().fold({ listOf<A>() }, { invoke(it).ev().all }))
            }
}

val cofreeListToNel: FunctionK<CofreeF<ListT.ListF>, NonEmptyListHK> = object : FunctionK<CofreeF<ListT.ListF>, NonEmptyListHK> {
    override fun <A> invoke(fa: HK<CofreeF<ListT.ListF>, A>): HK<NonEmptyListHK, A> =
            fa.ev().let { c: Cofree<ListT.ListF, A> ->
                val all: List<Cofree<ListT.ListF, A>> = c.tailForced().lev().all
                val tail: List<A> = all.foldRight(listOf<A>(), { v, acc -> acc + invoke(v).ev().all })
                val headL: List<A> = listOf(c.head)
                NonEmptyList.fromListUnsafe(headL + tail)
            }
}

fun <A> HK<ListT.ListF, A>.lev() = this as ListT<A>

val optionToList: FunctionK<OptionHK, ListT.ListF> = object : FunctionK<OptionHK, ListT.ListF> {
    override fun <A> invoke(fa: HK<OptionHK, A>): HK<ListT.ListF, A> =
            fa.ev().fold({ ListT(listOf()) }, { ListT(listOf(it)) })
}

val optionInterpreter: FunctionK<Ops.F, OptionHK> = object : FunctionK<Ops.F, OptionHK> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Option.Some(op.a + op.y)
            is Ops.Subtract -> Option.Some(op.a - op.y)
            is Ops.Value -> Option.Some(op.a)
        } as Option<A>
    }
}
val optionApInterpreter: FunctionK<OpsAp.F, OptionHK> = object : FunctionK<OpsAp.F, OptionHK> {
    override fun <A> invoke(fa: HK<OpsAp.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is OpsAp.Add -> Option.Some(op.a + op.y)
            is OpsAp.Subtract -> Option.Some(op.a - op.y)
            is OpsAp.Value -> Option.Some(op.a)
        } as Option<A>
    }
}
val nonEmptyListInterpreter: FunctionK<Ops.F, NonEmptyListHK> = object : FunctionK<Ops.F, NonEmptyListHK> {
    override fun <A> invoke(fa: HK<Ops.F, A>): NonEmptyList<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> NonEmptyList.of(op.a + op.y)
            is Ops.Subtract -> NonEmptyList.of(op.a - op.y)
            is Ops.Value -> NonEmptyList.of(op.a)
        } as NonEmptyList<A>
    }
}
val nonEmptyListApInterpreter: FunctionK<OpsAp.F, NonEmptyListHK> = object : FunctionK<OpsAp.F, NonEmptyListHK> {
    override fun <A> invoke(fa: HK<OpsAp.F, A>): NonEmptyList<A> {
        val op = fa.ev()
        return when (op) {
            is OpsAp.Add -> NonEmptyList.of(op.a + op.y)
            is OpsAp.Subtract -> NonEmptyList.of(op.a - op.y)
            is OpsAp.Value -> NonEmptyList.of(op.a)
        } as NonEmptyList<A>
    }
}
val idInterpreter: FunctionK<Ops.F, IdHK> = object : FunctionK<Ops.F, IdHK> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Id<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Id(op.a + op.y)
            is Ops.Subtract -> Id(op.a - op.y)
            is Ops.Value -> Id(op.a)
        } as Id<A>
    }
}
val idApInterpreter: FunctionK<OpsAp.F, IdHK> = object : FunctionK<OpsAp.F, IdHK> {
    override fun <A> invoke(fa: HK<OpsAp.F, A>): Id<A> {
        val op = fa.ev()
        return when (op) {
            is OpsAp.Add -> Id(op.a + op.y)
            is OpsAp.Subtract -> Id(op.a - op.y)
            is OpsAp.Value -> Id(op.a)
        } as Id<A>
    }
}
