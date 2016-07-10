package org.bubblecloud.webvr

val CELL = Cell()

fun main(args : Array<String>) {
    val serverMain = RestServer()
    serverMain.startup()
}