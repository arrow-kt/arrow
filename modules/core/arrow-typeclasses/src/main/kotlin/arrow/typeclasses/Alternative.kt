package arrow.typeclasses

inline operator fun <F, A> Alternative<F>.invoke(ff: Alternative<F>.() -> A) =
        run(ff)

interface Alternative<F> : Applicative<F>, MonoidK<F>
