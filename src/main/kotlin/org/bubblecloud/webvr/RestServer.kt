package org.bubblecloud.webvr

import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import org.glassfish.jersey.server.ResourceConfig
import java.net.URI

import javax.ws.rs.core.UriBuilder

class RestServer(val url: String = "http://localhost:8080/") {
    val uri: URI = UriBuilder.fromUri(url).build()
    val config = ResourceConfig(NodesResource::class.java)
    val server: HttpServer = GrizzlyHttpServerFactory.createHttpServer(uri, config)

    fun startup(): Unit {
        server.start()
    }

    fun shutdown(): Unit {
        server.shutdownNow()
    }

}
