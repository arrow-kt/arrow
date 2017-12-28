package arrow

@instance(SetKW::class)
interface SetKWSemigroupInstance<A> : Semigroup<SetKW<A>> {
    override fun combine(a: SetKW<A>, b: SetKW<A>): SetKW<A> = (a + b).k()
}

@instance(SetKW::class)
interface SetKWMonoidInstance<A> : SetKWSemigroupInstance<A>, Monoid<SetKW<A>> {
    override fun empty(): SetKW<A> = emptySet<A>().k()
}

@instance(SetKW::class)
interface SetKWEqInstance<A> : Eq<SetKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SetKW<A>, b: SetKW<A>): Boolean =
            if (a.size == b.size) a.set.map { aa ->
                b.find { bb -> EQ().eqv(aa, bb) }.nonEmpty()
            }.fold(true) { acc, bool ->
                acc && bool
            }
            else false

}

interface SetKWFoldableInstance : arrow.Foldable<SetKWHK> {
    override fun <A, B> foldLeft(fa: arrow.SetKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.SetKWKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: arrow.SetKWKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

object SetKWFoldableInstanceImplicits {
    fun instance(): SetKWFoldableInstance = arrow.SetKW.Companion.foldable()
}

fun arrow.SetKW.Companion.foldable(): SetKWFoldableInstance =
        object : SetKWFoldableInstance, arrow.Foldable<SetKWHK> {}

interface SetKWSemigroupKInstance : arrow.SemigroupK<SetKWHK> {
    override fun <A> combineK(x: arrow.SetKWKind<A>, y: arrow.SetKWKind<A>): arrow.SetKW<A> =
            x.ev().combineK(y)
}

object SetKWSemigroupKInstanceImplicits {
    fun instance(): SetKWSemigroupKInstance = arrow.SetKW.Companion.semigroupK()
}

fun arrow.SetKW.Companion.semigroupK(): SetKWSemigroupKInstance =
        object : SetKWSemigroupKInstance, arrow.SemigroupK<SetKWHK> {}

interface SetKWMonoidKInstance : arrow.MonoidK<SetKWHK> {
    override fun <A> empty(): arrow.SetKW<A> =
            arrow.SetKW.empty()

    override fun <A> combineK(x: arrow.SetKWKind<A>, y: arrow.SetKWKind<A>): arrow.SetKW<A> =
            x.ev().combineK(y)
}

object SetKWMonoidKInstanceImplicits {
    fun instance(): SetKWMonoidKInstance = arrow.SetKW.Companion.monoidK()
}

fun arrow.SetKW.Companion.monoidK(): SetKWMonoidKInstance =
        object : SetKWMonoidKInstance, arrow.MonoidK<SetKWHK> {}
