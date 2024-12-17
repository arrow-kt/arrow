package arrow.platform.test

public annotation class NonFlakyOnThisPlatform

public expect annotation class FlakyOnJvm()
public expect annotation class FlakyOnJs()
public expect annotation class FlakyOnNative()
