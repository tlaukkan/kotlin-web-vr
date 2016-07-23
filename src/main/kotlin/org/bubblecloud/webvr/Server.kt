package org.bubblecloud.webvr

import logger
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.websockets.WebSocketAddOn
import org.glassfish.grizzly.websockets.WebSocketEngine
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import java.net.URI

import javax.ws.rs.core.UriBuilder

class Server(val url: String = "http://localhost:8080/") {
    private val log = logger()

    val uri: URI = UriBuilder.fromUri(url).build()
    val config = ResourceConfig(NodesResource::class.java)
    val server: HttpServer = GrizzlyHttpServerFactory.createHttpServer(uri, config, false, null, false)
    val addon = WebSocketAddOn()

    init {
        System.setProperty("java.util.logging.SimpleFormatter.format","%4\$s: %5\$s%n")
    }

    fun startup(): Unit {
        log.info("VR server startup.")
        server.getListener("grizzly").registerAddOn(addon)
        val webSocketApplication = WebVrWebSocketApplication()
        WebSocketEngine.getEngine().register("/ws", "/echo", webSocketApplication)
        server.start()
        log.info("VR server started.")
    }

    fun shutdown(): Unit {
        server.shutdownNow()
        log.info("VR server shutdown.")
    }

}
