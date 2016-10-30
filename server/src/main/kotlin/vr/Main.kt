package vr

import vr.network.NetworkServer

//val CELL = Cell("Default")

val IDENTITY_STORE = IdentityStore()

val NETWORK_SERVER = NetworkServer()

fun main(args : Array<String>) {
    val serverMain = VrServer()

    serverMain.startup()
}