package arrow.free

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.*
import arrow.core.FunctionK

inline fun <F, G, A> FreeApplicativeOf<F, A>.foldMapK(f: FunctionK<F, G>, GA: Applicative<G>): Kind<G, A> =
  (this as FreeApplicative<F, A>).foldMap(f, GA)

inline fun <F, A> FreeApplicativeOf<F, A>.foldK(FA: Applicative<F>): Kind<F, A> = (this as FreeApplicative<F, A>).fold(FA)

/**
 * See [https://github.com/edmundnoble/cats/blob/6454b4f8b7c5cefd15d8198fa7d52e46e2f45fea/docs/src/main/tut/datatypes/freeapplicative.md]
 */
@higherkind
sealed class FreeApplicative<F, out A> : FreeApplicativeOf<F, A> {

  companion object {
    fun <F, A> just(a: A): FreeApplicative<F, A> = Pure(a)

    fun <F, P, A> apPipe(
      fp: FreeApplicativeOf<F, P>,
      fn: FreeApplicativeOf<F, (P) -> A>
    ): FreeApplicative<F, A> = Ap(fn.fix(), fp.fix())

    fun <F, A> liftF(fa: Kind<F, A>): FreeApplicative<F, A> = Lift(fa)

    internal fun <F, G> functionKF(f: FunctionK<F, G>): FunctionK<F, FreeApplicativePartialOf<G>> =
      object : FunctionK<F, FreeApplicativePartialOf<G>> {
        override fun <A> invoke(fa: Kind<F, A>): FreeApplicative<G, A> =
          liftF(f(fa))

      }

    internal fun <F> applicativeF(): Applicative<FreeApplicativePartialOf<F>> = object : Applicative<FreeApplicativePartialOf<F>> {
      override fun <A> just(a: A): FreeApplicative<F, A> =
        Companion.just(a)

      override fun <A, B> FreeApplicativeOf<F, A>.apPipe(
        ff: FreeApplicativeOf<F, (A) -> B>
      ): FreeApplicative<F, B> =
        Companion.apPipe(fix(), ff.fix())
    }
  }

  fun <B> apPipe(ap: FreeApplicativeOf<F, (A) -> B>): FreeApplicative<F, B> {
    val fixed = ap.fix()
    return when (fixed) {
      is Pure -> map(fixed.value)
      else -> Ap(fixed, this)
    }
  }

  fun <C> map(f: (A) -> C): FreeApplicative<F, C> =
    when (this) {
      is Pure -> Pure(f(value))
      else -> Ap(Pure(f), this)
    }

  fun fold(FA: Applicative<F>): Kind<F, A> = foldMap(FunctionK.id(), FA)

  fun <G> compile(f: FunctionK<F, G>): FreeApplicative<G, A> = foldMap(functionKF(f), applicativeF()).fix()

  fun <G> flatCompile(f: FunctionK<F, FreeApplicativePartialOf<G>>, GFA: Applicative<FreeApplicativePartialOf<G>>): FreeApplicative<G, A> =
    foldMap(f, GFA).fix()

  inline fun <M> analyze(MM: Monoid<M>, f: FunctionK<F, ConstPartialOf<M>>): M =
    foldMap(object : FunctionK<F, ConstPartialOf<M>> {
      override fun <A> invoke(fa: Kind<F, A>): Const<M, A> = f(fa).fix()
    }, Const.applicative(MM)).value()

  fun monad(ap: Applicative<FreePartialOf<F>>): Free<F, A> =
    foldMap(Free.functionKF(), Free.applicativeF(ap)).fix()

  // Beware: smart code
  @Suppress("UNCHECKED_CAST")
  fun <G> foldMap(f: FunctionK<F, G>, GA: Applicative<G>): Kind<G, A> {
    var argsF: List<FreeApplicative<F, Any?>> = mutableListOf(this)
    var argsFLength: Int = 1

    var fns: List<CurriedFunction<G, Any?, Any?>> = mutableListOf()
    var fnsLength: Int = 0

    tailrec fun loop(): Kind<G, Any?> {
      var argF: FreeApplicative<F, Any?> = argsF.first()
      argsF = argsF.drop(1)
      argsFLength -= 1

      return if (argF is Ap<F, *, *>) {
        val lengthInitial = argsFLength

        do {
          val ap = argF as Ap<F, Any?, Any?>
          argsF = listOf(ap.fp) + argsF
          argsFLength += 1
          argF = ap.fn
        } while (argF is Ap<F, *, *>)

        val argc = argsFLength - lengthInitial
        fns = listOf(CurriedFunction(foldArg(argF as FreeApplicative<F, (Any?) -> Any?>, f, GA), argc)) + fns
        fnsLength += 1

        loop()
      } else {
        val argT: Kind<G, Any?> = foldArg(argF, f, GA)

        if (fns.isNotEmpty()) {

          var fn = fns.first()
          fns = fns.drop(1)
          fnsLength -= 1

          var res = GA.run { argT.apPipe(fn.gab) }

          if (fn.remaining > 1) {
            fns = listOf(CurriedFunction(res as Kind<G, (Any?) -> Any?>, fn.remaining - 1)) + fns
            fnsLength += 1
            loop()

          } else {
            if (fnsLength > 0) {

              tailrec fun innerLoop(): Unit {
                fn = fns.first()
                fns = fns.drop(1)
                fnsLength -= 1
                res = GA.run { res.apPipe(fn.gab) }

                if (fn.remaining > 1) {
                  fns = listOf(CurriedFunction(res as Kind<G, (Any?) -> Any?>, fn.remaining - 1)) + fns
                  fnsLength += 1
                }

                if (fn.remaining == 1 && fnsLength > 0) {
                  innerLoop()
                }
              }

              innerLoop()
            }

            if (fnsLength == 0) {
              res
            } else {
              loop()
            }
          }
        } else {
          argT
        }
      }
    }

    return loop() as Kind<G, A>
  }

  internal data class CurriedFunction<out G, in A, out B>(val gab: Kind<G, (A) -> B>, val remaining: Int)

  internal data class Pure<S, out A>(val value: A) : FreeApplicative<S, A>()

  internal data class Lift<S, out A>(val fa: Kind<S, A>) : FreeApplicative<S, A>()

  internal data class Ap<S, P, out A>(val fn: FreeApplicative<S, (P) -> A>, val fp: FreeApplicative<S, P>) : FreeApplicative<S, A>()

  override fun toString(): String = "FreeApplicative(...)"
}

private fun <F, G, A> foldArg(node: FreeApplicative<F, A>, f: FunctionK<F, G>, GA: Applicative<G>): Kind<G, A> =
  when (node) {
    is FreeApplicative.Pure<F, A> -> GA.just(node.value)
    else -> {
      val lift = node as FreeApplicative.Lift<F, A>
      f(lift.fa)
    }
  }

@Suppress("NOTHING_TO_INLINE")
inline infix fun <F, A, B> FreeApplicativeOf<F, (A) -> B>.ap(
  fa: FreeApplicativeOf<F, A>
): FreeApplicative<F, B> = fa.fix().apPipe(this)

fun <S, A> A.freeAp(): FreeApplicative<S, A> = FreeApplicative.just(this)
