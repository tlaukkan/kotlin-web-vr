package vr.server.model

import logger
import vr.network.model.DataVector3
import vr.network.model.Node
import vr.network.model.PrimitiveNode
import java.net.URL
import java.util.*

/**
 * Created by tlaukkan on 7/9/2016.
 */
class Cell(val url: String, var remote: Boolean = false, var neighbours: MutableMap<String, DataVector3> = TreeMap<String, DataVector3>()) {
    private val log = logger()

    val serverUrl: String
    val primeNode: PrimitiveNode = PrimitiveNode("box", "textures/alien.jpg")

    private val nodes: MutableMap<String, Node> = HashMap()

    init {
        var url = URL(url)
        val host = url.host
        val port = url.port
        val protocol: String
        if ("http".equals(url.protocol)) {
            protocol = "ws"
        } else {
            protocol = "wss"
        }
        serverUrl = "$protocol://$host:$port/"
        primeNode.volatile = true
        addNode(primeNode)
    }

    @Synchronized fun addNode(node: Node) : Boolean {

        if (node.url.endsWith("00000000-0000-0000-0000-000000000000") || node.url.length == 0) {
            node.url = "$url/${UUID.randomUUID()}"
        }
        if (nodes.containsKey(node.url)) {
            log.warning("Node add failed. Node already exists ${node.url}")
            return false
        }

        log.info("Added node: ${node.url} of type ${node.javaClass.simpleName}")

        nodes[node.url] = node
        return true
    }

    @Synchronized fun updateNode(node: Node) : Boolean {
        if (!nodes.containsKey(node.url)) {
            log.warning("Node update failed. Node does not exist ${node.url}")
            return false
        }
        nodes[node.url] = node

        //log.info("Updated node: ${node.url} of type ${node.javaClass.simpleName}")

        return true
    }

    @Synchronized fun removeNode(url: String) : Boolean {
        if (!nodes.containsKey(url)) {
            return false
        }
        val node = nodes.get(url)

        if (node != null) {
            nodes.remove(url)
            log.info("Removed node: ${node.url} of type ${node.javaClass.simpleName}")
        } else {
            log.warning("Remove failed. Node does not exist $url")
        }

        return true
    }

    @Synchronized fun hasNode(url: String) : Boolean {
        return nodes.containsKey(url)
    }

    @Synchronized fun getNode(url: String) : Node {
        if (!nodes.containsKey(url)) {
            throw IllegalArgumentException("No such node: " + url)
        }
        return nodes[url]!!
    }

    @Synchronized fun getNodes() : Array<Node>  {
        return ArrayList<Node>(nodes.values).toTypedArray()
    }
}