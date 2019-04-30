package org.neuroteamhex.s7camel

import com.github.s7connector.api.DaveArea
import org.apache.camel.Endpoint
import org.apache.camel.Exchange
import org.apache.camel.PollingConsumer
import org.apache.camel.Processor
import org.apache.camel.support.DefaultScheduledPollConsumer
import org.apache.camel.support.ScheduledPollConsumer
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.lang.IllegalStateException
import java.util.concurrent.ScheduledExecutorService

class S7BytePollConsumer(
        val executorService: ScheduledExecutorService,
        val endpoint: S7Endpoint,
        processor: Processor,
        var daveArea: DaveArea,
        var areaNumber: Int,
        var bytes: Int,
        var offset: Int = 0)
    : ScheduledPollConsumer(endpoint, processor) {

    override fun poll(): Int {
        log.info("Poll begin")

        var exchange = receive()

        log.trace("Polled {}", exchange)

        // if the result of the polled exchange has output we should create a new exchange and
        // use the output as input to the next processor
        if (exchange.hasOut()) {
            // lets create a new exchange
            val newExchange = getEndpoint().createExchange()
            newExchange.getIn().copyFrom(exchange.out)
            exchange = newExchange
        }
        processor.process(exchange)
        log.info("Poll end")

        return 1
    }

    fun receive(): Exchange {
        log.info("Receive begin")
        val exchange = endpoint.createExchange()
        val result = endpoint.connection?.read(daveArea, areaNumber, bytes, offset)
                ?: throw IllegalStateException("Connection is null")
        exchange.getIn().body = result
        log.info("Receive end")

        return exchange
    }

}