package org.bubblecloud.webvr

import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder
import logger
import org.glassfish.grizzly.http.server.HttpHandlerRegistration
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.server.NetworkListener
import org.glassfish.grizzly.ssl.SSLEngineConfigurator
import org.glassfish.grizzly.websockets.WebSocketAddOn
import org.glassfish.grizzly.websockets.WebSocketEngine
import org.bubblecloud.webvr.grizzly.GrizzlyHttpContainer
import org.glassfish.grizzly.http.server.StaticHttpHandler
import org.glassfish.jersey.grizzly2.httpserver.internal.LocalizationMessages
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spi.Container
import java.io.IOException
import java.net.URI
import javax.ws.rs.ProcessingException

import javax.ws.rs.core.UriBuilder

class VrServer(val url: String = "http://localhost:8080/") {
    private val log = logger()

    val config = ResourceConfig(NodeRestService::class.java)
    val server: HttpServer = createHttpServer( UriBuilder.fromUri(url + "api").build(), GrizzlyHttpContainer(config), false, null, false)

    init {
        System.setProperty("java.util.logging.SimpleFormatter.format","%1\$tT [%4\$s] %5\$s %6\$s %n")
    }

    fun startup(): Unit {
        log.info("VR server startup...")

        server.serverConfiguration.addHttpHandler(StaticHttpHandler("out/resources/main/webapp"))
        server.getListener("grizzly").registerAddOn(WebSocketAddOn())
        val wsListener = WebSocketListener()
        WebSocketEngine.getEngine().register("", "/ws", wsListener)

        server.getListener("grizzly").getFileCache().setEnabled(false);
        server.start()
        log.info("VR server startup.")
    }

    fun shutdown(): Unit {
        log.info("VR server shutdown...")
        server.shutdownNow()
        log.info("VR server shutdown.")
    }

    fun createHttpServer(uri: URI,
                         handler: GrizzlyHttpContainer?,
                         secure: Boolean,
                         sslEngineConfigurator: SSLEngineConfigurator?,
                         start: Boolean): HttpServer {

        val host = if (uri.host == null) NetworkListener.DEFAULT_NETWORK_HOST else uri.host
        val port = if (uri.port == -1)
            if (secure) Container.DEFAULT_HTTP_PORT else Container.DEFAULT_HTTPS_PORT
        else
            uri.port

        val listener = NetworkListener("grizzly", host, port)

        listener.transport.workerThreadPoolConfig.threadFactory = ThreadFactoryBuilder().setNameFormat("grizzly-http-server-%d").setUncaughtExceptionHandler(JerseyProcessingUncaughtExceptionHandler()).build()

        listener.isSecure = secure
        if (sslEngineConfigurator != null) {
            listener.setSSLEngineConfig(sslEngineConfigurator)
        }

        val server = HttpServer()
        server.addListener(listener)

        // Map the path to the processor.
        val config = server.serverConfiguration
        if (handler != null) {
            val path = uri.path.replace("/{2,}".toRegex(), "/")

            val contextPath = if (path.endsWith("/")) path.substring(0, path.length - 1) else path
            config.addHttpHandler(handler, HttpHandlerRegistration.bulder().contextPath(contextPath).build())
        }

        config.isPassTraceRequest = true
        config.defaultQueryEncoding = org.glassfish.grizzly.utils.Charsets.UTF8_CHARSET

        if (start) {
            try {
                // Start the server.
                server.start()
            } catch (ex: IOException) {
                server.shutdownNow()
                throw ProcessingException(LocalizationMessages.FAILED_TO_START_SERVER(ex.message), ex)
            }

        }

        return server
    }

}
