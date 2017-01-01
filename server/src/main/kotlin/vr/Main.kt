package vr

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import logger
import org.apache.commons.io.FileUtils
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
        val timeMillis = System.currentTimeMillis() - startTimeMillis

        val angle = (timeMillis / 20000.0) * 2 * Math.PI
        val y = Math.sin(angle) * 3
        val axisAngle = AxisAngle4d(0.0, 1.0, 0.0, angle)
        val orientation = Quat4d()
        orientation.set(axisAngle)
        val scale = (Math.sin(angle) + 2) / 2

        for (server in servers.values) {

            for (cell in server.networkServer.getCells()) {
                if (cell.remote) {
                    continue
                }

                //cell.primeNode.position.y = y

                cell.primeNode.orientation.x = orientation.x
                cell.primeNode.orientation.y = orientation.y
                cell.primeNode.orientation.z = orientation.z
                cell.primeNode.orientation.w = orientation.w

                cell.primeNode.scale.x = 0.1
                cell.primeNode.scale.y = 0.1
                cell.primeNode.scale.z = 0.1

                //cell.primeNode.scale.x = scale
                //cell.primeNode.scale.y = scale
                //cell.primeNode.scale.z = scale

                server.networkServer.processReceivedNodes(mutableListOf(cell.primeNode))
            }

        }

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
        servers[serverConfig.name] = server

        // Configuring cells according to server configuration.
        for (cellConfig in serverConfig.cells) {
            var cell = Cell("${serverConfig.uri}api/cells/${cellConfig.name}")

            server.networkServer.addCell(cell)
            log.info("Added cell ${cellConfig.name} to server ${serverConfig.name} with uri ${cell.url}")
        }

        // Setting up cell neighbours according to server configuration.
        for (cellConfig in serverConfig.cells) {
            for (neighbour in cellConfig.neighbours.keys) {
                var cell = server.networkServer.getCell("${serverConfig.uri}api/cells/${cellConfig.name}")

                val neighbourCellUri: String
                if (neighbour.contains('/')) {
                    neighbourCellUri = neighbour
                } else {
                    neighbourCellUri = "${serverConfig.uri}api/cells/$neighbour"
                }
                val neighbourVector = cellConfig.neighbours[neighbour]!!

                val remoteNeighbour = !neighbourCellUri.startsWith(serverConfig.uri)

                if (!server.networkServer.hasCell(neighbourCellUri)) {
                    if (remoteNeighbour) {
                        server.networkServer.addCell(Cell(neighbourCellUri, true))
                        log.info("Added remote neighbour cell: $neighbourCellUri")
                    } else {
                        log.warning("No such local neighbour cell: $neighbourCellUri")
                        continue
                    }
                }

                val neighbourCell = server.networkServer.getCell(neighbourCellUri)!!

                cell.neighbours[neighbourCellUri] = neighbourVector
                neighbourCell.neighbours[cell.url] = DataVector3(
                        -neighbourVector.x,
                        -neighbourVector.y,
                        -neighbourVector.z)

                log.info("Added neighbours: ${cell.url} ${cell.neighbours[neighbourCellUri]} - ${neighbourCell.url} ${neighbourCell.neighbours[cell.url]}")
            }
        }

        // Loading cell contents
        server.load(path)

        server.startup()

    }

    return servers
}