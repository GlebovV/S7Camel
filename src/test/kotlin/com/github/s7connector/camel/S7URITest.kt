package org.neuroteamhex.s7camel

import com.github.s7connector.api.DaveArea
import org.junit.Assert
import org.junit.Test
import java.net.URI

class S7URITest {
    val sampleUri = S7URI(URI("tcp://localhost:1024/DB/100?bytes=4&offset=10"))

    @Test
    fun daveAreaTest() = Assert.assertEquals(DaveArea.DB, sampleUri.daveArea)

    @Test
    fun addressTest() = Assert.assertEquals(100, sampleUri.areaNumber)

    @Test
    fun protocolTest() = Assert.assertEquals("tcp", sampleUri.protocol)
}