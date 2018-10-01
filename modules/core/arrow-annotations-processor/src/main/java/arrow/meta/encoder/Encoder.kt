package arrow.meta.encoder

import shadow.core.Either
import arrow.meta.ast.Tree
import javax.lang.model.element.Element

sealed class EncodingError {
  data class UnsupportedElementType(val msg: String, val element: Element) : EncodingError()
}

interface MetaEncoder<out A: Tree> {
  fun encode(element: Element): Either<EncodingError, A>
}