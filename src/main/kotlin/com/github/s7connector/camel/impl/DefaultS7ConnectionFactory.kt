package org.neuroteamhex.s7camel.impl

import com.github.s7connector.api.S7Connector
import com.github.s7connector.api.SiemensPLCS
import com.github.s7connector.api.factory.S7ConnectorFactory
import org.neuroteamhex.s7camel.S7ConnectionFactory

class DefaultS7ConnectionFactory : S7ConnectionFactory {
    override fun getConnection(host: String, port: Int, rack: Int, slot: Int, plcType: SiemensPLCS): S7Connector =
            S7ConnectorFactory.buildTCPConnector().withHost(host).withPort(port).withRack(rack).withSlot(slot).build()
}