package org.funktionale.pairing

import org.testng.Assert
import org.testng.annotations.Test

class PairingTest {
    @Test fun testPaired() {
        val sum2ints = { x: Int, y: Int -> x + y }

        val paired = sum2ints.paired()
        val unpaired = paired.unpaired()

        Assert.assertEquals(sum2ints(5, 9), paired(5 to 9))
        Assert.assertEquals(paired(5 to 9), unpaired(5, 9))
    }
}