package vr

import vr.network.NetworkServer
import vr.network.model.DataVector3
import vr.network.model.LightFieldNode
import vr.network.model.PrimitiveNode
import java.util.*

//val CELL = Cell("Default")

val IDENTITY_STORE = IdentityStore()

val NETWORK_SERVER = NetworkServer()

fun main(args : Array<String>) {
    val serverMain = VrServer()
    serverMain.startup()

    NETWORK_SERVER.getCell("Default").addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 0.2))
    NETWORK_SERVER.getCell("Default").addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 1.0, DataVector3(0.0, 0.8, 0.0)))
    var node = PrimitiveNode(UUID.randomUUID().toString(), "box", "textures/paree_nightmare.jpg")
    node.position.x = -5.0
    node.scale.x = 0.2
    node.scale.y = 0.2
    node.scale.z = 0.2
    NETWORK_SERVER.getCell("Default").addNode(node)

}