package arrow.ap.tests

import arrow.optics.OptikalProcessor

class OptionalTest: APTest() {

    init {

        testProcessor(AnnotationProcessor(
                name = "@optionals test",
                sourceFile = "Optional.java",
                destFile = "Optional.kt",
                processor = OptikalProcessor()
        ))

        testProcessor(AnnotationProcessor(
                name = "@optionals sealed test",
                sourceFile = "OptionalSealed.java",
                processor = OptikalProcessor(),
                errorMessage = "It can only be used on data class."
        ))

    }

}