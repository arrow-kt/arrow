package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.core.None
import arrow.core.Some
import arrow.core.getOrElse
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.Token
import arrow.fx.coroutines.stream.R.Done
import arrow.fx.coroutines.stream.R.Out
import arrow.fx.coroutines.stream.Pull.Result
import arrow.fx.coroutines.stream.R.Interrupted

internal sealed class R<out O> {
  data class Done(val scope: Scope) : R<Nothing>()
  data class Out<O>(val head: Chunk<O>, val scope: Scope, val tail: Pull<O, Unit>) : R<O>()
  data class Interrupted<O>(val scopeId: Token, val err: Throwable?) : R<O>()
}

/** Left-folds the output of a stream. */
@Suppress("TAIL_RECURSION_IN_TRY_IS_NOT_SUPPORTED")
internal suspend fun <O, B> compile(
  stream: Pull<O, Unit>,
  scope: Scope,
  extendLastTopLevelScope: Boolean,
  init: B,
  g: (B, Chunk<O>) -> B
): B {
  var prevScope: Scope = scope
  var prevStream: Pull<O, Unit> = stream
  var prevInit: B = init

  while (true) {
    when (val res = compileLoop(prevScope, extendLastTopLevelScope, prevStream)) {
      null -> return@compile prevInit
      else -> {
        val (output, newScope, newTail) = res
        try {
          prevInit = g(prevInit, output)
          prevStream = newTail
          prevScope = newScope
        } catch (e: Throwable) {
          prevStream = newTail.asHandler(e)
          prevScope = newScope
        }
      }
    }
  }
}

/*
 * Interruption of the stream is tightly coupled between Pull and CompileScope
 * Reason for this is unlike interruption of `suspend` we need to find
 * recovery point where stream evaluation has to continue in Stream algebra
 *
 * As such the `Token` is passed to Pull.Interrupted as glue between Pull that allows pass-along
 * information for Pull and scope to correctly compute recovery point after interruption was signalled via `Scope`.
 *
 * This token indicates scope of the computation where interruption actually happened.
 * This is used to precisely find most relevant interruption scope where interruption shall be resumed
 * for normal continuation of the stream evaluation.
 *
 * Interpreter uses this to find any parents of this scope that has to be interrupted, and guards the
 * interruption so it won't propagate to scope that shall not be anymore interrupted.
 */ // TODO rewrite to `while` is probably easier and nicer than `tailrec` anyway
@PublishedApi
internal suspend fun <O> compileLoop(
  scope: Scope,
  extendLastTopLevelScope: Boolean,
  stream: Pull<O, Unit>
): Triple<Chunk<O>, Scope, Pull<O, Unit>>? =
  when (val res = go(scope, extendLastTopLevelScope, null, stream)) {
    is R.Out -> Triple(res.head as Chunk<O>, res.scope, res.tail as Pull<O, Unit>)
    is R.Done -> null
    is Interrupted<*> -> when (res.err) {
      null -> null
      else -> throw res.err
    }
  }

/**
 * Inject interruption to the tail used in flatMap.
 * Assures that close of the scope is invoked if at the flatMap tail, otherwise switches evaluation to `interrupted` path
 *
 * @param stream tail to inject interruption into
 * @param interruptedScope scopeId to interrupt
 * @param interruptedError Additional finalizer errors
 * @tparam F
 * @tparam O
 * @return
 */
internal fun <O> interruptBoundary(
  stream: Pull<O, Unit>,
  interruptedScope: Token,
  interruptedError: Throwable?
): Pull<O, Unit> =
  when (val view = ViewL(stream)) {
    is Result.Pure ->
      Result.Interrupted(interruptedScope, interruptedError)
    is Result.Fail ->
      Result.Fail(Platform.composeErrors(view.error, interruptedError))
    is Result.Interrupted<*> -> view
    is Pull.EvalView<*, *, *> -> {
      val view = view as Pull.EvalView<O, Unit, Unit>
      when (val close = view.step) {
        is Pull.Eval.CloseScope -> Pull.Eval.CloseScope(
          close.scopeId,
          Pair(interruptedScope, interruptedError),
          ExitCase.Cancelled
        ).transformWith(view::next)
        else ->
          // all other cases insert interruption cause
          view.next(Result.Interrupted(interruptedScope, interruptedError))
      }
    }
    else -> throw RuntimeException("Exhaustive in interruptBoundary: $view")
  }

