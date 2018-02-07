package arrow.free

import arrow.Kind
import arrow.core.*
import arrow.data.*

val cofreeOptionToNel: FunctionK<CofreePartialOf<ForOption>, ForNonEmptyList> = object : FunctionK<CofreePartialOf<ForOption>, ForNonEmptyList> {
    override fun <A> invoke(fa: Kind<CofreePartialOf<ForOption>, A>): Kind<ForNonEmptyList, A> =
            fa.reify().let { c ->
                NonEmptyList.fromListUnsafe(listOf(c.head) + c.tailForced().reify().fold({ listOf<A>() }, { invoke(it).reify().all }))
            }
}

val cofreeListToNel: FunctionK<CofreePartialOf<ForListK>, ForNonEmptyList> = object : FunctionK<CofreePartialOf<ForListK>, ForNonEmptyList> {
    override fun <A> invoke(fa: Kind<CofreePartialOf<ForListK>, A>): Kind<ForNonEmptyList, A> =
            fa.reify().let { c: Cofree<ForListK, A> ->
                val all: List<Cofree<ForListK, A>> = c.tailForced().reify()
                val tail: List<A> = all.foldRight(listOf<A>(), { v, acc -> acc + invoke(v).reify().all })
                val headL: List<A> = listOf(c.head)
                NonEmptyList.fromListUnsafe(headL + tail)
            }
}

val optionToList: FunctionK<ForOption, ForListK> = object : FunctionK<ForOption, ForListK> {
    override fun <A> invoke(fa: Kind<ForOption, A>): Kind<ForListK, A> =
            fa.reify().fold({ listOf<A>().k() }, { listOf(it).k() })
}

val optionInterpreter: FunctionK<Ops.F, ForOption> = object : FunctionK<Ops.F, ForOption> {
    override fun <A> invoke(fa: Kind<Ops.F, A>): Option<A> {
        val op = fa.reify()
        return when (op) {
            is Ops.Add -> Some(op.a + op.y)
            is Ops.Subtract -> Some(op.a - op.y)
            is Ops.Value -> Some(op.a)
        } as Option<A>
    }
}
val optionApInterpreter: FunctionK<OpsAp.F, ForOption> = object : FunctionK<OpsAp.F, ForOption> {
    override fun <A> invoke(fa: Kind<OpsAp.F, A>): Option<A> {
        val op = fa.reify()
        return when (op) {
            is OpsAp.Add -> Some(op.a + op.y)
            is OpsAp.Subtract -> Some(op.a - op.y)
            is OpsAp.Value -> Some(op.a)
        } as Option<A>
    }
}
val nonEmptyListInterpreter: FunctionK<Ops.F, ForNonEmptyList> = object : FunctionK<Ops.F, ForNonEmptyList> {
    override fun <A> invoke(fa: Kind<Ops.F, A>): NonEmptyList<A> {
        val op = fa.reify()
        return when (op) {
            is Ops.Add -> NonEmptyList.of(op.a + op.y)
            is Ops.Subtract -> NonEmptyList.of(op.a - op.y)
            is Ops.Value -> NonEmptyList.of(op.a)
        } as NonEmptyList<A>
    }
}
val nonEmptyListApInterpreter: FunctionK<OpsAp.F, ForNonEmptyList> = object : FunctionK<OpsAp.F, ForNonEmptyList> {
    override fun <A> invoke(fa: Kind<OpsAp.F, A>): NonEmptyList<A> {
        val op = fa.reify()
        return when (op) {
            is OpsAp.Add -> NonEmptyList.of(op.a + op.y)
            is OpsAp.Subtract -> NonEmptyList.of(op.a - op.y)
            is OpsAp.Value -> NonEmptyList.of(op.a)
        } as NonEmptyList<A>
    }
}
val idInterpreter: FunctionK<Ops.F, ForId> = object : FunctionK<Ops.F, ForId> {
    override fun <A> invoke(fa: Kind<Ops.F, A>): Id<A> {
        val op = fa.reify()
        return when (op) {
            is Ops.Add -> Id(op.a + op.y)
            is Ops.Subtract -> Id(op.a - op.y)
            is Ops.Value -> Id(op.a)
        } as Id<A>
    }
}
val idApInterpreter: FunctionK<OpsAp.F, ForId> = object : FunctionK<OpsAp.F, ForId> {
    override fun <A> invoke(fa: Kind<OpsAp.F, A>): Id<A> {
        val op = fa.reify()
        return when (op) {
            is OpsAp.Add -> Id(op.a + op.y)
            is OpsAp.Subtract -> Id(op.a - op.y)
            is OpsAp.Value -> Id(op.a)
        } as Id<A>
    }
}