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
    var servers = configureServers(".")
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

        for (cellConfig in serverConfig.cells) {
            var cell = Cell("${serverConfig.uri}api/cells/${cellConfig.name}")

            server.networkServer.addCell(cell)
            log.info("Added cell ${cellConfig.name} to server ${serverConfig.name} with uri ${cell.cellUri}")
        }

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
                neighbourCell.neighbours[cell.cellUri] = DataVector3(
                        -neighbourVector.x,
                        -neighbourVector.y,
                        -neighbourVector.z)

                log.info("Added neighbours: ${cell.cellUri} ${cell.neighbours[neighbourCellUri]} - ${neighbourCell.cellUri} ${neighbourCell.neighbours[cell.cellUri]}")
            }
        }

        server.load(path)

        server.startup()

    }

    return servers
}