internal suspend inline fun interruptGuard(scope: Scope): Result<Any?>? =
  when (val isInterrupted = scope.isInterrupted()) {
    None -> null
    is Some -> when (val eith = isInterrupted.t) {
      is Either.Left -> Result.Fail(eith.a)
      is Either.Right -> Result.Interrupted(eith.b, null)
    }
  }

// Make actually stack-safe
internal tailrec suspend fun go(
  scope: Scope,
  extendLastTopLevelScope: Boolean,
  extendedTopLevelScope: Scope?,
  stream: Pull<Any?, Any?>
): R<Any?> =
  when (val view = ViewL(stream)) {
    is Result.Pure -> Done(scope)
    is Result.Fail -> throw view.error
    is Result.Interrupted<*> -> interruptedIssue(view)
    is Pull.EvalView<*, *, *> -> {
      view as Pull.EvalView<Any?, Any?, Unit>

      when (val res = view.step) {
        is Pull.Eval.Output<*> -> {
          when (val r = interruptGuard(scope)) {
            null -> R.Out(res.values, scope, view.next(Result.unit))
            else -> go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(r))
          }
        }
        is Pull.Eval.Step<*> -> {
          // if scope was specified in step, try to find it, otherwise use the current scope.
          when (val stepScope = res.token?.let { scopeId -> scope.findStepScope(scopeId).orNull() } ?: scope) {
            null -> throw RuntimeException(
              """|Scope lookup failure!
                   |
                   |This is typically caused by uncons-ing from two or more streams in the same Pull.
                   |To do this safely, use `s.pull.stepLeg` instead of `s.pull.uncons` or a variant
                   |thereof. See the implementation of `Stream#zipWith_` for an example.
                   |
                   |Scope id: ${scope.id}
                   |Step: $res""".trimMargin()
            )
            else -> when (val either =
              Either.catch { go(stepScope, extendLastTopLevelScope, extendedTopLevelScope, res.pull) }) {
              is Either.Right -> when (val r = either.b) {
                is Done -> when (val rr = interruptGuard(r.scope)) {
                  null -> go(r.scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.Pure(null)))
                  else -> go(r.scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(rr))
                }
                is Out -> {
                  // if we originally swapped scopes we want to return the original
                  // scope back to the go as that is the scope that is expected to be here.
                  val nextScope = if (res.token == null) r.scope else scope
                  val result = Result.Pure(Triple(r.head, r.scope.id, r.tail))
                  when (val rr = interruptGuard(r.scope)) {
                    null -> go(nextScope, extendLastTopLevelScope, extendedTopLevelScope, view.next(result))
                    else -> go(r.scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(rr))
                  }
                }
                is Interrupted ->
                  go(
                    scope,
                    extendLastTopLevelScope,
                    extendedTopLevelScope,
                    view.next(Result.Interrupted(r.scopeId, r.err))
                  )
              }
              is Either.Left ->
                go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.Fail(either.a)))
            }
          }
        }
        is Pull.Eval.Effect<*> -> {
          when (val r = scope.interruptibleEval(res.value)) {
            is Either.Left -> when (val r2 = r.a) {
              is Either.Left -> go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.Fail(r2.a)))
              is Either.Right -> go(
                scope,
                extendLastTopLevelScope,
                extendedTopLevelScope,
                view.next(Result.Interrupted(r2.b, null))
              )
            }
            is Either.Right -> go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.Pure(r.b)))
          }
        }

        is Pull.Eval.Acquire<*> -> {
          when (val r = interruptGuard(scope)) {
            null -> {
              val release = res.release as suspend (Any?, ExitCase) -> Unit
              val r = scope.acquireResource(res.resource, release)
              go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.fromEither(r)))
            }
            else -> go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(r))
          }
        }
        is Pull.Eval.GetScope -> go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.Pure(scope)))

        /**
         * OpenScope:
         *  - Check for interruption
         *  - If we're opening a new top-level scope (aka, direct descendant of root), close the current extended top-level scope if it is defined.
         *  - Open new Scope, and recurse
         */
        is Pull.Eval.OpenScope -> {
          when (val r = interruptGuard(scope)) {
            null -> {
              val closedExtendedScope = if (scope.parent == null) {
                when (extendedTopLevelScope) {
                  null -> false
                  else -> extendedTopLevelScope.close(ExitCase.Completed).fold({ throw it }, { true })
                }
              } else false

              val newExtendedScope = if (closedExtendedScope) null else extendedTopLevelScope
              when (val res2 = Either.catch { scope.open(res.isInterruptible) }) {
                is Either.Left -> go(scope, extendLastTopLevelScope, newExtendedScope, view.next(Result.Fail(res2.a)))
                is Either.Right -> go(res2.b, extendLastTopLevelScope, newExtendedScope, view.next(Result.Pure(res2.b.id)))
              }
            }
            else -> go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(r))
          }
        }

        is Pull.Eval.CloseScope -> {
          val toClose: Scope? = scope
            .findSelfOrAncestor(res.scopeId) ?: scope.findSelfOrChild(res.scopeId).orNull()

          when (toClose) {
            null -> {
              // scope already closed, continue with current scope
              val r = when (val interruptedScope = res.interruptedScope) {
                null -> Result.unit
                else -> Result.Interrupted(interruptedScope.first, interruptedScope.second)
              }
              go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(r))
            }
            else -> {
              if (toClose.parent == null) {
                // Impossible - don't close root scope as a result of a `CloseScope` call
                go(scope, extendLastTopLevelScope, extendedTopLevelScope, view.next(Result.unit))
              } else if (extendLastTopLevelScope && toClose.parent.parent == null) {
                // Request to close the current top-level scope - if we're supposed to extend
                // it instead, leave the scope open and pass it to the continuation
                extendedTopLevelScope?.close(ExitCase.Completed)
                  ?.fold({ throw it }, { Unit })
                val ancestor = toClose.openAncestor()
                go(ancestor, extendLastTopLevelScope, toClose, view.next(Result.unit))
              } else {
                val r = toClose.close(res.exitCase)
                val ancestor = toClose.openAncestor()
                val res2 = when (res.interruptedScope) {
                  null -> Result.fromEither(r)
                  else -> {
                    val (interruptedScopeId, err) = res.interruptedScope
                    val err1 = Platform.composeErrors(err, r.swap().getOrElse { null })
                    if (ancestor.findSelfOrAncestor(interruptedScopeId) != null) {
                      // we still have scopes to interrupt, lets build interrupted tail
                      Result.Interrupted(interruptedScopeId, err1)
                    } else {
                      // interrupts scope was already interrupted, resume operation
                      when (err1) {
                        null -> Result.unit
                        else -> Result.Fail(err1)
                      }
                    }
                  }
                }
                go(ancestor, extendLastTopLevelScope, extendedTopLevelScope, view.next(res2))
              }
            }
          }
        }
      }
    }
    else -> throw RuntimeException("Exhaustive in go: $view")
  }

private fun interruptedIssue(stream: Result.Interrupted<*>): Interrupted<Any?> {
  return when (val ctx = stream.context) {
    is Token -> Interrupted(ctx, stream.deferredError)
    else -> throw RuntimeException("Unexpected interruption context: $stream (compileLoop)")
  }
}
