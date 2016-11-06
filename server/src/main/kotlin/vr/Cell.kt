package vr

import logger
import vr.network.model.Node
import java.util.*

/**
 * Created by tlaukkan on 7/9/2016.
 */
class Cell(val cellUri: String, val remoteCell: Boolean = false) {
    private val log = logger()

    private val nodes: MutableMap<String, Node> = HashMap()

    @Synchronized fun addNode(node: Node) : Boolean {

        if ("00000000-0000-0000-0000-000000000000".equals(node.id)) {
            log.warning("Node add failed. Node with empty ID of type ${node.javaClass.simpleName}")
            return false
        }
        if (nodes.containsKey(node.url)) {
            log.warning("Node add failed. Node already exists ${node.id}")
            return false
        }

        node.url = "$cellUri/${node.id}"

        log.info("Added node: ${node.url} of type ${node.javaClass.simpleName}")

        nodes[node.url] = node
        return true
    }

    @Synchronized fun updateNode(node: Node) : Boolean {
        if (!nodes.containsKey(node.url)) {
            log.warning("Node update failed. Node does not exist ${node.id}")
            return false
        }
        nodes[node.url] = node

        log.info("Updated node: ${node.id} of type ${node.javaClass.simpleName}")

        return true
    }

    @Synchronized fun removeNode(url: String) : Boolean {
        if (!nodes.containsKey(url)) {
            return false
        }
        val node = nodes.get(url)

        if (node != null) {
            nodes.remove(url)
            log.info("Removed node: ${node.id} of type ${node.javaClass.simpleName}")
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

    @Synchronized fun getNodes() : List<Node>  {
        return ArrayList<Node>(nodes.values)
    }
}