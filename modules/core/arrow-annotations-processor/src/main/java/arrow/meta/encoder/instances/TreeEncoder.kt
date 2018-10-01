package arrow.meta.encoder.instances

import shadow.core.Either
import arrow.meta.ast.PackageName
import arrow.meta.ast.Tree
import arrow.meta.ast.Type
import arrow.meta.encoder.EncodingError
import arrow.meta.encoder.MetaEncoder
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind

class TreeEncoder(
  private val packageEncoder: MetaEncoder<PackageName>,
  private val typeEncoder: MetaEncoder<Type>
) : MetaEncoder<Tree> {

  override fun encode(element: Element): Either<EncodingError, Tree> =
    when (element.kind) {
      ElementKind.PACKAGE -> packageEncoder.encode(element)
      ElementKind.CLASS -> typeEncoder.encode(element)
      ElementKind.INTERFACE -> typeEncoder.encode(element)
      else -> Either.Left(EncodingError.UnsupportedElementType("Not supported: ${element.kind}", element))
    }

}