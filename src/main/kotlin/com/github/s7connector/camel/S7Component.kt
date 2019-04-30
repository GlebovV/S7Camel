package com.github.s7connector.camel

import com.github.s7connector.camel.impl.DefaultS7ConnectionFactory
import org.apache.camel.Endpoint
import org.apache.camel.support.DefaultComponent
import org.apache.camel.util.URISupport

import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

class S7Component(
        private val connectorFactory: S7ConnectionFactory = DefaultS7ConnectionFactory(),
        private val executorService: ScheduledExecutorService = ScheduledThreadPoolExecutor(1)
) : DefaultComponent() {
    private val logger = LoggerFactory.getLogger(S7Component::class.java)
    override fun createEndpoint(uri: String,
                                remaining: String,
                                parameters: MutableMap<String, Any>?): Endpoint {
        logger.debug("Create endpoint with uri: $uri")
        var addressUri = uri
        if (addressUri.startsWith("s7://")) {
            addressUri = addressUri.removePrefix("s7://")
        } else {
            addressUri = remaining
        }
        return S7Endpoint(this, connectorFactory, executorService, S7URI(URISupport.createRemainingURI(URI(addressUri), parameters)));
    }
}