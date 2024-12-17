package arrow.platform.test

public actual typealias FlakyOnJvm = NonFlakyOnThisPlatform
public actual typealias FlakyOnJs = kotlin.test.Ignore
public actual typealias FlakyOnNative = NonFlakyOnThisPlatform
