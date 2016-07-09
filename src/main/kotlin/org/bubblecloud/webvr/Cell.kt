package org.bubblecloud.webvr

import java.net.URI
import java.util.*

val CELL = Cell()

/**
 * Created by tlaukkan on 7/9/2016.
 */
class Cell {
    private val nodes: MutableMap<URI, Node> = HashMap()

    @Synchronized fun addNode(node: Node) : Boolean {
        if (nodes.containsKey(node.uri)) {
            return false
        }
        nodes[node.uri] = node
        return true
    }

    @Synchronized fun updateNode(node: Node) : Boolean {
        if (!nodes.containsKey(node.uri)) {
            return false
        }
        nodes[node.uri] = node
        return true
    }

    @Synchronized fun removeNode(uri: URI) : Boolean {
        if (!nodes.containsKey(uri)) {
            return false
        }
        nodes.remove(uri)
        return true
    }

    @Synchronized fun hasNode(uri: URI) : Boolean {
        return nodes.containsKey(uri)
    }

    @Synchronized fun getNode(uri: URI) : Node {
        if (!nodes.containsKey(uri)) {
            throw IllegalArgumentException("No such node: " + uri)
        }
        return nodes[uri]!!
    }

    @Synchronized fun getNodes() : List<Node>  {
        return ArrayList<Node>(nodes.values)
    }
}