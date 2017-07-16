package kategory

import kategory.Option.None
import kategory.Option.Some

object OptionTraverse : Traverse<Option.F> {
    override fun <G, A, B> traverse(fa: HK<Option.F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<Option.F, B>> =
            fa.ev().let { option ->
                when (option) {
                    is Some -> GA.map(f(option.value), { Some(it) })
                    is None -> GA.pure(None)
                }
            }

    override fun <A, B> foldL(fa: HK<Option.F, A>, b: B, f: (B, A) -> B): B =
            fa.ev().let { option ->
                when (option) {
                    is Some -> f(b, option.value)
                    is None -> b
                }
            }

    override fun <A, B> foldR(fa: HK<Option.F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().let { option ->
                when (option) {
                    is Some -> f(option.value, lb)
                    is None -> lb
                }
            }
}
