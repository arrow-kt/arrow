package arrow.raise.ktor.server.request

public sealed interface RequestComponent

public data object ReceiveBody : RequestComponent

public sealed interface Parameter : RequestComponent {
  public val name: String

  public data class Form(override val name: String) : Parameter
  public data class Path(override val name: String) : Parameter
  public data class Query(override val name: String) : Parameter
}
