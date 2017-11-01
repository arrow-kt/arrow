package kategory

object UnitSemigroupInstance : Semigroup<Unit> {
    override fun combine(a: Unit, b: Unit) = Unit
}

object UnitSemigroupInstanceImplicits {
    @JvmStatic fun instance() = UnitSemigroupInstance
}

object UnitMonoidInstance : Monoid<Unit> {
    override fun empty() = Unit

    override fun combine(a: Unit, b: Unit) = Unit
}

object UnitMonoidInstanceImplicits {
    @JvmStatic fun instance() = UnitMonoidInstance
}

object UnitEqInstance : Eq<Unit> {
    override fun eqv(a: Unit, b: Unit) = true
}

object UnitEqInstanceImplicits {
    @JvmStatic fun instance() = UnitEqInstance
}