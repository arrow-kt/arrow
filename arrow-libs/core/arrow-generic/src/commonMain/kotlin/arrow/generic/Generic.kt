package arrow.generic

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public sealed interface Generic<out A> {

  public object Null : Generic<Nothing> {
    override fun toString(): kotlin.String = "Generic.Null"
  }

  public inline class String(val value: kotlin.String) : Generic<kotlin.String>

  public inline class Char(val value: kotlin.Char) : Generic<kotlin.Char>

  public sealed interface Number<A : kotlin.Number> : Generic<A> {
    val value: A

    public inline class Byte(override val value: kotlin.Byte) : Number<kotlin.Byte>
    public inline class Short(override val value: kotlin.Short) : Number<kotlin.Short>
    public inline class Int(override val value: kotlin.Int) : Number<kotlin.Int>
    public inline class Long(override val value: kotlin.Long) : Number<kotlin.Long>
    public inline class Float(override val value: kotlin.Float) : Number<kotlin.Float>
    public inline class Double(override val value: kotlin.Double) : Number<kotlin.Double>

// These are not considered Number yet
//    public inline class UByte(val value: kotlin.UByte) : Number<kotlin.UByte>
//    public inline class UShort(val value: kotlin.UShort) : Number<kotlin.UShort>
//    public inline class UInt(val value: kotlin.UInt) : Number<kotlin.UInt>
//    public inline class ULong(val value: kotlin.ULong) : Number<kotlin.ULong>
  }

  public data class Inline<A>(
    override val info: Info,
    val element: Generic<*>
  ) : Object<A>

  public inline class Boolean(val value: kotlin.Boolean) : Generic<kotlin.Boolean>

  public sealed interface Object<A> : Generic<A> {
    public val info: Info
  }

  /**
   * Represents a product type.
   * A product type has [Info] & a fixed set of [fields]
   *
   * public data class Person(val name: String, val age: Int)
   *
   * Person =>
   *   Schema2.Product(
   *     ObjectInfo("Person"),
   *     listOf(
   *       Pair(FieldName("name"), Schema.string),
   *       Pair(FieldName("age"), Schema.int)
   *     )
   *   )
   */
  public data class Product<A>(
    override val info: Info,
    val fields: List<Pair<kotlin.String, Generic<*>>>,
  ) : Object<A> {
//    public fun required(): List<kotlin.String> =
//      fields.mapNotNull { (f, s) -> if (!s.isOptional()) f else null }

    public companion object {
      public val Empty = Product<Unit>(Info.unit, emptyList())
      operator fun invoke(info: Info, vararg fields: Pair<kotlin.String, Generic<*>>): Generic.Product<*> =
        Generic.Product<Any?>(info, fields.toList())
    }
  }

  /**
   * Represents a sum or coproduct type.
   * Has [Info], and NonEmptyList of subtypes schemas.
   * These subtype schemas contain all details about the subtypes, since they'll all have Schema2 is Schema2.Object.
   *
   * Either<A, B> =>
   *   Schema2.Coproduct(
   *     Schema2.ObjectInfo("Either", listOf("A", "B")),
   *     listOf(
   *       Schema2.Product("Either.Left", listOf("value", schemeA)),
   *       Schema2.Product("Either.Right", listOf("value", schemeA)),
   *     )
   *   )
   */
  public data class Coproduct<A>(
    override val info: Info,
    val productInfo: Info,
    val fields: List<Pair<kotlin.String, Generic<*>>>,
    val index: Int
  ) : Object<A>

  /**
   * Represents a value in an enum class
   * A product of [kotlin.Enum.name] and [kotlin.Enum.ordinal]
   */
  public data class EnumValue(val name: kotlin.String, val ordinal: Int)

  /**
   * Represents an Enum
   * Has [Info], and list of its values.
   *
   * enum class Test { A, B, C; }
   *
   * Test =>
   *   Schema2.Enum(
   *     Schema2.ObjectInfo("Test"),
   *     listOf(
   *       Schema2.EnumValue("A", 0),
   *       Schema2.EnumValue("B", 1),
   *       Schema2.EnumValue("C", 2)
   *     )
   *   )
   */
  public data class Enum<A>(
    override val info: Info,
    val values: List<EnumValue>,
    val index: Int
  ) : Object<A>

  /**
   * ObjectInfo contains the fullName of an object, and the type-param names.
   *
   * Either<A, B> => ObjectInfo("Either", listOf("A", "B"))
   */
  public data class Info(
    val fullName: kotlin.String,
    val typeParameterShortNames: List<kotlin.String> = emptyList()
  ) {
    public companion object {
      public val unit: Info = Info(fullName = "Unit")
    }
  }

  public companion object {

    @ExperimentalSerializationApi
    public inline fun <reified A> encode(
      value: A,
      ser: KSerializer<A> = serializer(),
      serializersModule: SerializersModule = EmptySerializersModule
    ): Generic<A> {
      val genericEncoder = GenericEncoder(serializersModule)
      ser.serialize(genericEncoder, value)
      return genericEncoder.result(ser) as Generic<A>
    }

    public fun <A : kotlin.Enum<A>> enum(
      name: kotlin.String,
      enumValues: Array<out A>,
      index: Int
    ): Generic<A> = Enum(
      Info(name),
      enumValues.map { EnumValue(it.name, it.ordinal) },
      index
    )

    public inline fun <reified A : kotlin.Enum<A>> enum(value: kotlin.Enum<A>): Generic<A> =
      enum(
        requireNotNull(A::class.qualifiedName) { "Qualified name on KClass should never be null." },
        enumValues(),
        enumValues<A>().indexOfFirst { it == value }
      )
  }
}
