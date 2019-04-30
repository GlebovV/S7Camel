package com.github.s7connector.camel

import com.github.s7connector.api.DaveArea
import org.apache.camel.Exchange
import org.apache.camel.support.DefaultProducer

class S7ByteProducer(val endpoint: S7Endpoint,
                     var daveArea: DaveArea,
                     var areaNumber: Int,
                     var offset: Int) : DefaultProducer(endpoint) {
    override fun process(exchange: Exchange) {
        val bytesToWrite = exchange.getIn(ByteArray::class.java)
        endpoint.connection!!.write(daveArea, areaNumber, offset, bytesToWrite)
    }
}