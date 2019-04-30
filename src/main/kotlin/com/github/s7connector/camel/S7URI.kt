package com.github.s7connector.camel

import com.github.s7connector.api.DaveArea
import java.net.URI

class S7URI(val uri: URI) {
    val protocol: String = uri.scheme

    val daveArea: DaveArea = DaveArea.valueOf(uri.path.split("/")[1])

    val areaNumber: Int = uri.path.split("/").getOrNull(2)?.toInt() ?: 0

    override fun toString(): String = uri.toString()
}