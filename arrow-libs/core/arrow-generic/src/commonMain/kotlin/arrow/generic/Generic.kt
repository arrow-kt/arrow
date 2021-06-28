package arrow.generic

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public sealed interface Generic<A> {

  /**
   * Returns an optional version of this schema, with `isOptional` set to true.
   */
  public fun asNullable(): Generic<A?> = Nullable(this)

  /**
   * Returns an array version of this schema, with the schema type wrapped in [SchemaType.List].
   * Sets `isOptional` to true as the collection might be empty.
   */
  public fun asArray(): Generic<Array<A>> = List(this)

  /** Returns a collection version of this schema, with the schema type wrapped in [SchemaType.List].
   * Sets `isOptional` to true as the collection might be empty.
   */
  public fun asList(): Generic<kotlin.collections.List<A>> = List(this)

  /**
   * Nullable & Collections are considered nullable. Collections because they can be empty.
   **/
  public fun isOptional(): kotlin.Boolean =
    this is Nullable || this is List

  public fun isNotOptional(): kotlin.Boolean = !isOptional()

  public inline class String(val value: kotlin.String) : Generic<kotlin.String>

  public inline class Char(val value: kotlin.Char) : Generic<kotlin.Char>

  public sealed interface Number<A : kotlin.Number> : Generic<A> {
    val value: A

    public inline class Byte(override val value: kotlin.Byte) : Number<kotlin.Byte>

//    public inline class UByte(val value: kotlin.UByte) : Number<kotlin.UByte>

    public inline class Short(override val value: kotlin.Short) : Number<kotlin.Short>

//    public inline class UShort(val value: kotlin.UShort) : Number<kotlin.UShort>

    public inline class Int(override val value: kotlin.Int) : Number<kotlin.Int>

//    public inline class UInt(val value: kotlin.UInt) : Number<kotlin.UInt>

    public inline class Long(override val value: kotlin.Long) : Number<kotlin.Long>

//    public inline class ULong(val value: kotlin.ULong) : Number<kotlin.ULong>

    public inline class Float(override val value: kotlin.Float) : Number<kotlin.Float>

    public inline class Double(override val value: kotlin.Double) : Number<kotlin.Double>
  }

  public inline class Boolean(val value: kotlin.Boolean) : Generic<kotlin.Boolean>

  public data class List<A>(
    val element: Generic<*>,
  ) : Generic<A> {
    override fun toString(): kotlin.String = "[$element]"
  }

  public data class Nullable<A>(
    val element: Generic<*>,
  ) : Generic<A> {
    override fun toString(): kotlin.String = "$element?"
  }

  public sealed interface Object<A> : Generic<A> {
    public val objectInfo: ObjectInfo
  }

  public data class Either<A>(
    val left: Generic<*>,
    val right: Generic<*>,
  ) : Object<A> {
    override val objectInfo: ObjectInfo =
      ObjectInfo("arrow.core.Either", listOf(left.toString(), right.toString()))

    override fun toString(): kotlin.String = "either<$left, $right>"
  }

  /**
   * Represents an key-value set or Map<K, V>.
   * A Map contains N-fields of the same type [valueGeneric] which are held by a corresponding key [keyGeneric].
   *
   * Map<Int, DateTime> =>
   *   Schema2.Map(
   *     Schema2.ObjectInfo("Map", listOf("Int", "DateTime")),
   *     Schema.int,
   *     Schema.dateTime
   *   )
   */
  public data class Map<A>(
    override val objectInfo: ObjectInfo,
    val keyGeneric: Generic<*>,
    val valueGeneric: Generic<*>,
  ) : Object<A> {
    override fun toString(): kotlin.String = "$keyGeneric->$valueGeneric"
  }

  /**
   * Represents an open-product or Map<String, V>.
   * An open product contains N-fields, which are held by [String] keys.
   *
   * Map<String, Int> =>
   *   Schema2.OpenProduct(
   *     Schema2.ObjectInfo("Map", listOf("String", "Int")),
   *     Schema.int
   *   )
   */
  public data class OpenProduct<A>(
    override val objectInfo: ObjectInfo,
    val valueGeneric: Generic<*>,
  ) : Object<A> {
    override fun toString(): kotlin.String = "String->$valueGeneric"
  }

  /**
   * Represents a product type.
   * A product type has [ObjectInfo] & a fixed set of [fields]
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
    override val objectInfo: ObjectInfo,
    val fields: kotlin.collections.List<Pair<kotlin.String, Generic<*>>>,
  ) : Object<A> {
    public fun required(): kotlin.collections.List<kotlin.String> =
      fields.mapNotNull { (f, s) -> if (!s.isOptional()) f else null }

    override fun toString(): kotlin.String =
      "${objectInfo.fullName}(${fields.joinToString(",") { (f, s) -> "$f=$s" }})"

    public companion object {
      public val Empty = Product<Unit>(ObjectInfo.unit, emptyList())
      operator fun invoke(objectInfo: ObjectInfo, vararg fields: Pair<kotlin.String, Generic<*>>): Generic.Product<*> =
        Generic.Product<Any?>(objectInfo, fields.toList())
    }
  }

  /**
   * Represents a value in an enum class
   * A product of [kotlin.Enum.name] and [kotlin.Enum.ordinal]
   */
  public data class EnumValue(val name: kotlin.String, val ordinal: Int)

  /**
   * Represents an Enum
   * Has [ObjectInfo], and list of its values.
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
    override val objectInfo: ObjectInfo,
    val values: kotlin.collections.List<EnumValue>,
    val index: Int
  ) : Object<A> {
    override fun toString(): kotlin.String =
      "${objectInfo.fullName}[${values.joinToString(separator = " | ")}]"
  }

  /**
   * Represents a sum or coproduct type.
   * Has [ObjectInfo], and NonEmptyList of subtypes schemas.
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
    override val objectInfo: ObjectInfo,
//    val schemas: arrow.core.NonEmptyList<Schema<*>>,
    val generics: kotlin.collections.List<Generic<*>>,
  ) : Object<A> {
    override fun toString(): kotlin.String =
      "${objectInfo.fullName}[${generics.joinToString(separator = " | ")}]"
  }

  /**
   * ObjectInfo contains the fullName of an object, and the type-param names.
   *
   * Either<A, B> => ObjectInfo("Either", listOf("A", "B"))
   */
  public data class ObjectInfo(
    val fullName: kotlin.String,
    val typeParameterShortNames: kotlin.collections.List<kotlin.String> = emptyList()
  ) {
    public companion object {
      public val unit: ObjectInfo = ObjectInfo(fullName = "Unit")
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
      return genericEncoder.result(ser.descriptor.serialName) as Generic<A>
    }

    public fun <A : kotlin.Enum<A>> enum(
      name: kotlin.String,
      enumValues: Array<out A>,
      index: Int
    ): Generic<A> = Enum(
      Generic.ObjectInfo(name),
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
