package org.neuroteamhex.s7camel

import com.github.s7connector.api.S7Connector
import com.github.s7connector.api.SiemensPLCS
import com.github.s7connector.impl.nodave.S7Connection
import com.nhaarman.mockitokotlin2.any
import org.apache.camel.CamelContext
import org.apache.camel.spi.Registry
import org.apache.camel.support.SimpleRegistry
import org.apache.camel.test.junit4.CamelTestSupport
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.EndpointInject
import org.apache.camel.RoutesBuilder
import org.apache.camel.builder.RouteBuilder
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito


class S7EndpointTest : CamelTestSupport() {
    @EndpointInject(uri = "mock:result")
    protected var resultEndpoint: MockEndpoint? = null

    protected var mockConnection: S7Connector = Mockito.mock(S7Connector::class.java)

    override fun createRouteBuilder(): RoutesBuilder = object : RouteBuilder() {
        override fun configure() {
            from("s7:tcp://localhost/DB/10").to("mock:result")
        }
    }

    override fun createCamelRegistry(): Registry {
        val registry = SimpleRegistry()
        registry.bind("s7", S7Component(object : S7ConnectionFactory {
            override fun getConnection(host: String, port: Int, rack: Int, slot: Int, plcType: SiemensPLCS): S7Connector {
                return mockConnection
            }
        }))
        return registry
    }

    @Test
    fun simpleTest() {
        Mockito.`when`(mockConnection.read(any(), any(), any(), any())).thenReturn(ByteArray(1))
        resultEndpoint!!.expectedCount = 1
        resultEndpoint!!.assertIsSatisfied(5000)
    }


}