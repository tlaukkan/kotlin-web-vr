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

//val CELL = Cell("Default")

val IDENTITY_STORE = IdentityStore()

val NETWORK_SERVER = NetworkServer()

private val log = Logger.getLogger("vr.main")

fun main(args : Array<String>) {
    val mapper = ObjectMapper(YAMLFactory())
    val string = FileUtils.readFileToString(File("servers.yaml"), Charset.forName("UTF-8"))
    val serversConfig = mapper.readValue(string, ServersConfig::class.java)

    for (serverConfig in serversConfig.servers) {
        log.info("Starting server ${serverConfig.name} at ${serverConfig.url}")
        val serverMain = VrServer(serverConfig.url)
        serverMain.startup()
        for (cellConfig in serverConfig.cells) {
            log.info("Adding cell ${cellConfig.name} to server ${serverConfig.name}")
            var cell = Cell(cellConfig.name)
            NETWORK_SERVER.addCell(cell)
            cell.addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 0.2))
            cell.addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 1.0, DataVector3(0.0, 0.8, 0.0)))

            var node = PrimitiveNode(UUID.randomUUID().toString(), "box", "textures/paree_nightmare.jpg")
            node.position.x = -5.0
            node.scale.x = 0.2
            node.scale.y = 0.2
            node.scale.z = 0.2
            cell.addNode(node)
        }

    }

/*
    val serverMain = VrServer()
    serverMain.startup()

    NETWORK_SERVER.getCell("Default").addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 0.2))
    NETWORK_SERVER.getCell("Default").addNode(LightFieldNode(UUID.randomUUID().toString(), 0xffffff, 1.0, DataVector3(0.0, 0.8, 0.0)))
    var node = PrimitiveNode(UUID.randomUUID().toString(), "box", "textures/paree_nightmare.jpg")
    node.position.x = -5.0
    node.scale.x = 0.2
    node.scale.y = 0.2
    node.scale.z = 0.2
    NETWORK_SERVER.getCell("Default").addNode(node)
    */

}