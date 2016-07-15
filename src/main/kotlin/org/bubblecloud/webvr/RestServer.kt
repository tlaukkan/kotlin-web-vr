package org.bubblecloud.webvr

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.websockets.WebSocketAddOn
import org.glassfish.grizzly.websockets.WebSocketEngine
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import java.net.URI

import javax.ws.rs.core.UriBuilder

class RestServer(val url: String = "http://localhost:8080/") {
    val uri: URI = UriBuilder.fromUri(url).build()
    val config = ResourceConfig(NodesResource::class.java)
    val server: HttpServer = GrizzlyHttpServerFactory.createHttpServer(uri, config, false, null, false)
    val addon = WebSocketAddOn()

    init {
    }

    fun startup(): Unit {
        server.getListener("grizzly").registerAddOn(addon)
        val webSocketApplication = WebVrWebSocketApplication()
        WebSocketEngine.getEngine().register("/ws", "/echo", webSocketApplication)
        server.start()
    }

    fun shutdown(): Unit {
        server.shutdownNow()
    }

}
