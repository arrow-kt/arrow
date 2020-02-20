package arrow

@Retention(AnnotationRetention.RUNTIME)
@Target(
  /** Class, interface or object, annotation class is also included */
  AnnotationTarget.CLASS,
  /** Annotation class only */
  AnnotationTarget.ANNOTATION_CLASS,
  /** Generic type parameter (unsupported yet) */
  AnnotationTarget.TYPE_PARAMETER,
  /** Property */
  AnnotationTarget.PROPERTY,
  /** Field, including property's backing field */
  AnnotationTarget.FIELD,
  /** Local variable */
  AnnotationTarget.LOCAL_VARIABLE,
  /** Value parameter of a function or a constructor */
  AnnotationTarget.VALUE_PARAMETER,
  /** Constructor only (primary or secondary) */
  AnnotationTarget.CONSTRUCTOR,
  /** Function (constructors are not included) */
  AnnotationTarget.FUNCTION,
  /** Property getter only */
  AnnotationTarget.PROPERTY_GETTER,
  /** Property setter only */
  AnnotationTarget.PROPERTY_SETTER,
  /** Type usage */
  AnnotationTarget.TYPE,
  /** File */
  AnnotationTarget.FILE,
  AnnotationTarget.TYPEALIAS
)
@MustBeDocumented
annotation class synthetic
