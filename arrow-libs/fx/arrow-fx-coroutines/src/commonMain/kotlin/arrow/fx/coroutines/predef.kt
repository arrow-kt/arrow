package arrow.fx.coroutines

/**
 * Gets current system time in milliseconds since certain moment in the past,
 * only delta between two subsequent calls makes sense.
 *
 * For the JVM target this delegates to `java.lang.System.currentTimeMillis()`
 * For the native targets this delegates to `kotlin.system.getTimeMillis`
 * For Javascript it relies on `new Date().getTime()`
 */
public expect fun timeInMillis(): Long
