package arrow.platform.test

public actual typealias FlakyOnJvm = org.junit.jupiter.api.Disabled
public actual typealias FlakyOnJs = NonFlakyOnThisPlatform
public actual typealias FlakyOnNative = NonFlakyOnThisPlatform
