package arrow.ap.objects.optional

import arrow.core.Option
import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.OPTIONAL])
public data class OptionalWithoutCompanion(
  public val field: String,
  public val nullable: String?,
  public val option: Option<String>
)
