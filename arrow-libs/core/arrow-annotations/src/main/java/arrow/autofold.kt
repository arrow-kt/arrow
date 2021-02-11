package arrow

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Deprecated("kapt generation of autofold will no longer be supported in the future. Prefer using when directly in code, or manually implement fold")
annotation class autofold
