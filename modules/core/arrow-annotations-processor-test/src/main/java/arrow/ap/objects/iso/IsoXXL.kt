package arrow.ap.objects.iso

import arrow.optics.OpticsTarget
import arrow.optics.optics

@optics([OpticsTarget.ISO])
data class IsoXXL(
        val field1: String,
        val field2: String,
        val field3: String,
        val field4: String,
        val field5: String,
        val field6: String,
        val field7: String,
        val field8: String,
        val field9: String,
        val field10: String,
        val field11: String
)