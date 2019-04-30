package org.neuroteamhex.s7camel

import com.github.s7connector.api.S7Connector
import com.github.s7connector.api.SiemensPLCS

@FunctionalInterface
interface S7ConnectionFactory {
    fun getConnection(host: String,
                      port: Int = 102,
                      rack: Int = 0,
                      slot: Int = 2,
                      plcType: SiemensPLCS = SiemensPLCS.SNon200): S7Connector
}