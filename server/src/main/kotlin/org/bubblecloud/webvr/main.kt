package org.bubblecloud.webvr

val CELL = Cell()

val IDENTITY_STORE = IdentityStore()

val NETWORK = Network()

fun main(args : Array<String>) {
    val serverMain = VrServer()
    serverMain.startup()
}