package arrow

import arrow.core.FunctionK
import arrow.core.Id
import arrow.core.Option
import arrow.core.Some
import arrow.data.NonEmptyList
import arrow.data.k
import arrow.free.Cofree

val cofreeOptionToNel: FunctionK<CofreeKindPartial<OptionHK>, NonEmptyListHK> = object : FunctionK<CofreeKindPartial<OptionHK>, NonEmptyListHK> {
    override fun <A> invoke(fa: HK<CofreeKindPartial<OptionHK>, A>): HK<NonEmptyListHK, A> =
            arrow.test.laws.ev().let { c ->
                NonEmptyList.fromListUnsafe(listOf(c.head) + c.tailForced().ev().fold({ listOf<A>() }, { invoke(it).ev().all }))
            }
}

val cofreeListToNel: FunctionK<CofreeKindPartial<ListKWHK>, NonEmptyListHK> = object : FunctionK<CofreeKindPartial<ListKWHK>, NonEmptyListHK> {
    override fun <A> invoke(fa: HK<CofreeKindPartial<ListKWHK>, A>): HK<NonEmptyListHK, A> =
            arrow.test.laws.ev().let { c: Cofree<ListKWHK, A> ->
                val all: List<Cofree<ListKWHK, A>> = arrow.test.laws.ev()
                val tail: List<A> = all.foldRight(listOf<A>(), { v, acc -> acc + invoke(v).ev().all })
                val headL: List<A> = listOf(c.head)
                NonEmptyList.fromListUnsafe(headL + tail)
            }
}

val optionToList: FunctionK<OptionHK, ListKWHK> = object : FunctionK<OptionHK, ListKWHK> {
    override fun <A> invoke(fa: HK<OptionHK, A>): HK<ListKWHK, A> =
            arrow.test.laws.ev().fold({ listOf<A>().k() }, { listOf(it).k() })
}

val optionInterpreter: FunctionK<Ops.F, OptionHK> = object : FunctionK<Ops.F, OptionHK> {
    override fun <A> invoke(fa: HK<Ops.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is Ops.Add -> Some(op.a + op.y)
            is Ops.Subtract -> Some(op.a - op.y)
            is Ops.Value -> Some(op.a)
        } as Option<A>
    }
}
val optionApInterpreter: FunctionK<OpsAp.F, OptionHK> = object : FunctionK<OpsAp.F, OptionHK> {
    override fun <A> invoke(fa: HK<OpsAp.F, A>): Option<A> {
        val op = fa.ev()
        return when (op) {
            is OpsAp.Add -> Some(op.a + op.y)
            is OpsAp.Subtract -> Some(op.a - op.y)
            is OpsAp.Value -> Some(op.a)
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
