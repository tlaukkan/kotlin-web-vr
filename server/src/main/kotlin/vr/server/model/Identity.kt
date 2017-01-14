package vr.server.model

import vr.network.model.Node
import java.net.URI
import java.util.*

data class Identity(var id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    var uri: URI = URI.create("http://0.0.0.0/nodes/$id"),
                    var name: String = "",
                    var avatar: Node = Node(),
                    var properties: Map<String, Any> = TreeMap())