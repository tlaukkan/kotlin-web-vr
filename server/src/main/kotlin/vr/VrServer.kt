package vr

import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder
import logger
import org.apache.commons.io.FileUtils
import org.glassfish.grizzly.http.server.HttpHandlerRegistration
import org.glassfish.grizzly.http.server.HttpServer
import org.glassfish.grizzly.http.server.NetworkListener
import org.glassfish.grizzly.ssl.SSLEngineConfigurator
import org.glassfish.grizzly.websockets.WebSocketAddOn
import vr.util.grizzly.GrizzlyHttpContainer
import org.glassfish.grizzly.http.server.StaticHttpHandler
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.spi.Container
import vr.network.NetworkLinker
import vr.network.NetworkServer
import vr.network.WebSocketListener
import vr.network.model.Envelope
import vr.network.model.HandshakeResponse
import vr.network.model.Node
import vr.util.Mapper
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.util.*

import javax.ws.rs.core.UriBuilder

val WEB_SOCKET_LISTENER = WebSocketListener()

val PORT_NETWORK_SERVER_MAP: MutableMap<Int, NetworkServer> = HashMap()


class VrServer(val url: String = "http://localhost:8080/") {
    private val log = logger()

    val networkServer = NetworkServer(this)
    val networkLinker = NetworkLinker(networkServer)
    val server: HttpServer = createHttpServer( UriBuilder.fromUri(url).build(), false, null)

    init {
        System.setProperty("java.util.logging.SimpleFormatter.format","%1\$tT [%4\$s] %5\$s %6\$s %n")
    }

    fun startup(): Unit {
        //NETWORK_SERVER.addCell(Cell("Default"))

        log.info("VR server startup...")
        server.start()
        networkLinker.startup()
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
        val restHandler = GrizzlyHttpContainer(ResourceConfig(NodeRestService::class.java, TextureRestService::class.java))
        config.addHttpHandler(restHandler, HttpHandlerRegistration.builder().contextPath("/api").build())


        // Add web socket support.
        server.getListener("grizzly").registerAddOn(WebSocketAddOn())
        //val wsListener = WebSocketListener(networkServer)
        PORT_NETWORK_SERVER_MAP[uri.port] = networkServer

        server.getListener("grizzly").getFileCache().setEnabled(false);

        config.isPassTraceRequest = true
        config.defaultQueryEncoding = org.glassfish.grizzly.utils.Charsets.UTF8_CHARSET

        return server
    }

    fun save() {
        log.info("Saving cell nodes.")
        val serverUrl: URL = URL(url)
        val dataDirectory = File("data/${serverUrl.host.replace('.','_')}_${serverUrl.port}")
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs()
        }

        val mapper = Mapper()
        for (cell in networkServer.getCells()) {
            if (cell.remote) {
                continue
            }
            val cellName = cell.url.substring(cell.url.lastIndexOf('/') + 1)
            val cellDirectory = File("${dataDirectory.absolutePath}/${cellName}")
            cellDirectory.mkdirs()

            deleteDirectoryContent(cellDirectory)

            for (node in cell.getNodes()) {
                // Do not save volatile nodes
                if (node.volatile) {
                    continue
                }
                val nodeId = node.url.substring(node.url.lastIndexOf('/') + 1)
                val nodeType = mapper.getValueType(node)
                val nodeString = mapper.writeValue(node, true)
                val nodeFile = File("${cellDirectory.absolutePath}/${nodeType}_${nodeId}.json")
                FileUtils.writeStringToFile(nodeFile, nodeString, Charset.forName("UTF-8"))
            }
        }
    }

    fun deleteDirectoryContent(folder: File) {
        val files = folder.listFiles()
        if (files != null) { //some JVMs return null for empty dirs
            for (f in files) {
                if (f.isDirectory) {
                    deleteDirectoryContent(f)
                } else {
                    f.delete()
                }
            }
        }
    }

    fun load(path: String) {
        val serverUrl: URL = URL(url)
        val dataDirectory = File("$path/data/${serverUrl.host.replace('.','_')}_${serverUrl.port}")
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs()
        }

        val mapper = Mapper()
        for (cell in networkServer.getCells()) {
            if (cell.remote) {
                continue
            }
            val cellName = cell.url.substring(cell.url.lastIndexOf('/') + 1)
            val cellDirectory = File("${dataDirectory.absolutePath}/${cellName}")
            cellDirectory.mkdirs()

            val nodeFiles = cellDirectory.listFiles()

            for (nodeFile in nodeFiles) {
                val nodeType = nodeFile.name.split('_')[0]
                val nodeString = FileUtils.readFileToString(nodeFile, Charset.forName("UTF-8"))
                val node = mapper.readValue(nodeString, nodeType) as Node
                cell.addNode(node)
                log.info("Cell ${cellName} loaded ${node.url} of type ${node.javaClass.simpleName}")
            }

        }
    }

}
