package kategory

interface CoyonedaFunctorInstance<F, G> : Functor<CoyonedaKindPartial<F, G>> {
    override fun <A, B> map(fa: CoyonedaKind<F, G, A>, f: (A) -> B): Coyoneda<F, G, B> = fa.ev().map(f)
}

object CoyonedaFunctorInstanceImplicits {
    @JvmStatic fun <F, G> instance(): CoyonedaFunctorInstance<F, G> = object : CoyonedaFunctorInstance<F, G> {}
}