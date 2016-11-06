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

val IDENTITY_STORE = IdentityStore()

private val log = Logger.getLogger("vr.main")

fun main(args : Array<String>) {
    val string = FileUtils.readFileToString(File("servers.yaml"), Charset.forName("UTF-8"))
    var servers = configureServers(string)
}

fun configureServers(string: String?) : Map<String, VrServer> {
    val mapper = ObjectMapper(YAMLFactory())
    val serversConfig = mapper.readValue(string, ServersConfig::class.java)
    val servers: MutableMap<String, VrServer> = mutableMapOf()
    for (serverConfig in serversConfig.servers) {
        log.info("Starting server ${serverConfig.name} at ${serverConfig.uri}")
        val server = VrServer(serverConfig.uri)
        servers[serverConfig.name] = server
        server.startup()
        for (cellConfig in serverConfig.cells) {
            var cell = Cell("${serverConfig.uri}api/cells/${cellConfig.name}")

            server.networkServer.addCell(cell)
            log.info("Added cell ${cellConfig.name} to server ${serverConfig.name} with uri ${cell.cellUri}")

            for (node in cellConfig.primitives) {
                node.url = serverConfig.uri + "/nodes/" + node.id
                cell.addNode(node)
            }

            for (node in cellConfig.lightFields) {
                node.url = serverConfig.uri + "/nodes/" + node.id
                cell.addNode(node)
            }

        }

        for (cellConfig in serverConfig.cells) {
            for (neighbour in cellConfig.neighbours.keys) {
                var cell = Cell("${serverConfig.uri}api/cells/${cellConfig.name}")

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
                neighbourCell.neighbours[cell.cellUri] = DataVector3(
                        -neighbourVector.x,
                        -neighbourVector.y,
                        -neighbourVector.z)

                log.info("Added neighbours: ${cell.cellUri} ${cell.neighbours[neighbourCellUri]} - ${neighbourCell.cellUri} ${neighbourCell.neighbours[cell.cellUri]}")
            }

        }

    }

    return servers

}