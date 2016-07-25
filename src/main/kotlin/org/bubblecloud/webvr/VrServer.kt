package org.bubblecloud.webvr

import logger
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.websockets.WebSocketAddOn
import org.glassfish.grizzly.websockets.WebSocketEngine
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import java.net.URI

import javax.ws.rs.core.UriBuilder

class VrServer(val url: String = "http://localhost:8080/") {
    private val log = logger()

    val uri: URI = UriBuilder.fromUri(url).build()
    val config = ResourceConfig(NodeRestService::class.java)
    val server: HttpServer = GrizzlyHttpServerFactory.createHttpServer(uri, config, false, null, false)
    val addon = WebSocketAddOn()

    init {
        System.setProperty("java.util.logging.SimpleFormatter.format","%1\$tT [%4\$s] %5\$s %6\$s %n")
    }

    fun startup(): Unit {
        log.info("VR server startup...")
        server.getListener("grizzly").registerAddOn(addon)
        val wsListener = WebSocketListener()
        WebSocketEngine.getEngine().register("", "/ws", wsListener)
        server.start()
        log.info("VR server startup.")
    }

    fun shutdown(): Unit {
        log.info("VR server shutdown...")
        server.shutdownNow()
        log.info("VR server shutdown.")
    }

}
