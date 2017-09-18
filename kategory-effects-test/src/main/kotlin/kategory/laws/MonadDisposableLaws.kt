package kategory.laws

import kategory.*
import kategory.effects.*
import kategory.effects.data.internal.BindingCancellationException
import kategory.effects.typeclass.bindingEDisposable

object MonadDisposableLaws {
    inline fun <reified F> laws(AC: AsyncContext<F> = asyncContext(), M: MonadDisposable<F, Throwable> = monadDisposable<F, Throwable>(), EQ: Eq<HK<F, Int>>, EQ_EITHER: Eq<HK<F, Either<Throwable, Int>>>, EQERR: Eq<HK<F, Int>> = EQ): List<Law> =
            AsyncLaws.laws(AC, M, EQERR, EQ_EITHER, EQ) + listOf(
                    Law("Monad disposable: binding cancellation before flatMap", { asyncCancellationBefore(AC, M, EQ) }),
                    Law("Monad disposable: binding cancellation after flatMap", { asyncCancellationAfter(AC, M, EQ) })
            )

    inline fun <reified F> asyncCancellationBefore(AC: AsyncContext<F> = asyncContext(), M: MonadDisposable<F, Throwable> = monadDisposable<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(10, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = M.bindingEDisposable {
                    val a = runAsync(AC) { Thread.sleep(1000); num }.bind()
                    sideEffect.increment()
                    val b = runAsync(AC) { a + 1 }.bind()
                    val c = M.pure(b + 1).bind()
                    yields(c)
                }
                Try { Thread.sleep(500); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(M.raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
            })

    inline fun <reified F> asyncCancellationAfter(AC: AsyncContext<F> = asyncContext(), M: MonadDisposable<F, Throwable> = monadDisposable<F, Throwable>(), EQ: Eq<HK<F, Int>>): Unit =
            forFew(10, genIntSmall(), { num: Int ->
                val sideEffect = SideEffect()
                val (binding, dispose) = M.bindingEDisposable {
                    val a = runAsync(AC) { num }.bind()
                    val b = runAsync(AC) { Thread.sleep(1000); sideEffect.increment(); a + 1 }.bind()
                    yields(b)
                }
                Try { Thread.sleep(500); dispose() }.recover { throw it }
                binding.equalUnderTheLaw(M.raiseError(BindingCancellationException()), EQ) && sideEffect.counter == 0
            })
}