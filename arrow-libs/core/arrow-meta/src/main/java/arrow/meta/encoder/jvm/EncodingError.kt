package arrow.meta.encoder.jvm

import javax.lang.model.element.Element

public sealed class EncodingError {
  public data class UnsupportedElementType(val msg: String, val element: Element) : EncodingError()
}
