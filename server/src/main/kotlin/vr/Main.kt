package vr

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import logger
import org.apache.commons.io.FileUtils
import vr.config.ServerConfig
import vr.config.ServersConfig
import vr.network.NetworkServer
import vr.network.model.DataVector3
import vr.network.model.LightFieldNode
import vr.network.model.PrimitiveNode
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.logging.Logger
import javax.vecmath.AxisAngle4d
import javax.vecmath.Quat4d

val IDENTITY_STORE = IdentityStore()

private val log = Logger.getLogger("vr.main")

fun main(args : Array<String>) {
    var servers = configureServers(".")

    val refreshTimeMillis = 300L
    var startTimeMillis = System.currentTimeMillis()

    while (true) {
        updateCellPrimeNodes(servers, startTimeMillis)

        Thread.sleep(refreshTimeMillis)
    }

}

fun configureServers(path: String) : Map<String, VrServer> {
    val string = FileUtils.readFileToString(File("$path/servers.yaml"), Charset.forName("UTF-8"))
    val mapper = ObjectMapper(YAMLFactory())
    val serversConfig = mapper.readValue(string, ServersConfig::class.java)
    val servers: MutableMap<String, VrServer> = mutableMapOf()
    for (serverConfig in serversConfig.servers) {
        log.info("Starting server ${serverConfig.name} at ${serverConfig.uri}")

        val server = VrServer(serverConfig.uri)

        // Configure server cells and neighbouring cells
        server.configureCells(serverConfig)

        // Load server cell contents
        server.load(path)

        // Startup server
        server.startup()

        // Store server to servers map.
        servers[serverConfig.name] = server
    }

    return servers
}

private fun updateCellPrimeNodes(servers: Map<String, VrServer>, startTimeMillis: Long) {
    val timeMillis = System.currentTimeMillis() - startTimeMillis

    val angle = (timeMillis / 20000.0) * 2 * Math.PI
    val axisAngle = AxisAngle4d(0.0, 1.0, 0.0, angle)
    val orientation = Quat4d()
    orientation.set(axisAngle)

    for (server in servers.values) {

        for (cell in server.networkServer.getCells()) {
            if (cell.remote) {
                continue
            }

            cell.primeNode.orientation.x = orientation.x
            cell.primeNode.orientation.y = orientation.y
            cell.primeNode.orientation.z = orientation.z
            cell.primeNode.orientation.w = orientation.w

            cell.primeNode.scale.x = 0.1
            cell.primeNode.scale.y = 0.1
            cell.primeNode.scale.z = 0.1

            server.networkServer.processReceivedNodes(mutableListOf(cell.primeNode))
        }

    }
}