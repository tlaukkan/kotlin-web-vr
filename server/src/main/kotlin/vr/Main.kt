package vr

import vr.network.NetworkServer
import vr.network.model.DataVector3
import vr.network.model.LightFieldNode
import java.util.*

//val CELL = Cell("Default")

val IDENTITY_STORE = IdentityStore()

val NETWORK_SERVER = NetworkServer()

fun main(args : Array<String>) {
    val serverMain = VrServer()
    serverMain.startup()

    NETWORK_SERVER.getCell("Default").addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 0.2))
    NETWORK_SERVER.getCell("Default").addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 1.0, DataVector3(0.0, 0.8, 0.0)))
}