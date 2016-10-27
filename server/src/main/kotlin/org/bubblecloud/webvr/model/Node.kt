package org.bubblecloud.webvr.model

import java.net.URI
import java.util.*

data class Node(var id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                var uri: URI = URI.create("http://0.0.0.0/nodes/$id"),
                var parentUri: URI? = null,
                var removed: Boolean = false,
                var properties: Map<String, Any> = TreeMap()) {
}