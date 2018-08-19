package arrow.meta.encoding

import arrow.meta.ast.Tree
import javax.lang.model.element.Element

sealed class EncodingError {
  data class UnsupportedElementType(val msg: String, val element: Element) : EncodingError()
}

sealed class EncodingResult<out A: Tree> {
  data class Success<out A: Tree>(val tree: A) : EncodingResult<A>()
  data class Failure(val error: EncodingError) : EncodingResult<Nothing>()

  fun <B> fold(onFailure: (EncodingError) -> B, onSuccess: (A) -> B): B =
    when (this) {
      is EncodingResult.Success -> onSuccess(tree)
      is EncodingResult.Failure -> onFailure(error)
    }

  fun <B: Tree> flatMap(f: (A) -> EncodingResult<B>): EncodingResult<B> =
    fold({ Failure(it) }, f)

  fun <B: Tree> map(f: (A) -> B): EncodingResult<B> =
    flatMap { Success(f(it)) }

  companion object {
    fun <A: Tree> just(tree: A): EncodingResult<A> = Success(tree)
    fun <A: Tree> raiseError(encodingError: EncodingError): EncodingResult<A> = Failure(encodingError)
  }
}

fun <A: Tree> EncodingResult<A>.handleWith(f: (EncodingError) -> EncodingResult<A>): EncodingResult<A> =
  fold({ f(it) }, { EncodingResult.Success(it) })

fun <A: Tree> EncodingResult<A>.handle(f: (EncodingError) -> A): EncodingResult<A> =
  fold({ EncodingResult.Success(f(it)) }, { EncodingResult.Success(it) })

interface MetaEncoder<out A: Tree> {
  fun encode(element: Element): EncodingResult<A>
}