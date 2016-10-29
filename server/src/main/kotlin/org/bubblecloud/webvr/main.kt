package org.bubblecloud.webvr

import org.bubblecloud.webvr.network.NetworkServer

val CELL = Cell()

val IDENTITY_STORE = IdentityStore()

val NETWORK_SERVER = NetworkServer()

fun main(args : Array<String>) {
    val serverMain = VrServer()
    serverMain.startup()
}