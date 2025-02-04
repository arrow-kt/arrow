package arrow.raise.ktor.server

import arrow.core.NonEmptyList
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.charsets.encodeToByteArray

public class ValidatedContent(
  public val text: String,
  public override val status: HttpStatusCode
) : OutgoingContent.ByteArrayContent() {
  override val contentType: ContentType = ContentType.Text.Plain
  private val bytes: ByteArray =
    (contentType.charset() ?: Charsets.UTF_8).newEncoder().encodeToByteArray(text)

  override val contentLength: Long
    get() = bytes.size.toLong()

  override fun bytes(): ByteArray = bytes
  override fun toString(): String = "ValidatedContent[$contentType] \"${text.take(30)}\""
}

public data class ValidationContent(val content: NonEmptyList<TextContent>) : OutgoingContent.ByteArrayContent() {
  override val contentType: ContentType = ContentType.Text.Plain
  private val bytes: ByteArray =
    (contentType.charset() ?: Charsets.UTF_8).newEncoder().encodeToByteArray(content.joinToString { it.text })

  override val contentLength: Long
    get() = bytes.size.toLong()

  override fun bytes(): ByteArray = bytes
  override fun toString(): String = "ValidatedContent[$contentType]"
}
