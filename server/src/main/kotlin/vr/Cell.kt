package vr

import vr.network.model.Node
import java.net.URI
import java.util.*

/**
 * Created by tlaukkan on 7/9/2016.
 */
class Cell {
    private val nodes: MutableMap<String, Node> = HashMap()

    @Synchronized fun addNode(node: Node) : Boolean {
        if (nodes.containsKey(node.url)) {
            return false
        }
        nodes[node.url] = node
        return true
    }

    @Synchronized fun updateNode(node: Node) : Boolean {
        if (!nodes.containsKey(node.url)) {
            return false
        }
        nodes[node.url] = node
        return true
    }

    @Synchronized fun removeNode(url: String) : Boolean {
        if (!nodes.containsKey(url)) {
            return false
        }
        nodes.remove(url)
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