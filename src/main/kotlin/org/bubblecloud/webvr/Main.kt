package org.bubblecloud.webvr

val CELL = Cell()

val NETWORK = Network()

fun main(args : Array<String>) {
    val serverMain = VrServer()
    serverMain.startup()
}