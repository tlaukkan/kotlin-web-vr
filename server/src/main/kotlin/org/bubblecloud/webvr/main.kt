package org.bubblecloud.webvr

import org.bubblecloud.webvr.model.Cell

val CELL = Cell()

val NETWORK = Network()

fun main(args : Array<String>) {
    val serverMain = VrServer()
    serverMain.startup()
}