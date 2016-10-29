package vr

import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder
import logger
import org.glassfish.grizzly.http.server.HttpHandlerRegistration
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.server.NetworkListener
import org.glassfish.grizzly.ssl.SSLEngineConfigurator
import org.glassfish.grizzly.websockets.WebSocketAddOn
import org.glassfish.grizzly.websockets.WebSocketEngine
import vr.util.grizzly.GrizzlyHttpContainer
import org.glassfish.grizzly.http.server.StaticHttpHandler
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spi.Container
import vr.network.WebSocketListener
import java.net.URI

import javax.ws.rs.core.UriBuilder

class VrServer(val url: String = "http://localhost:8080/") {
    private val log = logger()

    val server: HttpServer = createHttpServer( UriBuilder.fromUri(url).build(), false, null)

    init {
        System.setProperty("java.util.logging.SimpleFormatter.format","%1\$tT [%4\$s] %5\$s %6\$s %n")
    }

    fun startup(): Unit {
        log.info("VR server startup...")
        server.start()
        log.info("VR server startup.")
    }

    fun shutdown(): Unit {
        log.info("VR server shutdown...")
        server.shutdownNow()
        log.info("VR server shutdown.")
    }

    fun createHttpServer(uri: URI,
                         secure: Boolean,
                         sslEngineConfigurator: SSLEngineConfigurator?): HttpServer {

        val host = if (uri.host == null) NetworkListener.DEFAULT_NETWORK_HOST else uri.host
        val port = if (uri.port == -1)
            if (secure) Container.DEFAULT_HTTP_PORT else Container.DEFAULT_HTTPS_PORT
        else
            uri.port

        val listener = NetworkListener("grizzly", host, port)

        listener.transport.workerThreadPoolConfig.threadFactory = ThreadFactoryBuilder().setNameFormat(
                "grizzly-http-server-%d").setUncaughtExceptionHandler(
                JerseyProcessingUncaughtExceptionHandler()).build()

        listener.isSecure = secure
        if (sslEngineConfigurator != null) {
            listener.setSSLEngineConfig(sslEngineConfigurator)
        }

        val server = HttpServer()
        server.addListener(listener)

        val config = server.serverConfiguration

        // Add static HTTP handler.
        config.addHttpHandler(StaticHttpHandler("out/resources/main/webapp"))

        // Add Jersey REST HTTP handler.
        val restHandler = GrizzlyHttpContainer(ResourceConfig(NodeRestService::class.java))
        config.addHttpHandler(restHandler, HttpHandlerRegistration.builder().contextPath("/api").build())


        // Add web socket support.
        server.getListener("grizzly").registerAddOn(WebSocketAddOn())
        val wsListener = WebSocketListener()
        WebSocketEngine.getEngine().register("", "/ws", wsListener)

        server.getListener("grizzly").getFileCache().setEnabled(false);

        config.isPassTraceRequest = true
        config.defaultQueryEncoding = org.glassfish.grizzly.utils.Charsets.UTF8_CHARSET

        return server
    }

}
