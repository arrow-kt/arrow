package arrow.free

import arrow.Kind
import arrow.core.ForId
import arrow.core.ForOption
import arrow.core.FunctionK
import arrow.core.Id
import arrow.core.Option
import arrow.core.Some
import arrow.core.fix
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.k

val cofreeOptionToNel: FunctionK<CofreePartialOf<ForOption>, ForNonEmptyList> = object : FunctionK<CofreePartialOf<ForOption>, ForNonEmptyList> {
  override fun <A> invoke(fa: Kind<CofreePartialOf<ForOption>, A>): Kind<ForNonEmptyList, A> =
    fa.fix().let { c ->
      NonEmptyList.fromListUnsafe(listOf(c.head) + c.tailForced().fix().fold({ listOf<A>() }, { invoke(it).fix().all }))
    }
}

val cofreeListToNel: FunctionK<CofreePartialOf<ForListK>, ForNonEmptyList> = object : FunctionK<CofreePartialOf<ForListK>, ForNonEmptyList> {
  override fun <A> invoke(fa: Kind<CofreePartialOf<ForListK>, A>): Kind<ForNonEmptyList, A> =
    fa.fix().let { c: Cofree<ForListK, A> ->
      val all: List<Cofree<ForListK, A>> = c.tailForced().fix()
      val tail: List<A> = all.foldRight(listOf<A>()) { v, acc -> acc + invoke(v).fix().all }
      val headL: List<A> = listOf(c.head)
      NonEmptyList.fromListUnsafe(headL + tail)
    }
}

val optionToList: FunctionK<ForOption, ForListK> = object : FunctionK<ForOption, ForListK> {
  override fun <A> invoke(fa: Kind<ForOption, A>): Kind<ForListK, A> =
    fa.fix().fold({ listOf<A>().k() }, { listOf(it).k() })
}

@Suppress("UNCHECKED_CAST")
val optionInterpreter: FunctionK<ForOps, ForOption> = object : FunctionK<ForOps, ForOption> {
  override fun <A> invoke(fa: Kind<ForOps, A>): Option<A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> Some(op.a + op.y)
      is Ops.Subtract -> Some(op.a - op.y)
      is Ops.Value -> Some(op.a)
    } as Option<A>
  }
}

@Suppress("UNCHECKED_CAST")
val optionApInterpreter: FunctionK<OpsAp.F, ForOption> = object : FunctionK<OpsAp.F, ForOption> {
  override fun <A> invoke(fa: Kind<OpsAp.F, A>): Option<A> {
    val op = fa.fix()
    return when (op) {
      is OpsAp.Add -> Some(op.a + op.y)
      is OpsAp.Subtract -> Some(op.a - op.y)
      is OpsAp.Value -> Some(op.a)
    } as Option<A>
  }
}

@Suppress("UNCHECKED_CAST")
val nonEmptyListInterpreter: FunctionK<ForOps, ForNonEmptyList> = object : FunctionK<ForOps, ForNonEmptyList> {
  override fun <A> invoke(fa: Kind<ForOps, A>): NonEmptyList<A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> NonEmptyList.of(op.a + op.y)
      is Ops.Subtract -> NonEmptyList.of(op.a - op.y)
      is Ops.Value -> NonEmptyList.of(op.a)
    } as NonEmptyList<A>
  }
}

@Suppress("UNCHECKED_CAST")
val nonEmptyListApInterpreter: FunctionK<OpsAp.F, ForNonEmptyList> = object : FunctionK<OpsAp.F, ForNonEmptyList> {
  override fun <A> invoke(fa: Kind<OpsAp.F, A>): NonEmptyList<A> {
    val op = fa.fix()
    return when (op) {
      is OpsAp.Add -> NonEmptyList.of(op.a + op.y)
      is OpsAp.Subtract -> NonEmptyList.of(op.a - op.y)
      is OpsAp.Value -> NonEmptyList.of(op.a)
    } as NonEmptyList<A>
  }
}

@Suppress("UNCHECKED_CAST")
val idInterpreter: FunctionK<ForOps, ForId> = object : FunctionK<ForOps, ForId> {
  override fun <A> invoke(fa: Kind<ForOps, A>): Id<A> {
    val op = fa.fix()
    return when (op) {
      is Ops.Add -> Id(op.a + op.y)
      is Ops.Subtract -> Id(op.a - op.y)
      is Ops.Value -> Id(op.a)
    } as Id<A>
  }
}

@Suppress("UNCHECKED_CAST")
val idApInterpreter: FunctionK<OpsAp.F, ForId> = object : FunctionK<OpsAp.F, ForId> {
  override fun <A> invoke(fa: Kind<OpsAp.F, A>): Id<A> {
    val op = fa.fix()
    return when (op) {
      is OpsAp.Add -> Id(op.a + op.y)
      is OpsAp.Subtract -> Id(op.a - op.y)
      is OpsAp.Value -> Id(op.a)
    } as Id<A>
  }
}
