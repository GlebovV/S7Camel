package org.neuroteamhex.s7camel

import com.github.s7connector.api.S7Serializable
import com.github.s7connector.api.S7Serializer
import com.github.s7connector.impl.serializer.S7SerializerImpl
import com.github.s7connector.impl.serializer.parser.BeanParser
import org.apache.camel.Converter
import org.apache.camel.Exchange
import java.util.*

@Converter
class S7Converters {
    companion object {
        private inline fun <reified T> S7Serializable.fromByteArray(buffer: BitSet): T =
                this.extract(T::class.java, buffer.toByteArray(), 0, 0)

        fun toS7Bean(beanClass: Class<Any>, buffer: ByteArray): Any =
                S7SerializerImpl.extractBytes(beanClass, buffer, 0)

        fun fromS7Bean(bean: Any): ByteArray {
            val result = BeanParser.parse(bean)
            val buffer = ByteArray(result.blockSize)
            S7SerializerImpl.insertBytes(bean, buffer, 0)
            return buffer
        }
    }
}