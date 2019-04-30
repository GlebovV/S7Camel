package org.neuroteamhex.s7camel

import com.github.s7connector.api.S7Connector
import com.github.s7connector.api.SiemensPLCS
import com.github.s7connector.api.factory.S7ConnectorFactory
import jdk.nashorn.internal.runtime.regexp.joni.Config.log
import org.apache.camel.*
import org.apache.camel.spi.*
import org.apache.camel.support.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicReference

@UriEndpoint(scheme = "s7", syntax = "tcp:host:port/daveArea/areaNumber", title = "S7Endpoint")
@UriParams
class S7Endpoint(
        val component: S7Component,
        val connectionFactory: S7ConnectionFactory,
        executorService: ScheduledExecutorService,
        @UriPath
        @Metadata(required = true)
        val s7uri: S7URI
) : ScheduledPollEndpoint(s7uri.toString(), component) {
    init {
        this.scheduledExecutorService = executorService
    }

    @UriParam(defaultValue = "0")
    var bytes: Int = 0

    @UriParam(defaultValue = "0")
    var offset: Int = 0

    @UriParam(defaultValue = "0")
    var rack: Int = 0

    @UriParam(defaultValue = "2")
    var slot: Int = 2

    @UriParam
    var plcType: SiemensPLCS = SiemensPLCS.SNon200

    override fun configureConsumer(consumer: Consumer) {
        super.configureConsumer(consumer)
        consumer as S7BytePollConsumer
        consumer.pollStrategy = object : DefaultPollingConsumerPollStrategy() {
            @Throws(Exception::class)
            override fun rollback(consumer: Consumer, endpoint: Endpoint, retryCounter: Int, e: Exception): Boolean {
                restartConnection()
                return true
            }
        }
    }


    @Throws(Exception::class)
    override fun createConsumer(processor: Processor): S7BytePollConsumer {
        val result = S7BytePollConsumer(scheduledExecutorService,
                this, processor, s7uri.daveArea, s7uri.areaNumber, bytes, offset)
        configureConsumer(result)
        return result
    }

    override fun createProducer(): Producer {
        log.info("CREATE PRODUCER (${s7uri.daveArea}, ${s7uri.areaNumber}, $bytes, ${s7uri.uri.port})")
        return S7ByteProducer(
                this,
                s7uri.daveArea,
                s7uri.areaNumber,
                offset
        )
    }

    override fun isSingleton(): Boolean = false

    override fun createEndpointUri(): String = s7uri.toString()

    @Volatile
    var connection: S7Connector? = null
        private set


    @Synchronized
    override fun doStart() {
        log.info("START")
        if (connection == null)
            connection = connectionFactory.getConnection(
                    s7uri.uri.host,
                    if (s7uri.uri.port > 0) s7uri.uri.port else 102,
                    rack,
                    slot,
                    plcType
            )
        super.doStart()
    }

    @Synchronized
    override fun doStop() {
        log.info("STOP")

        val c = connection
        if (c != null) {
            connection = null
            c.close()
        }
        super.doStop()
    }

    @Synchronized
    fun restartConnection() {
        log.info("RESTART")

        doStop()
        doStart()
    }
}